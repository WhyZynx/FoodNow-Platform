package com.example.project_1;

import android.text.TextUtils;

public class ApiConfig {
    public static final String BASE_URL = "https://api.foodnow.example.com/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String REGISTER_URL = BASE_URL + "register";
    public static final String FORGOT_PASSWORD_URL = BASE_URL + "forgot-password";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "update-profile";
    public static final String UPDATE_PASSWORD_URL = BASE_URL + "update-password";

    public static boolean authenticateUser(String email, String password) {
        // Simulate API login request
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }

    public static boolean registerUser(String fullName, String email, String password, String phone, String address) {
        // Simulate API register request
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }

    public static boolean checkUserEmailExists(String email) {
        // Simulate API check email exists
        return false;
    }

    public static User getUserByEmail(String email) {
        // Simulate API get user profile
        return new User(1, "FoodNow User", email, "", "0123456789", "TpHCM");
    }

    public static boolean updatePassword(String email, String newPassword) {
        // Simulate API update password request
        return !TextUtils.isEmpty(newPassword);
    }

    public static boolean updateUserProfile(String email, String fullName, String phone, String address) {
        // Simulate API update profile request
        return !TextUtils.isEmpty(email);
    }
}
