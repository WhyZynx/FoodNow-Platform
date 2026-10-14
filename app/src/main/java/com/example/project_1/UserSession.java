package com.example.project_1;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF_NAME = "foodnow_user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ID = "user_id";
    private static final String KEY_FULL_NAME = "user_full_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PHONE = "user_phone";
    private static final String KEY_ADDRESS = "user_address";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public UserSession(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void createSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.apply();
    }

    public void updateSessionProfile(String fullName, String phone, String address) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        int id = preferences.getInt(KEY_ID, 0);
        String fullName = preferences.getString(KEY_FULL_NAME, "");
        String email = preferences.getString(KEY_EMAIL, "");
        String phone = preferences.getString(KEY_PHONE, "");
        String address = preferences.getString(KEY_ADDRESS, "");

        return new User(id, fullName, email, "", phone, address);
    }

    public String getUserEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
