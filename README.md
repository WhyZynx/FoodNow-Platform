# FoodNow Platform

FoodNow is a food-ordering platform consisting of:

- **Android application:** Java + XML, built with Android Studio.
- **Backend API:** FastAPI (Python).
- **Admin website:** HTML templates rendered by FastAPI + Jinja2.
- **Database:** MySQL (`foodnow_db`).

## 1. Project structure

```text
FoodNow-Platform/
├── app/                 # Android application
├── backend/             # FastAPI backend and Admin website
│   ├── main.py
│   ├── requirements.txt
│   ├── .env.example
│   ├── templates/       # Admin pages
│   └── static/          # Admin CSS and static files
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 2. Requirements

Install the following tools before running the project:

- Python 3.10 or later.
- MySQL Server (and a MySQL client such as MySQL Workbench, if desired).
- Android Studio with Android SDK installed.
- JDK compatible with the Android Gradle Plugin configured in this project. Use the Gradle JDK recommended by Android Studio.
- A web browser.

## 3. Configure and run the backend

The backend provides the API used by the Android app and the Admin website. Start MySQL before starting FastAPI.

### macOS

Open Terminal and go to the backend folder:

```bash
cd FoodNow-Platform/backend
```

Create and activate a virtual environment:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

Install dependencies:

```bash
python -m pip install --upgrade pip
pip install -r requirements.txt
```

Create a `.env` file based on `.env.example`:

```bash
cp .env.example .env
```

Edit `.env` and enter the MySQL connection information. For example:

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=YOUR_MYSQL_PASSWORD
DB_NAME=foodnow_db
```

Replace `YOUR_MYSQL_PASSWORD` with the password configured for the local MySQL account. If the MySQL account has no password, leave the value empty:

```env
DB_PASSWORD=
```

Start the API and Admin website:

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### Windows (PowerShell)

```powershell
cd FoodNow-Platform\backend
py -m venv .venv
.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
pip install -r requirements.txt
Copy-Item .env.example .env
```

Edit `.env` with the correct MySQL credentials, then run:

```powershell
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

If PowerShell blocks virtual-environment activation, run this in the current PowerShell window and activate again:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

### Database initialization

When the FastAPI application starts, its startup process calls `init_db()` in `backend/main.py`. It creates the `foodnow_db` database and required tables if they do not already exist, then inserts the configured demo users, restaurants, foods, and drivers when missing.

Keep the MySQL service running while using the API, Android app, or Admin website. Do not delete the database if you need to preserve existing accounts and orders.

## 4. Open the Admin website

Make sure the backend is running, then open:

- Admin login: <http://127.0.0.1:8000/admin/login>
- Admin dashboard: <http://127.0.0.1:8000/admin/dashboard>
- API documentation (Swagger UI): <http://127.0.0.1:8000/docs>
- API health/root endpoint: <http://127.0.0.1:8000/>

Demo Admin account configured by the backend:

```text
Email:    admin@foodnow.com
Password: 123456
```

The backend also seeds a demo customer account:

```text
Email:    user@foodnow.com
Password: 123456
```

Change demo credentials before using the project in any real environment. These are development/demo credentials only.

## 5. Run the Android application

1. Start MySQL and the FastAPI backend using the instructions above.
2. Open **Android Studio**.
3. Select **Open** and choose the `FoodNow-Platform` project root (the folder containing `settings.gradle.kts`).
4. Wait for Gradle Sync to finish. Allow Android Studio to install any required Android SDK components.
5. Choose an Android emulator or connect an Android phone with USB debugging enabled.
6. Check the API base URL in:

   `app/src/main/java/com/example/project_1/ApiConfig.java`

7. Set `BASE_URL` according to the device being used (include the trailing slash):

   **Android Emulator:**

   ```java
   public static String BASE_URL = "http://10.0.2.2:8000/";
   ```

   **Physical Android phone:** use the computer's LAN IP address, for example:

   ```java
   public static String BASE_URL = "http://192.168.1.100:8000/";
   ```

   Replace `192.168.1.100` with the actual IP address of the computer running FastAPI. The phone and computer must be connected to the same Wi-Fi network. Keep the backend running and allow port `8000` through the computer's firewall if necessary.

8. Press **Run** in Android Studio and select the device.

### Find the computer's local IP address

**macOS:**

```bash
ipconfig getifaddr en0
```

If this returns no address, check the active network interface in System Settings → Network.

**Windows:**

```powershell
ipconfig
```

Find the IPv4 Address of the Wi-Fi adapter currently in use.

> `localhost` or `127.0.0.1` on an Android phone refers to the phone itself, not the computer. Use `10.0.2.2` for the standard Android emulator, or the computer's LAN IP for a physical phone.

## 6. Build an Android APK

In Android Studio, select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

Or, from the project root:

**macOS:**

```bash
./gradlew assembleDebug
```

**Windows:**

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 7. Stop the services

- Stop FastAPI by pressing `Ctrl + C` in the terminal running Uvicorn.
- Stop MySQL using the service manager or tool used to start it.
- The Android app can be closed from the emulator or device.

## 8. Troubleshooting

| Problem | What to check |
|---|---|
| MySQL connection error | Confirm MySQL is running and `DB_HOST`, `DB_USER`, `DB_PASSWORD`, and `DB_NAME` in `backend/.env` are correct. |
| `ModuleNotFoundError` when starting FastAPI | Activate the backend virtual environment and run `pip install -r requirements.txt`. |
| Admin page cannot be opened | Confirm Uvicorn is running on port `8000`; open `http://127.0.0.1:8000/admin/login`. |
| Android app cannot connect to backend | Check `ApiConfig.BASE_URL`, confirm the backend is running, and verify the phone/emulator network configuration. |
| Physical phone cannot connect | Use the computer's LAN IPv4 address, connect both devices to the same Wi-Fi, and check firewall settings. |
| Port `8000` is already in use | Stop the process using that port, or start Uvicorn on another port and update the Android `BASE_URL` to match. |
| Gradle Sync/build fails | Check the Gradle JDK and Android SDK configuration in Android Studio, then sync the project again. |

## 9. Important notes

- Start **MySQL first**, then the **FastAPI backend**, then launch the **Android app**.
- The Admin website is served by the same FastAPI backend; there is no separate web server to start.
- Keep `backend/.env` local. Do not commit database passwords or other secrets to Git. Use `.env.example` as the shareable configuration template.
- The Android app's API address is configured in `ApiConfig.java`. Update it when changing computers, networks, or backend ports.
- This README describes the development setup. Production deployment requires proper secrets management, HTTPS, authentication hardening, and production server configuration.
