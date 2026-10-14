package com.example.project_1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvHeaderName, tvHeaderEmail;
    private EditText etFullName, etEmail, etPhone, etAddress;

    private UserSession userSession;
    private String currentEmail = "";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        userSession = new UserSession(requireContext());

        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvHeaderEmail = view.findViewById(R.id.tvHeaderEmail);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnChangePassword = view.findViewById(R.id.btnChangePassword);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        loadUserData();

        btnUpdate.setOnClickListener(v -> handleUpdateProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> handleLogout());

        return view;
    }

    private void loadUserData() {
        if (!userSession.isLoggedIn()) {
            tvHeaderName.setText(R.string.text_not_logged_in);
            tvHeaderEmail.setText(R.string.text_not_logged_in_sub);
            return;
        }

        User sessionUser = userSession.getUserDetails();
        if (sessionUser != null) {
            currentEmail = sessionUser.getEmail();
            User dbUser = ApiConfig.getUserByEmail(currentEmail);
            User user = dbUser != null ? dbUser : sessionUser;

            String displayName = !TextUtils.isEmpty(user.getFullName()) ? user.getFullName() : getString(R.string.text_default_user_name);
            tvHeaderName.setText(displayName);
            tvHeaderEmail.setText(user.getEmail());

            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
        }
    }

    private void handleUpdateProfile() {
        if (TextUtils.isEmpty(currentEmail)) {
            Toast.makeText(getContext(), "Please sign in to update profile!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        boolean updated = ApiConfig.updateUserProfile(currentEmail, fullName, phone, address);
        if (updated) {
            userSession.updateSessionProfile(fullName, phone, address);
            tvHeaderName.setText(!TextUtils.isEmpty(fullName) ? fullName : getString(R.string.text_default_user_name));
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Update failed, please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        if (TextUtils.isEmpty(currentEmail)) {
            Toast.makeText(getContext(), "Please sign in!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.text_change_password);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etCurrentPass = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPass = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPass = dialogView.findViewById(R.id.etConfirmNewPassword);

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.text_change_password, null);
        builder.setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPass = etCurrentPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            if (TextUtils.isEmpty(currentPass)) {
                etCurrentPass.setError("Enter current password");
                return;
            }

            if (!ApiConfig.authenticateUser(currentEmail, currentPass)) {
                etCurrentPass.setError("Incorrect current password");
                return;
            }

            if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
                etNewPass.setError("New password must be at least 6 characters");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirmPass.setError("Password confirmation does not match");
                return;
            }

            boolean success = ApiConfig.updatePassword(currentEmail, newPass);
            if (success) {
                Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogout() {
        userSession.logout();
        Toast.makeText(getContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
