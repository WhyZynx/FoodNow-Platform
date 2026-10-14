package com.example.project_1;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class ApiConfig {

    public static final String BASE_URL = "http://10.0.2.2:8000/";

    private static Retrofit retrofit = null;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public static synchronized ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public interface ApiService {
        @POST("login")
        Call<ApiResponse<User>> login(@Body LoginRequest request);

        @POST("register")
        Call<ApiResponse<User>> register(@Body RegisterRequest request);

        @POST("forgot-password")
        Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);

        @GET("profile/{user_id}")
        Call<ApiResponse<User>> getProfile(@Path("user_id") int userId);

        @PUT("profile/{user_id}")
        Call<ApiResponse<Void>> updateProfile(@Path("user_id") int userId, @Body UpdateProfileRequest request);

        @PUT("change-password/{user_id}")
        Call<ApiResponse<Void>> changePassword(@Path("user_id") int userId, @Body ChangePasswordRequest request);

        @GET("restaurants")
        Call<RestaurantListResponse> getRestaurants();

        @GET("restaurants/{restaurant_id}/foods")
        Call<FoodListResponse> getRestaurantFoods(@Path("restaurant_id") int restaurantId);

        @POST("orders")
        Call<CreateOrderResponse> createOrder(@Body CreateOrderRequest request);
    }

    public static class ApiResponse<T> {
        public String status;
        public String message;
        public T user;
    }

    public static class RestaurantListResponse {
        public String status;
        public List<RestaurantModel> restaurants;
    }

    public static class FoodListResponse {
        public String status;
        public List<FoodModel> foods;
    }

    public static class CreateOrderResponse {
        public String status;
        public String message;
        public Integer order_id;
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterRequest {
        public String full_name;
        public String email;
        public String password;
        public String phone;
        public String address;

        public RegisterRequest(String full_name, String email, String password, String phone, String address) {
            this.full_name = full_name;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.address = address;
        }
    }

    public static class ForgotPasswordRequest {
        public String email;
        public String new_password;

        public ForgotPasswordRequest(String email, String new_password) {
            this.email = email;
            this.new_password = new_password;
        }
    }

    public static class UpdateProfileRequest {
        public String full_name;
        public String phone;
        public String address;

        public UpdateProfileRequest(String full_name, String phone, String address) {
            this.full_name = full_name;
            this.phone = phone;
            this.address = address;
        }
    }

    public static class ChangePasswordRequest {
        public String old_password;
        public String new_password;

        public ChangePasswordRequest(String old_password, String new_password) {
            this.old_password = old_password;
            this.new_password = new_password;
        }
    }

    public static class CreateOrderRequest {
        public int customer_id;
        public int restaurant_id;
        public String delivery_address;
        public String payment_method;
        public double total_amount;
        public List<OrderItemRequest> items;

        public CreateOrderRequest(int customer_id, int restaurant_id, String delivery_address, String payment_method, double total_amount, List<OrderItemRequest> items) {
            this.customer_id = customer_id;
            this.restaurant_id = restaurant_id;
            this.delivery_address = delivery_address;
            this.payment_method = payment_method;
            this.total_amount = total_amount;
            this.items = items;
        }
    }

    public static class OrderItemRequest {
        public int food_id;
        public int quantity;
        public double price;

        public OrderItemRequest(int food_id, int quantity, double price) {
            this.food_id = food_id;
            this.quantity = quantity;
            this.price = price;
        }
    }

    public static class RestaurantModel {
        public int id;
        public String name;
        public String address;
        public String open_hours;
        public String logo;
        public String cover;
        public String status;
    }

    public static class FoodModel {
        public int id;
        public int restaurant_id;
        public String name;
        public double price;
        public String image;
        public String status;
    }

    public static void loginUserApi(String email, String password, ApiCallback<User> callback) {
        executor.execute(() -> {
            try {
                Call<ApiResponse<User>> call = getApiService().login(new LoginRequest(email, password));
                Response<ApiResponse<User>> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    User user = response.body().user;
                    mainHandler.post(() -> callback.onSuccess(user));
                } else {
                    String err = response.errorBody() != null ? response.errorBody().string() : "Login failed";
                    mainHandler.post(() -> callback.onError(err));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void registerUserApi(String fullName, String email, String password, String phone, String address, ApiCallback<User> callback) {
        executor.execute(() -> {
            try {
                Call<ApiResponse<User>> call = getApiService().register(new RegisterRequest(fullName, email, password, phone, address));
                Response<ApiResponse<User>> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    User user = response.body().user;
                    mainHandler.post(() -> callback.onSuccess(user));
                } else {
                    String err = response.errorBody() != null ? response.errorBody().string() : "Registration failed";
                    mainHandler.post(() -> callback.onError(err));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void updateProfileApi(int userId, String fullName, String phone, String address, ApiCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Call<ApiResponse<Void>> call = getApiService().updateProfile(userId, new UpdateProfileRequest(fullName, phone, address));
                Response<ApiResponse<Void>> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    mainHandler.post(() -> callback.onSuccess(true));
                } else {
                    mainHandler.post(() -> callback.onError("Update profile failed"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void changePasswordApi(int userId, String oldPassword, String newPassword, ApiCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Call<ApiResponse<Void>> call = getApiService().changePassword(userId, new ChangePasswordRequest(oldPassword, newPassword));
                Response<ApiResponse<Void>> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    mainHandler.post(() -> callback.onSuccess(true));
                } else {
                    mainHandler.post(() -> callback.onError("Change password failed"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void getRestaurantsApi(ApiCallback<List<RestaurantModel>> callback) {
        executor.execute(() -> {
            try {
                Call<RestaurantListResponse> call = getApiService().getRestaurants();
                Response<RestaurantListResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    List<RestaurantModel> list = response.body().restaurants;
                    mainHandler.post(() -> callback.onSuccess(list != null ? list : new ArrayList<>()));
                } else {
                    mainHandler.post(() -> callback.onError("Failed to fetch restaurants"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void getFoodsApi(int restaurantId, ApiCallback<List<FoodModel>> callback) {
        executor.execute(() -> {
            try {
                Call<FoodListResponse> call = getApiService().getRestaurantFoods(restaurantId);
                Response<FoodListResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    List<FoodModel> list = response.body().foods;
                    mainHandler.post(() -> callback.onSuccess(list != null ? list : new ArrayList<>()));
                } else {
                    mainHandler.post(() -> callback.onError("Failed to fetch foods"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void createOrderApi(int customerId, int restaurantId, String address, String paymentMethod, double totalAmount, List<OrderItemRequest> items, ApiCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                CreateOrderRequest req = new CreateOrderRequest(customerId, restaurantId, address, paymentMethod, totalAmount, items);
                Call<CreateOrderResponse> call = getApiService().createOrder(req);
                Response<CreateOrderResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    int orderId = response.body().order_id != null ? response.body().order_id : 0;
                    mainHandler.post(() -> callback.onSuccess(orderId));
                } else {
                    mainHandler.post(() -> callback.onError("Failed to create order"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static boolean authenticateUser(String email, String password) {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }

    public static boolean registerUser(String fullName, String email, String password, String phone, String address) {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }

    public static boolean checkUserEmailExists(String email) {
        return false;
    }

    public static User getUserByEmail(String email) {
        return new User(1, "FoodNow User", email, "", "0123456789", "TpHCM");
    }

    public static boolean updatePassword(String email, String newPassword) {
        return newPassword != null && !newPassword.isEmpty();
    }

    public static boolean updateUserProfile(String email, String fullName, String phone, String address) {
        return email != null && !email.isEmpty();
    }
}
