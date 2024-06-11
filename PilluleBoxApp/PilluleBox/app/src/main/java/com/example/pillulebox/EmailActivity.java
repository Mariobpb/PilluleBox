package com.example.pillulebox;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

import AsyncTasks.LogInUserTask;

public class EmailActivity extends AppCompatActivity {
    Button send_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        send_code = findViewById(R.id.email_button);

        send_code.setOnClickListener(v -> {
            sendCodeToEmail();
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
    private void sendCodeToApiAndEmail() {
        String randomCode = generateRandomCode();
        String email = getEmailFromSignUpActivity(); // Obtener el correo electrónico ingresado en SignUpActivity

        // Enviar el código, la fecha y hora de creación y expiración, y el correo electrónico a la API
        new SendCodeToApiTask(this).execute(randomCode, email);

        // Enviar el código al correo electrónico
        new SendCodeToEmailTask(this).execute(randomCode, email);
    }
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void toastMessage(String message) {
        Toast.makeText(EmailActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}