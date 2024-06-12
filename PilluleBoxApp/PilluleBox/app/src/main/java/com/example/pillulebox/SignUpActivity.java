package com.example.pillulebox;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import AsyncTasks.SignUpUserTask;

public class SignUpActivity extends AppCompatActivity {
    TextView error;
    EditText username, email, password;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        error = findViewById(R.id.error_signup);
        username = findViewById(R.id.username_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        signup = findViewById(R.id.signup_button);

        error.setText("");
        signup.setOnClickListener(v -> {
            String username_str = username.getText().toString();
            String email_str = email.getText().toString();
            String password_str = password.getText().toString();
            Functions.toastMessage("Campos: '"+username_str+"' : '"+email_str+"' : '"+password_str+"'", SignUpActivity.this);
            if(validateFields(username_str, email_str, password_str)){
                try {
                    String passEncrypted = Functions.encryptPassword(password_str);
                    SignUpUserTask task = new SignUpUserTask(SignUpActivity.this, error);
                    task.execute(username_str, email_str, passEncrypted);
                } catch (Exception e) {
                    Functions.toastMessage(e.toString(), SignUpActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error.setText("Ingrese los datos en todos los campos");
            return false;
        } else if (!username.matches("^[a-zA-Z0-9 &!+:#*_ñÑ\\$=]*$")) {
            error.setText("El nombre de usuario debe contener sólo caracteres alfanuméricos y los siguientes símbolos:\n' ', '&', '!', '+', ':', '#', '*', '_', '$', '=')");
            return false;
        } else if ((!(password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*") && password.matches(".*[&!+:#*_$=@.].*"))) || (!password.matches("^[a-zA-Z0-9 &!+:#*_@.ñÑ\\$=]*$"))) {
            error.setText("La contraseña debe contener al menos una minúscula, una mayúscula, un número, y algún símbolo permitido:\n('&', '!', '+', ':', '#', '*', '_', '$', '=', '@', '.')");
            return false;
        }
        error.setText("");
        return true;
    }

}