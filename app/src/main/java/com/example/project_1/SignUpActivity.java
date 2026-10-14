package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtFullName, txtEmail, txtPhone, txtPassword, txtConfirmPassword;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userSession = new UserSession(this);

        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnBackSignIn = findViewById(R.id.btnBackSignIn);

        btnSignUp.setOnClickListener(this);
        btnBackSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnSignUp) {
            handleSignUp();
        } else if (id == R.id.btnBackSignIn) {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        }
    }

    private void handleSignUp() {
        String fullName = txtFullName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            txtFullName.setError("Please enter Full Name");
            txtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Please enter Email");
            txtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Please enter Password");
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            txtPassword.setError("Password must be at least 6 characters");
            txtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtConfirmPassword.setError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return;
        }

        if (ApiConfig.checkUserEmailExists(email)) {
            Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSuccess = ApiConfig.registerUser(fullName, email, password, phone, "");

        if (isSuccess) {
            User newUser = ApiConfig.getUserByEmail(email);
            if (newUser != null) {
                userSession.createSession(newUser);
            } else {
                userSession.createSession(new User(fullName, email, password, phone, ""));
            }

            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed, please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
