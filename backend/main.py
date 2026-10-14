import os
import bcrypt
import pymysql
import pymysql.cursors
from datetime import datetime, date
from typing import List, Optional, Dict, Any
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request, HTTPException, Form, Cookie, status, Depends
from fastapi.responses import HTMLResponse, RedirectResponse, JSONResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from pydantic import BaseModel

env_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), ".env")
if os.path.exists(env_path):
    with open(env_path, "r") as f:
        for line in f:
            if "=" in line and not line.strip().startswith("#"):
                k, v = line.strip().split("=", 1)
                os.environ[k.strip()] = v.strip()

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("DB_NAME", "foodnow_db")

def get_db_connection(db_name: Optional[str] = DB_NAME):
    passwords = [DB_PASSWORD, "", "root", "123456", "password", "admin", "12345"]
    passwords = list(dict.fromkeys(passwords))

    last_err = None
    for pwd in passwords:
        try:
            conn = pymysql.connect(
                host=DB_HOST,
                user=DB_USER,
                password=pwd,
                database=db_name,
                charset="utf8mb4",
                autocommit=True,
                cursorclass=pymysql.cursors.DictCursor
            )
            return conn
        except pymysql.err.OperationalError as e:
            last_err = e
            if e.args[0] != 1045:
                raise e
    print("\n[LỖI KẾT NỐI MYSQL]: Access Denied (1045). Vui lòng mở file backend/.env và cấu hình đúng DB_PASSWORD.\n")
    raise last_err

def format_dates(obj: Any) -> Any:
    if isinstance(obj, list):
        return [format_dates(item) for item in obj]
    if isinstance(obj, dict):
        new_dict = {}
        for k, v in obj.items():
            if isinstance(v, (datetime, date)):
                new_dict[k] = v.strftime("%Y-%m-%d %H:%M:%S")
            elif hasattr(v, '__float__') and not isinstance(v, (int, float)):
                new_dict[k] = float(v)
            elif isinstance(v, (list, dict)):
                new_dict[k] = format_dates(v)
            else:
                new_dict[k] = v
        return new_dict
    return obj

