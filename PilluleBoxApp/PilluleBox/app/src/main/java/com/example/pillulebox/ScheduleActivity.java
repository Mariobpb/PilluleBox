package com.example.pillulebox;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pillulebox.adapters.BasicModeAdapter;
import com.example.pillulebox.adapters.DispenserAdapter;
import com.example.pillulebox.adapters.SequentialModeAdapter;
import com.example.pillulebox.adapters.SingleModeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import AsyncTasks.GetBasicModesTask;
import AsyncTasks.GetSequentialModesTask;
import AsyncTasks.GetSingleModesTask;
import Models.Dispenser;
import Models.ScheduleModes.BasicMode;
import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;

public class ScheduleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RadioGroup modeRadioGroup;
    private RecyclerView schedulesRecyclerView;
    private FloatingActionButton addScheduleFab;

    private static final int SINGLE_MODE = 1;
    private static final int SEQUENTIAL_MODE = 2;
    private static final int BASIC_MODE = 3;
    private int currentMode = SINGLE_MODE;
    protected void onResume() {
        super.onResume();
        switch (currentMode) {
            case SINGLE_MODE:
                loadSingleModeData();
                break;
            case SEQUENTIAL_MODE:
                loadSequentialModeData();
                break;
            case BASIC_MODE:
                loadBasicModeData();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initializeViews();
        setupToolbar();
        setupListeners();
        loadInitialData();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        modeRadioGroup = findViewById(R.id.modeRadioGroup);
        schedulesRecyclerView = findViewById(R.id.schedulesRecyclerView);
        addScheduleFab = findViewById(R.id.addScheduleFab);

        schedulesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupListeners() {
        modeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.singleModeRadio) {
                currentMode = SINGLE_MODE;
                loadSingleModeData();
            } else if (checkedId == R.id.sequentialModeRadio) {
                currentMode = SEQUENTIAL_MODE;
                loadSequentialModeData();
            } else if (checkedId == R.id.basicModeRadio) {
                currentMode = BASIC_MODE;
                loadBasicModeData();
            }
        });

        addScheduleFab.setOnClickListener(v -> {
            /*
            Intent intent;
            switch (currentMode) {
                case SINGLE_MODE:
                    intent = new Intent(this, AddSingleModeActivity.class);
                    break;
                case SEQUENTIAL_MODE:
                    intent = new Intent(this, AddSequentialModeActivity.class);
                    break;
                case BASIC_MODE:
                    intent = new Intent(this, AddBasicModeActivity.class);
                    break;
                default:
                    return;
            }
            startActivity(intent);

             */
        });
    }

    private void loadInitialData() {
        // Por defecto, cargar Single Mode
        modeRadioGroup.check(R.id.singleModeRadio);
        loadSingleModeData();
    }

    private void loadSingleModeData() {
        Dispenser selectedDispenser = General.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = General.getToken(this);
            new GetSingleModesTask(this, token, selectedDispenser.getMac() ,new GetSingleModesTask.SingleModesCallback() {
                @Override
                public void onModesLoaded(List<SingleMode> modes) {
                    SingleModeAdapter adapter = new SingleModeAdapter(modes);
                    schedulesRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ScheduleActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    private void loadSequentialModeData() {
        Dispenser selectedDispenser = General.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = General.getToken(this);
            new GetSequentialModesTask(this, token, selectedDispenser.getMac(), new GetSequentialModesTask.SequentialModesCallback() {
                @Override
                public void onModesLoaded(List<SequentialMode> modes) {
                    SequentialModeAdapter adapter = new SequentialModeAdapter(modes);
                    schedulesRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ScheduleActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    private void loadBasicModeData() {
        Dispenser selectedDispenser = General.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = General.getToken(this);
            new GetBasicModesTask(this, token, selectedDispenser.getMac(), new GetBasicModesTask.BasicModesCallback() {
                @Override
                public void onModesLoaded(List<BasicMode> modes) {
                    BasicModeAdapter adapter = new BasicModeAdapter(modes);
                    schedulesRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ScheduleActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}