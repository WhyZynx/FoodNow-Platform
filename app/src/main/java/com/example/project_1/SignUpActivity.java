package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtFullName, txtEmail, txtPhone, txtPassword, txtConfirmPassword;
    private UserSession userSession;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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
        TextView btnBackSignIn = findViewById(R.id.btnBackSignIn);

        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);
        ImageView btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        btnSignUp.setOnClickListener(this);
        btnBackSignIn.setOnClickListener(this);

        if (btnTogglePassword != null) {
            btnTogglePassword.setOnClickListener(v -> {
                if (isPasswordVisible) {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnTogglePassword.setImageResource(R.drawable.eye);
                    isPasswordVisible = false;
                } else {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnTogglePassword.setImageResource(R.drawable.eye_active);
                    isPasswordVisible = true;
                }
                txtPassword.setSelection(txtPassword.getText().length());
            });
        }

        if (btnToggleConfirmPassword != null) {
            btnToggleConfirmPassword.setOnClickListener(v -> {
                if (isConfirmPasswordVisible) {
                    txtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnToggleConfirmPassword.setImageResource(R.drawable.eye);
                    isConfirmPasswordVisible = false;
                } else {
                    txtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnToggleConfirmPassword.setImageResource(R.drawable.eye_active);
                    isConfirmPasswordVisible = true;
                }
                txtConfirmPassword.setSelection(txtConfirmPassword.getText().length());
            });
        }
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

        ApiConfig.registerUserApi(fullName, email, password, phone, "", new ApiConfig.ApiCallback<User>() {
            @Override
            public void onSuccess(User newUser) {
                userSession.createSession(newUser);
                Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
