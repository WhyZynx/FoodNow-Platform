package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtEmail = findViewById(R.id.txtEmail);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        Button btnBackSignIn = findViewById(R.id.btnBackSignIn);

        btnResetPassword.setOnClickListener(this);
        btnBackSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnResetPassword) {
            handleResetPassword();
        } else if (id == R.id.btnBackSignIn) {
            startActivity(new Intent(ForgotPassword.this, SignInActivity.class));
            finish();
        }
    }

    private void handleResetPassword() {
        String email = txtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Please enter Email");
            txtEmail.requestFocus();
            return;
        }

        Toast.makeText(this, "Reset code sent to your email!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ForgotPassword.this, SignInActivity.class));
        finish();
    }
}
