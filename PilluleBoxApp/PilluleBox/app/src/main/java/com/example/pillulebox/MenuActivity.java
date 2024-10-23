package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import AsyncTasks.GetUserDispensersTask;

import com.example.pillulebox.adapters.DispenserAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import AsyncTasks.CallbackValidations;
import Classes.Dispenser;

public class MenuActivity extends AppCompatActivity implements CallbackValidations {
    private static final String TAG = "MenuActivity"; // Tag para los logs
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView dispensersList;
    private Button validateToken;
    private Toolbar toolbar;
    private ImageButton logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initializeViews();
        setSupportActionBar(toolbar);
        setupNavigationDrawer();
        setupRecyclerView();
        loadUserDispensers();
        setupListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        validateToken = findViewById(R.id.token_validation);
        logout = findViewById(R.id.logout_button);
        toolbar = findViewById(R.id.toolbar);
        // Encontrar el RecyclerView directamente en el NavigationView
        dispensersList = navigationView.findViewById(R.id.rv_dispensers);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Configurando RecyclerView");

        if (dispensersList == null) {
            Log.e(TAG, "setupRecyclerView: RecyclerView no encontrado");
            return;
        }

        // Configurar el layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dispensersList.setLayoutManager(layoutManager);

        // Inicializar con una lista vacía
        List<Dispenser> emptyList = new ArrayList<>();
        DispenserAdapter adapter = new DispenserAdapter(emptyList);
        dispensersList.setAdapter(adapter);

        Log.d(TAG, "setupRecyclerView: RecyclerView configurado exitosamente");
    }

    private void loadUserDispensers() {
        Log.d(TAG, "loadUserDispensers: Intentando cargar dispensadores del usuario");
        String token = General.getToken(this);

        if (token == null) {
            Log.e(TAG, "loadUserDispensers: Token no encontrado");
            General.toastMessage("No se encontró el token de autenticación", this);
            return;
        }

        if (dispensersList == null) {
            Log.e(TAG, "loadUserDispensers: dispensersList es null, no se pueden cargar los dispensadores");
            return;
        }

        Log.d(TAG, "loadUserDispensers: Ejecutando GetUserDispensersTask");
        new GetUserDispensersTask(this, dispensersList, token).execute();
    }

    private void setupListeners() {
        Log.d(TAG, "setupListeners: Configurando listeners");

        navigationView.setNavigationItemSelectedListener(item -> {
            Log.d(TAG, "onNavigationItemSelected: Item seleccionado: " + item.getTitle());
            return true;
        });

        logout.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Cerrando sesión");
            General.deleteToken(this);
            Intent intent = new Intent(MenuActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // CallbackValidations
    @Override
    public void onCodeSent(boolean success) {
        Log.d(TAG, "onCodeSent: " + success);
    }

    @Override
    public void onCodeValidated(boolean success) {
        Log.d(TAG, "onCodeValidated: " + success);
    }

    @Override
    public void onFieldsValidated(boolean success) {
        Log.d(TAG, "onFieldsValidated: " + success);
    }

    @Override
    public void onTokenValidated(boolean success) {
        Log.d(TAG, "onTokenValidated: " + success);
        if (success) {
            General.toastMessage("Autenticación exitosa", this);
        } else {
            General.toastMessage("Autenticación fallida", this);
        }
    }
}