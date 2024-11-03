package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import AsyncTasks.CallbackValidations;
import AsyncTasks.SendCodeTask;
import AsyncTasks.ValidateFieldsTask;

public class SignUpActivity extends AppCompatActivity implements CallbackValidations {
    TextView error;
    EditText username, email, password;
    Button signup;
    String username_str, email_str, password_str;

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
            username_str = username.getText().toString();
            email_str = email.getText().toString();
            password_str = password.getText().toString();
            General.toastMessage("Campos: '"+username_str+"' : '"+email_str+"' : '"+password_str+"'", SignUpActivity.this);
            if(validateFields(username_str, email_str, password_str)){
                try {
                    new ValidateFieldsTask(this, this, error).execute(username_str, email_str);
                } catch (Exception e) {
                    General.toastMessage(e.toString(), SignUpActivity.this);
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
    @Override
    public void onFieldsValidated(boolean success) {
        if (success) {
            runOnUiThread(() -> {
                new SendCodeTask(this).execute(General.generateRandomCode(), email_str);
            });
        }
    }

    @Override
    public void onTokenValidated(boolean success) {

    }

    @Override
    public void onCodeValidated(boolean success) {
    }
    @Override
    public void onCodeSent(boolean success) {
        if (success) {
            runOnUiThread(() -> {
                General.toastMessage("Código enviado exitosamente", SignUpActivity.this);

                Intent intent = new Intent(SignUpActivity.this, EmailActivity.class);
                intent.putExtra("username", username.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("password", password.getText().toString());
                startActivity(intent);
            });
        } else {
            runOnUiThread(() -> {
                error.setText("Error al enviar el código");
            });
        }
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