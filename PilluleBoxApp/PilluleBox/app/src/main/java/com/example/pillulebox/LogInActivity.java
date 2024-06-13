package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import AsyncTasks.LogInUserTask;
import AsyncTasks.ValidateTokenTask;


public class LogInActivity extends AppCompatActivity {
    Button login;
    EditText username_email, password;
    TextView error, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        String token = General.getToken(this);
        if (token != null) {
            new ValidateTokenTask(LogInActivity.this).execute(token);
        }

        login = findViewById(R.id.login_button);
        username_email = findViewById(R.id.username_email_login);
        password = findViewById(R.id.password_login);
        error = findViewById(R.id.error_login);
        signup = findViewById(R.id.from_login_to_signup);

        error.setText("");
        login.setOnClickListener(v -> {
            String username_email_str = username_email.getText().toString();
            String password_str = password.getText().toString();
            try {
                String passEncrypted = General.encryptPassword(password_str);
                new LogInUserTask(LogInActivity.this, error).execute(username_email_str, passEncrypted);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}