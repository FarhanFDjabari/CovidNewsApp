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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Button registerBtn, loginBtn;
    private EditText etLoginEmail, etLoginPassword;
    private ProgressBar loginProgressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        coordinatorLayout = findViewById(R.id.loginCoordinatorLayout);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        loginProgressBar = findViewById(R.id.login_loading);

        loginBtn.setOnClickListener(v -> {
            login();
        });

        registerBtn.setOnClickListener(v -> register());
    }

    private void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private boolean login() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            etLoginEmail.setError("Email is empty");
            etLoginEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Please provide valid email");
            etLoginEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("Password is empty");
            etLoginPassword.requestFocus();
            return false;
        }

        if (password.trim().length() < 6) {
            etLoginPassword.setError("Password must be longer or equal than 6 character");
            etLoginPassword.requestFocus();
            return false;
        }

        changeButtonEnabledState();
        loginProgressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginProgressBar.setVisibility(View.GONE);
                generateSnackbar("Login Success");
                navigateToMain();
            } else {
                loginProgressBar.setVisibility(View.GONE);
                generateSnackbar("Email/password not matched");
                changeButtonEnabledState();
            }
        });
        return true;
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
