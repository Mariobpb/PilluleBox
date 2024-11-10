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
import AsyncTasks.SignUpUserTask;
import AsyncTasks.ValidateCodeTask;

public class EmailActivity extends AppCompatActivity implements CallbackValidations {
    EditText codeInput;
    Button verifyCode;
    TextView error, sendCode;
    String username, email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        codeInput = findViewById(R.id.email_code);
        verifyCode = findViewById(R.id.email_button);
        error = findViewById(R.id.error_email);
        sendCode = findViewById(R.id.send_code);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        error.setText("");
        verifyCode.setOnClickListener(v -> {
            String entered_code = codeInput.getText().toString();
            new ValidateCodeTask(this, this, error).execute(entered_code, email);
        });
        sendCode.setOnClickListener(v -> {
            new SendCodeTask(this).execute(GeneralInfo.generateRandomCode(), email);
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
    public void onCodeValidated(boolean success) {
        if (success) {
            runOnUiThread(() -> {
                try {
                    new SignUpUserTask(this, this).execute(username, email, GeneralInfo.encryptPassword(password));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            runOnUiThread(() -> {
                error.setText("Error, el código de confirmación de correo electrónico no coincide");
            });
        }
    }

    @Override
    public void onFieldsValidated(boolean success) {

    }

    @Override
    public void onTokenValidated(boolean success) {

    }

    @Override
    public void onCodeSent(boolean success) {
        if (success) {
            runOnUiThread(() -> {
                GeneralInfo.toastMessage("Código enviado exitosamente", EmailActivity.this);
            });
        } else {
            runOnUiThread(() -> {
                error.setText("Error al enviar el código");
            });
        }
    }
}