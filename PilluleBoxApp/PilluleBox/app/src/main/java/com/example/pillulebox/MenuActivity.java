package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import AsyncTasks.GetUserDispensersTask;

import com.example.pillulebox.adapters.DispenserAdapter;
import com.example.pillulebox.Fragments.DispenserSelectedFragment;
import com.example.pillulebox.Fragments.NoDispenserSelectedFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import AsyncTasks.CallbackValidations;
import AsyncTasks.ValidateTokenTask;
import Models.Dispenser;

public class MenuActivity extends AppCompatActivity implements CallbackValidations {
    private static final String TAG = "MenuActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView dispensersList;
    private Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
        String token = General.getToken(this);
        if (token != null) {
            new ValidateTokenTask(this).execute(token);
        } else {
            returnToLogIn();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String token = General.getToken(this);
        if (token != null) {
            new ValidateTokenTask(this).execute(token);
        }

        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerView();
        updateSelectedDispenserNameFromPreferences();
        loadUserDispensers();
        setupListeners();
        updateFragmentBasedOnSelection();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        dispensersList = navigationView.findViewById(R.id.rv_dispensers);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    private void updateFragmentBasedOnSelection() {
        Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(this);
        Fragment fragment;

        if (selectedDispenser == null) {
            Log.d(TAG, "Dispensador no seleccionado");
            fragment = new NoDispenserSelectedFragment();
        } else {
            Log.d(TAG, "Dispensador seleccionado");
            fragment = new DispenserSelectedFragment();
        }

        // Realizar la transacción del fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_menu, fragment)
                .commit();
    }

    public void onDispenserSelected(Dispenser dispenser) {
        updateFragmentBasedOnSelection();
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Configurando RecyclerView");

        if (dispensersList == null) {
            Log.e(TAG, "setupRecyclerView: RecyclerView no encontrado");
            return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dispensersList.setLayoutManager(layoutManager);

        List<Dispenser> emptyList = new ArrayList<>();
        DispenserAdapter adapter = new DispenserAdapter(this, emptyList);
        dispensersList.setAdapter(adapter);

        Log.d(TAG, "setupRecyclerView: RecyclerView configurado exitosamente");
    }

    private void updateSelectedDispenserNameFromPreferences() {
        Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(this);
        updateSelectedDispenserName(selectedDispenser != null ? selectedDispenser.getName() : null);
    }

    public void updateSelectedDispenserName(String name) {
        /*
        if (dispenserSelectedName != null) {
            dispenserSelectedName.setText(name != null ? name : "Sin seleccionar");
        }
        */
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_history) {
            General.toastMessage("Historial", this);
            return true;
        } else if (id == R.id.menu_logout) {
            Log.d(TAG, "onClick: Cerrando sesión");
            General.clearAllPreferences(this);
            Intent intent = new Intent(MenuActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void returnToLogIn() {
        General.toastMessage("Autenticación fallida", this);
        General.clearAllPreferences(this);
        Intent intent = new Intent(this, LogInActivity.class);
        this.startActivity(intent);
        finish();
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
        } else {
            returnToLogIn();
        }
    }
}