def init_db():
    passwords = [DB_PASSWORD, "", "root", "123456", "password", "admin", "12345"]
    passwords = list(dict.fromkeys(passwords))

    conn = None
    last_err = None
    for pwd in passwords:
        try:
            conn = pymysql.connect(
                host=DB_HOST,
                user=DB_USER,
                password=pwd,
                charset="utf8mb4",
                autocommit=True
            )
            break
        except pymysql.err.OperationalError as e:
            last_err = e
            if e.args[0] != 1045:
                raise e
    if not conn:
        print("\n[LỖI KẾT NỐI MYSQL]: Access Denied (1045). Vui lòng mở file backend/.env và cấu hình đúng DB_PASSWORD.\n")
        raise last_err

    with conn.cursor() as cursor:
        cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{DB_NAME}` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
    conn.close()

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                full_name VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                phone VARCHAR(50),
                address TEXT,
                role VARCHAR(20) DEFAULT 'customer',
                status VARCHAR(20) DEFAULT 'active',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """)

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS restaurants (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                address TEXT,
                open_hours VARCHAR(100),
                logo VARCHAR(500),
                cover VARCHAR(500),
                status VARCHAR(20) DEFAULT 'active',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """)

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS foods (
                id INT AUTO_INCREMENT PRIMARY KEY,
                restaurant_id INT NOT NULL,
                name VARCHAR(255) NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                image VARCHAR(500),
                status VARCHAR(20) DEFAULT 'available',
                FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """)

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS orders (
                id INT AUTO_INCREMENT PRIMARY KEY,
                customer_id INT NOT NULL,
                restaurant_id INT NOT NULL,
                delivery_address TEXT,
                payment_method VARCHAR(50),
                total_amount DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) DEFAULT 'pending',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """)

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS order_items (
                id INT AUTO_INCREMENT PRIMARY KEY,
                order_id INT NOT NULL,
                food_id INT NOT NULL,
                quantity INT NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        """)

        demo_accounts = [
            ("FoodNow User", "user@foodnow.com", "123456", "0901234567", "123 Lê Lợi, Q.1, TP.HCM", "customer"),
            ("FoodNow Admin", "admin@foodnow.com", "123456", "0909999999", "Quản trị viên FoodNow", "admin")
        ]

        for full_name, email, plain_pass, phone, address, role in demo_accounts:
            cursor.execute("SELECT id FROM users WHERE email = %s", (email,))
            if not cursor.fetchone():
                hashed_pass = bcrypt.hashpw(plain_pass.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
                cursor.execute("""
                    INSERT INTO users (full_name, email, password, phone, address, role, status)
                    VALUES (%s, %s, %s, %s, %s, %s, 'active')
                """, (full_name, email, hashed_pass, phone, address, role))

        cursor.execute("DELETE FROM restaurants WHERE name IN ('Bánh Mì Phượng', 'Phở Sài Gòn', 'Cơm Tấm Sài Gòn 18', 'Trà Sữa & Ăn Vặt FoodNow')")

        restaurants_data = [
            ("Circle K", "123 Nguyen Hue St, District 1", "00:00 - 23:59", [
                ("Classic Hot Dog", 25000),
                ("Cheese Burger", 35000),
                ("Mixed Noodles", 25000),
                ("Soft Drink", 12000)
            ]),
            ("Highland Coffee", "456 Le Loi St, District 1", "07:00 - 22:00", [
                ("Premium Milk Coffee", 39000),
                ("Matcha Latte", 49000),
                ("Strawberry Latte", 49000),
                ("Lemon Mousse Cake", 35000),
                ("Black Coffee", 29000)
            ]),
            ("MiniStop", "789 CMT8 St, District 3", "00:00 - 23:59", [
                ("Osaka Takoyaki", 30000),
                ("Spicy Tokbokki", 35000),
                ("Shrimp Burger", 32000),
                ("Special Burger", 40000)
            ]),
            ("7 Eleven", "101 Ton Duc Thang St, District 1", "00:00 - 23:59", [
                ("Club Sandwich", 28000),
                ("Spaghetti Meatball", 45000),
                ("Beef Tacos", 35000),
                ("Saigon Banh Mi", 30000)
            ]),
            ("VinMart", "202 Pham Ngu Lao St, District 1", "08:00 - 22:00", [
                ("Grilled Sweet Potato", 20000),
                ("Fresh Egg Tart", 15000),
                ("Pre-cut Tropical Fruit", 25000),
                ("Healthy Bowl", 45000)
            ])
        ]

        for r_name, r_addr, r_hours, foods in restaurants_data:
            cursor.execute("SELECT id FROM restaurants WHERE name = %s", (r_name,))
            res_row = cursor.fetchone()
            if not res_row:
                cursor.execute("""
                    INSERT INTO restaurants (name, address, open_hours, logo, cover, status)
                    VALUES (%s, %s, %s, '', '', 'active')
                """, (r_name, r_addr, r_hours))
                r_id = cursor.lastrowid

                for f_name, f_price in foods:
                    cursor.execute("""
                        INSERT INTO foods (restaurant_id, name, price, image, status)
                        VALUES (%s, %s, %s, '', 'available')
                    """, (r_id, f_name, f_price))
            else:
                r_id = res_row['id']
                for f_name, f_price in foods:
                    cursor.execute("SELECT id FROM foods WHERE restaurant_id = %s AND name = %s", (r_id, f_name))
                    if not cursor.fetchone():
                        cursor.execute("""
                            INSERT INTO foods (restaurant_id, name, price, image, status)
                            VALUES (%s, %s, %s, '', 'available')
                        """, (r_id, f_name, f_price))

    db.close()

@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    yield

app = FastAPI(title="FoodNow Backend API & Admin Web", lifespan=lifespan)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
app.mount("/static", StaticFiles(directory=os.path.join(BASE_DIR, "static")), name="static")
templates = Jinja2Templates(directory=os.path.join(BASE_DIR, "templates"))

class RegisterModel(BaseModel):
    full_name: str
    email: str
    password: str
    phone: Optional[str] = ""
    address: Optional[str] = ""

class LoginModel(BaseModel):
    email: str
    password: str

class ForgotPasswordModel(BaseModel):
    email: str
    new_password: str

class UpdateProfileModel(BaseModel):
    full_name: str
    phone: Optional[str] = ""
    address: Optional[str] = ""

class ChangePasswordModel(BaseModel):
    old_password: str
    new_password: str

class OrderItemSchema(BaseModel):
    food_id: int
    quantity: int
    price: float

class CreateOrderModel(BaseModel):
    customer_id: int
    restaurant_id: int
    delivery_address: str
    payment_method: str
    total_amount: float
    items: List[OrderItemSchema]

def get_current_admin(admin_session: Optional[str] = Cookie(None)):
    if not admin_session:
        return None
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id, email, role FROM users WHERE email = %s AND role = 'admin' AND status = 'active'", (admin_session,))
        admin = cursor.fetchone()
    db.close()
    return format_dates(admin)

@app.get("/")
def root():
    return {"message": "Welcome to FoodNow Backend API", "status": "ok", "docs": "/docs", "admin": "/admin/login"}

@app.post("/register")
def register_user(data: RegisterModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id FROM users WHERE email = %s", (data.email,))
        if cursor.fetchone():
            db.close()
            raise HTTPException(status_code=400, detail="Email đã tồn tại trên hệ thống")

        hashed_pass = bcrypt.hashpw(data.password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        cursor.execute("""
            INSERT INTO users (full_name, email, password, phone, address, role, status)
            VALUES (%s, %s, %s, %s, %s, 'customer', 'active')
        """, (data.full_name, data.email, hashed_pass, data.phone, data.address))
        user_id = cursor.lastrowid

    db.close()
    return {
        "status": "success",
        "message": "Đăng ký tài khoản thành công",
        "user": {
            "id": user_id,
            "full_name": data.full_name,
            "email": data.email,
            "phone": data.phone,
            "address": data.address,
            "role": "customer"
        }
    }

@app.post("/login")
def login_user(data: LoginModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT * FROM users WHERE email = %s", (data.email,))
        user = cursor.fetchone()

    db.close()

    if not user:
        raise HTTPException(status_code=400, detail="Email hoặc mật khẩu không chính xác")

    if user['status'] != 'active':
        raise HTTPException(status_code=403, detail="Tài khoản đã bị khóa")

    if not bcrypt.checkpw(data.password.encode('utf-8'), user['password'].encode('utf-8')):
        raise HTTPException(status_code=400, detail="Email hoặc mật khẩu không chính xác")

    return {
        "status": "success",
        "message": "Đăng nhập thành công",
        "user": {
            "id": user['id'],
            "full_name": user['full_name'],
            "email": user['email'],
            "phone": user['phone'],
            "address": user['address'],
            "role": user['role']
        }
    }

@app.post("/forgot-password")
def forgot_password(data: ForgotPasswordModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id FROM users WHERE email = %s", (data.email,))
        user = cursor.fetchone()
        if not user:
            db.close()
            raise HTTPException(status_code=404, detail="Email không tồn tại trong hệ thống")

        hashed_pass = bcrypt.hashpw(data.new_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        cursor.execute("UPDATE users SET password = %s WHERE email = %s", (hashed_pass, data.email))

    db.close()
    return {"status": "success", "message": "Đặt lại mật khẩu thành công"}

@app.get("/profile/{user_id}")
def get_profile(user_id: int):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id, full_name, email, phone, address, role, status, created_at FROM users WHERE id = %s", (user_id,))
        user = cursor.fetchone()

    db.close()

    if not user:
        raise HTTPException(status_code=404, detail="Không tìm thấy người dùng")

    return {"status": "success", "user": format_dates(user)}

@app.put("/profile/{user_id}")
def update_profile(user_id: int, data: UpdateProfileModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id FROM users WHERE id = %s", (user_id,))
        if not cursor.fetchone():
            db.close()
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng")

        cursor.execute("""
            UPDATE users SET full_name = %s, phone = %s, address = %s WHERE id = %s
        """, (data.full_name, data.phone, data.address, user_id))

    db.close()
    return {"status": "success", "message": "Cập nhật thông tin thành công"}

@app.put("/change-password/{user_id}")
def change_password(user_id: int, data: ChangePasswordModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT password FROM users WHERE id = %s", (user_id,))
        user = cursor.fetchone()

        if not user:
            db.close()
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng")

        if not bcrypt.checkpw(data.old_password.encode('utf-8'), user['password'].encode('utf-8')):
            db.close()
            raise HTTPException(status_code=400, detail="Mật khẩu cũ không chính xác")

        hashed_new_pass = bcrypt.hashpw(data.new_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        cursor.execute("UPDATE users SET password = %s WHERE id = %s", (hashed_new_pass, user_id))

    db.close()
    return {"status": "success", "message": "Đổi mật khẩu thành công"}

@app.get("/restaurants")
def get_restaurants():
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT * FROM restaurants WHERE status = 'active' ORDER BY id DESC")
        restaurants = cursor.fetchall()
    db.close()
    return {"status": "success", "restaurants": format_dates(restaurants)}

@app.get("/restaurants/{restaurant_id}/foods")
def get_restaurant_foods(restaurant_id: int):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT * FROM foods WHERE restaurant_id = %s AND status = 'available' ORDER BY id DESC", (restaurant_id,))
        foods = cursor.fetchall()
    db.close()
    return {"status": "success", "foods": format_dates(foods)}

@app.post("/orders")
def create_order(data: CreateOrderModel):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            INSERT INTO orders (customer_id, restaurant_id, delivery_address, payment_method, total_amount, status)
            VALUES (%s, %s, %s, %s, %s, 'pending')
        """, (data.customer_id, data.restaurant_id, data.delivery_address, data.payment_method, data.total_amount))
        order_id = cursor.lastrowid

        for item in data.items:
            cursor.execute("""
                INSERT INTO order_items (order_id, food_id, quantity, price)
                VALUES (%s, %s, %s, %s)
            """, (order_id, item.food_id, item.quantity, item.price))

    db.close()
    return {"status": "success", "message": "Đặt hàng thành công", "order_id": order_id}

@app.get("/orders/{order_id}")
def get_order_detail(order_id: int):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            SELECT o.*, u.full_name as customer_name, u.phone as customer_phone, r.name as restaurant_name
            FROM orders o
            JOIN users u ON o.customer_id = u.id
            JOIN restaurants r ON o.restaurant_id = r.id
            WHERE o.id = %s
        """, (order_id,))
        order = cursor.fetchone()

        if not order:
            db.close()
            raise HTTPException(status_code=404, detail="Không tìm thấy đơn hàng")

        cursor.execute("""
            SELECT oi.*, f.name as food_name, f.image as food_image
            FROM order_items oi
            JOIN foods f ON oi.food_id = f.id
            WHERE oi.order_id = %s
        """, (order_id,))
        items = cursor.fetchall()

    db.close()
    order = format_dates(order)
    order['items'] = format_dates(items)
    return {"status": "success", "order": order}

@app.get("/orders/customer/{customer_id}")
def get_customer_orders(customer_id: int):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT o.*, r.name as restaurant_name FROM orders o JOIN restaurants r ON o.restaurant_id = r.id WHERE o.customer_id = %s ORDER BY o.id DESC", (customer_id,))
        orders = cursor.fetchall()
    db.close()
    return {"status": "success", "orders": format_dates(orders)}

@app.get("/admin/login", response_class=HTMLResponse)
def admin_login_page(request: Request):
    return templates.TemplateResponse(request=request, name="login.html")

@app.post("/admin/login")
def admin_login(request: Request, email: str = Form(...), password: str = Form(...)):
    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT * FROM users WHERE email = %s AND role = 'admin'", (email,))
        admin = cursor.fetchone()
    db.close()

    if not admin or not bcrypt.checkpw(password.encode('utf-8'), admin['password'].encode('utf-8')):
        return templates.TemplateResponse(request=request, name="login.html", context={"error": "Email hoặc mật khẩu Admin không đúng"})

    response = RedirectResponse(url="/admin/dashboard", status_code=status.HTTP_303_SEE_OTHER)
    response.set_cookie(key="admin_session", value=admin['email'], httponly=True)
    return response

@app.get("/admin/logout")
def admin_logout():
    response = RedirectResponse(url="/admin/login", status_code=status.HTTP_303_SEE_OTHER)
    response.delete_cookie("admin_session")
    return response

@app.get("/admin/dashboard", response_class=HTMLResponse)
def admin_dashboard(request: Request, admin=Depends(get_current_admin)):
    if not admin:
        return RedirectResponse(url="/admin/login")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT COUNT(*) as u_cnt FROM users")
        u_cnt = cursor.fetchone()['u_cnt']

        cursor.execute("SELECT COUNT(*) as r_cnt FROM restaurants")
        r_cnt = cursor.fetchone()['r_cnt']

        cursor.execute("SELECT COUNT(*) as f_cnt FROM foods")
        f_cnt = cursor.fetchone()['f_cnt']

        cursor.execute("SELECT COUNT(*) as o_cnt FROM orders")
        o_cnt = cursor.fetchone()['o_cnt']

        cursor.execute("""
            SELECT o.*, u.full_name as customer_name, r.name as restaurant_name
            FROM orders o
            JOIN users u ON o.customer_id = u.id
            JOIN restaurants r ON o.restaurant_id = r.id
            ORDER BY o.id DESC LIMIT 5
        """)
        recent_orders = cursor.fetchall()

    db.close()

    stats = {
        "users_count": u_cnt,
        "restaurants_count": r_cnt,
        "foods_count": f_cnt,
        "orders_count": o_cnt
    }

    return templates.TemplateResponse(request=request, name="dashboard.html", context={
        "admin_email": admin['email'],
        "stats": stats,
        "recent_orders": format_dates(recent_orders)
    })

@app.get("/admin/users", response_class=HTMLResponse)
def admin_users_page(request: Request, admin=Depends(get_current_admin)):
    if not admin:
        return RedirectResponse(url="/admin/login")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT id, full_name, email, phone, address, role, status, created_at FROM users ORDER BY id DESC")
        users = cursor.fetchall()
    db.close()

    return templates.TemplateResponse(request=request, name="users.html", context={
        "admin_email": admin['email'],
        "users": format_dates(users)
    })

@app.post("/admin/users/{user_id}/status")
def admin_update_user_status(user_id: int, status: str = Form(...), admin=Depends(get_current_admin)):
    if not admin:
        raise HTTPException(status_code=401, detail="Unauthorized")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("UPDATE users SET status = %s WHERE id = %s AND role != 'admin'", (status, user_id))
    db.close()

    return RedirectResponse(url="/admin/users", status_code=status.HTTP_303_SEE_OTHER)

@app.get("/admin/restaurants", response_class=HTMLResponse)
def admin_restaurants_page(request: Request, admin=Depends(get_current_admin)):
    if not admin:
        return RedirectResponse(url="/admin/login")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("SELECT * FROM restaurants ORDER BY id DESC")
        restaurants = cursor.fetchall()
    db.close()

    return templates.TemplateResponse(request=request, name="restaurants.html", context={
        "admin_email": admin['email'],
        "restaurants": format_dates(restaurants)
    })

@app.post("/admin/restaurants")
def admin_create_restaurant(
    name: str = Form(...),
    address: str = Form(...),
    open_hours: str = Form(...),
    status: str = Form("active"),
    admin=Depends(get_current_admin)
):
    if not admin:
        raise HTTPException(status_code=401, detail="Unauthorized")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            INSERT INTO restaurants (name, address, open_hours, logo, cover, status)
            VALUES (%s, %s, %s, '', '', %s)
        """, (name, address, open_hours, status))
    db.close()

    return RedirectResponse(url="/admin/restaurants", status_code=status.HTTP_303_SEE_OTHER)

@app.post("/admin/restaurants/{restaurant_id}/edit")
def admin_edit_restaurant(
    restaurant_id: int,
    name: str = Form(...),
    address: str = Form(...),
    open_hours: str = Form(...),
    status: str = Form("active"),
    admin=Depends(get_current_admin)
):
    if not admin:
        raise HTTPException(status_code=401, detail="Unauthorized")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            UPDATE restaurants SET name = %s, address = %s, open_hours = %s, status = %s
            WHERE id = %s
        """, (name, address, open_hours, status, restaurant_id))
    db.close()

    return RedirectResponse(url="/admin/restaurants", status_code=status.HTTP_303_SEE_OTHER)

@app.post("/admin/restaurants/{restaurant_id}/delete")
def admin_delete_restaurant(restaurant_id: int, admin=Depends(get_current_admin)):
    if not admin:
        raise HTTPException(status_code=401, detail="Unauthorized")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("DELETE FROM restaurants WHERE id = %s", (restaurant_id,))
    db.close()

    return RedirectResponse(url="/admin/restaurants", status_code=status.HTTP_303_SEE_OTHER)

@app.get("/admin/orders", response_class=HTMLResponse)
def admin_orders_page(request: Request, admin=Depends(get_current_admin)):
    if not admin:
        return RedirectResponse(url="/admin/login")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("""
            SELECT o.*, u.full_name as customer_name, r.name as restaurant_name
            FROM orders o
            JOIN users u ON o.customer_id = u.id
            JOIN restaurants r ON o.restaurant_id = r.id
            ORDER BY o.id DESC
        """)
        orders = cursor.fetchall()
    db.close()

    return templates.TemplateResponse(request=request, name="orders.html", context={
        "admin_email": admin['email'],
        "orders": format_dates(orders)
    })

@app.post("/admin/orders/{order_id}/status")
def admin_update_order_status(order_id: int, status: str = Form(...), admin=Depends(get_current_admin)):
    if not admin:
        raise HTTPException(status_code=401, detail="Unauthorized")

    db = get_db_connection()
    with db.cursor() as cursor:
        cursor.execute("UPDATE orders SET status = %s WHERE id = %s", (status, order_id))
    db.close()

    return RedirectResponse(url="/admin/orders", status_code=status.HTTP_303_SEE_OTHER)
