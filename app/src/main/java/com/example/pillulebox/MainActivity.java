package com.example.pillulebox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;


public class MainActivity extends AppCompatActivity {
    Button login_button;
    EditText username_email_et, password_et;
    private static final String BASE_URL = "http://192.168.100.14:8080/";
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_email_et = findViewById(R.id.username_email_login);
        password_et = findViewById(R.id.password_login);
        findViewById(R.id.login_button).setOnClickListener(v -> {
            String username_email_str = username_email_et.getText().toString();
            String password_str = password_et.getText().toString();
            new AuthenticateUserTask(MainActivity.this).execute(username_email_str, password_str);
        });
    }
    public void toastMessage(String str){
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}