package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import AsyncTasks.CallbackValidations;
import AsyncTasks.LogInUserTask;
import AsyncTasks.ValidateTokenTask;


public class LogInActivity extends AppCompatActivity implements CallbackValidations {
    Button login;
    EditText username_email, password;
    TextView error, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        String token = GeneralInfo.getToken(this);
        if (token != null) {
            new ValidateTokenTask(this).execute(token);
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
                String passEncrypted = GeneralInfo.encryptPassword(password_str);
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

    @Override
    public void onCodeSent(boolean success) {

    }

    @Override
    public void onCodeValidated(boolean success) {

    }

    @Override
    public void onFieldsValidated(boolean success) {

    }

    @Override
    public void onTokenValidated(boolean success) {
        if (success) {
            GeneralInfo.toastMessage("Autenticación exitosa", this);
            Intent intent = new Intent(this, MenuActivity.class);
            this.startActivity(intent);
            finish();
        } else {
            GeneralInfo.toastMessage("Autenticación fallida", this);
        }
    }
}