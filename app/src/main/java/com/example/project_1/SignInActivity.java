package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail, txtPassword;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(this);
        if (userSession.isLoggedIn()) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signin);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        Button btnForgot = findViewById(R.id.btnForgot);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView btnSignInGoogle = findViewById(R.id.btnSignInGoogle);

        btnForgot.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        if (btnSignInGoogle != null) {
            btnSignInGoogle.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnForgot) {
            startActivity(new Intent(SignInActivity.this, ForgotPassword.class));
        } else if (id == R.id.btnSignIn) {
            handleSignIn();
        } else if (id == R.id.btnSignUp) {
            startActivity(new Intent(SignInActivity.this, SignInActivity.class).setClass(this, SignUpActivity.class));
        } else if (id == R.id.btnSignInGoogle) {
            Toast.makeText(this, "Google sign-in feature is under development", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignIn() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

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

        ApiConfig.loginUserApi(email, password, new ApiConfig.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                userSession.createSession(user);
                Toast.makeText(SignInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SignInActivity.this, "Đăng nhập thất bại: Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
