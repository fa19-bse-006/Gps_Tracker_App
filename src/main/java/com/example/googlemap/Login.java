package com.example.googlemap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class Login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView,forgotPasswordTextView;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); // Now you can safely call hide()
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        loginButton.setOnClickListener(v -> loginUser(emailEditText.getText().toString(), passwordEditText.getText().toString()));
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
        forgotPasswordTextView.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                resetPassword(email);
            } else {
                Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loginUser(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        navigateToHomePage(); // Call the method to navigate to the home page
                    } else {
                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void navigateToHomePage() {

        Intent intent = new Intent(this, GPS_Activity.class); // Replace with your home page activity class
        startActivity(intent);
        finish(); // Optional: Finish the current login activity to prevent going back
    }

    public void Register(View view) {
        startActivity(new Intent(Login.this,Register.class));
    }
}