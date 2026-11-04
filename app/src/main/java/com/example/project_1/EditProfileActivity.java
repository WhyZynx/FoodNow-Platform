package com.example.project_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etAddress;
    private UserSession userSession;
    private int currentUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        userSession = new UserSession(this);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);

        User user = userSession.getUserDetails();
        if (user != null) {
            currentUserId = user.getId();
            etFullName.setText(user.getFullName());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
        }

        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    private void handleSaveProfile() {
        if (currentUserId == 0) {
            Toast.makeText(this, "Please sign in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Please enter Full Name");
            etFullName.requestFocus();
            return;
        }

        ApiConfig.updateProfileApi(currentUserId, fullName, phone, address, new ApiConfig.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                userSession.updateSessionProfile(fullName, phone, address);
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditProfileActivity.this, "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
