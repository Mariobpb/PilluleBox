package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import AsyncTasks.ValidateTokenTask;

public class MenuActivity extends AppCompatActivity {
    Button validateToken;
    ImageButton logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        validateToken = findViewById(R.id.token_validation);
        logout = findViewById(R.id.logout_button);

        validateToken.setOnClickListener(v -> {
            String token = General.getToken(this);
            if (token != null) {
                new ValidateTokenTask(MenuActivity.this).execute(token);
            }
        });
        logout.setOnClickListener(v -> {
            General.deleteToken(this);
            Intent intent = new Intent(MenuActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}