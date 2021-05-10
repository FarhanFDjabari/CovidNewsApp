package com.example.covidnewsapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.covidnewsapp.R;
import com.example.covidnewsapp.model.UserModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends AppCompatActivity {
    private Button loginBtn, registerBtn;
    private EditText etUsername, etEmail, etPassword;
    private ProgressBar registerLoading;
    private CoordinatorLayout coordinatorLayout;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

        coordinatorLayout = findViewById(R.id.registerCoordinatorLayout);
        loginBtn = findViewById(R.id.loginBtnToLogin);
        registerBtn = findViewById(R.id.registerBtnToLogin);
        etUsername = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        registerLoading = findViewById(R.id.register_loading);

        loginBtn.setOnClickListener(v -> {
            navigateToLogin();
        });

        registerBtn.setOnClickListener(v -> {
            registerProcess();
        });
    }

    private boolean registerProcess() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError("Username is empty");
            etUsername.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is empty");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is empty");
            etPassword.requestFocus();
            return false;
        }

        if (password.trim().length() < 6) {
            etPassword.setError("Password must be longer or equal than 6 character");
            etPassword.requestFocus();
            return false;
        }

        changeButtonEnabledState();
        registerLoading.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel user = new UserModel(username, email, password);

                FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(databaseTask -> {
                            if (databaseTask.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                firebaseUser.updateProfile(profileChangeRequest);

                                generateSnackbar("Berhasil membuat akun baru");
                                registerLoading.setVisibility(View.GONE);
                                navigateToLogin();
                            } else {
                                generateSnackbar("Gagal membuat akun baru");
                                registerLoading.setVisibility(View.GONE);
                                changeButtonEnabledState();
                            }
                        });
            } else {
                registerLoading.setVisibility(View.GONE);
                changeButtonEnabledState();
                generateSnackbar(task.getException().toString());
            }
        });
        return true;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeButtonEnabledState() {
        loginBtn.setEnabled(!loginBtn.isEnabled());
        registerBtn.setEnabled(!registerBtn.isEnabled());
    }

    private void generateSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, BaseTransientBottomBar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .show();
    }
}
