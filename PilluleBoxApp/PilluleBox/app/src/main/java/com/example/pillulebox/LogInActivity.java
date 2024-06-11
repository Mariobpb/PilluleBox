package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import AsyncTasks.LogInUserTask;


public class LogInActivity extends AppCompatActivity {
    Button login;
    EditText username_email, password;
    TextView error, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        login = findViewById(R.id.login_button);
        username_email = findViewById(R.id.username_email_login);
        password = findViewById(R.id.password_login);
        error = findViewById(R.id.error_login);
        signup = findViewById(R.id.from_login_to_signup);

        error.setText("");
        login.setOnClickListener(v -> {
            String username_email_str = username_email.getText().toString();
            String password_str = password.getText().toString();
            new LogInUserTask(LogInActivity.this, error).execute(username_email_str, password_str);
        });
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}