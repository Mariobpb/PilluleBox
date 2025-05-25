package com.example.pillulebox;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.adapters.HistoryAdapter;

import java.util.List;

import AsyncTasks.GetHistoryTask;
import Models.Dispenser;
import Models.History;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private Toolbar toolbar;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadHistoryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryData();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle("Historial");
        }
    }

    private void setupRecyclerView() {
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadHistoryData() {
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);
        String token = GeneralInfo.getToken(this);

        if (selectedDispenser == null) {
            Toast.makeText(this, "No hay dispensador seleccionado", Toast.LENGTH_SHORT).show();
            showEmptyState("No hay dispensador seleccionado");
            return;
        }

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token de autenticación no válido", Toast.LENGTH_SHORT).show();
            showEmptyState("Error de autenticación");
            return;
        }

        new GetHistoryTask(this, token, selectedDispenser.getMac(), new GetHistoryTask.HistoryCallback() {
            @Override
            public void onHistoryLoaded(List<History> historyList) {

                if (historyList.isEmpty()) {
                    showEmptyState("No hay historial disponible");
                } else {
                    showHistoryData(historyList);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HistoryActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                showEmptyState("Error al cargar el historial");
            }
        }).execute();
    }

    private void showHistoryData(List<History> historyList) {
        historyRecyclerView.setVisibility(View.VISIBLE);

        if (historyAdapter == null) {
            historyAdapter = new HistoryAdapter(historyList);
            historyAdapter.setOnItemClickListener(this::onHistoryItemClick);
            historyRecyclerView.setAdapter(historyAdapter);
        } else {
            historyAdapter.updateHistory(historyList);
        }
    }

    private void showEmptyState(String message) {
        historyRecyclerView.setVisibility(View.GONE);
    }

    private void onHistoryItemClick(History history) {
        String message = String.format(
                "Medicamento: %s\nEstado: %s\nFecha: %s\nRazón: %s",
                history.getMedicineName(),
                history.getConsumptionStatus(),
                history.getDateConsumption(),
                history.getReason() != null ? history.getReason() : "Sin razón especificada"
        );

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}