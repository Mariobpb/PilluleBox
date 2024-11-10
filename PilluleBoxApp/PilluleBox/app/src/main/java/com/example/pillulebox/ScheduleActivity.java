package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pillulebox.Fragments.SelectedSchedule.SelectedBasicModeFragment;
import com.example.pillulebox.Fragments.SelectedSchedule.SelectedSequentialModeFragment;
import com.example.pillulebox.Fragments.SelectedSchedule.SelectedSingleModeFragment;
import com.example.pillulebox.adapters.BasicModeAdapter;
import com.example.pillulebox.adapters.SequentialModeAdapter;
import com.example.pillulebox.adapters.SingleModeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import AsyncTasks.Schedules.GetBasicModesTask;
import AsyncTasks.Schedules.GetSequentialModesTask;
import AsyncTasks.Schedules.GetSingleModesTask;
import Models.Dispenser;
import Models.ScheduleModes.BasicMode;
import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;

public class ScheduleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RadioGroup modeRadioGroup;
    private RecyclerView schedulesRecyclerView;
    private FloatingActionButton addScheduleButton;

    private static final int SINGLE_MODE = 1;
    private static final int SEQUENTIAL_MODE = 2;
    private static final int BASIC_MODE = 3;
    private int currentMode = SINGLE_MODE;
    private static final String FRAGMENT_TAG = "schedule_fragment";

    protected void onResume() {
        super.onResume();
        clearFragments();
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
        addScheduleButton = findViewById(R.id.addScheduleFab);

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

        addScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DefineScheduleActivity.class);
            switch (currentMode) {
                case SINGLE_MODE:
                    intent.putExtra("mode_type", "single");
                    break;
                case SEQUENTIAL_MODE:
                    intent.putExtra("mode_type", "sequential");
                    break;
                case BASIC_MODE:
                    intent.putExtra("mode_type", "basic");
                    break;
            }
            v.getContext().startActivity(intent);
        });
    }

    private void loadInitialData() {
        modeRadioGroup.check(R.id.singleModeRadio);
        loadSingleModeData();
    }

    private void loadSingleModeData() {
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = GeneralInfo.getToken(this);
            new GetSingleModesTask(this, token, selectedDispenser.getMac(), new GetSingleModesTask.SingleModesCallback() {
                @Override
                public void onModesLoaded(List<SingleMode> modes) {
                    SingleModeAdapter adapter = new SingleModeAdapter(modes);
                    adapter.setOnItemClickListener(mode -> showSelectedSingleModeFragment(mode));
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
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = GeneralInfo.getToken(this);
            new GetSequentialModesTask(this, token, selectedDispenser.getMac(), new GetSequentialModesTask.SequentialModesCallback() {
                @Override
                public void onModesLoaded(List<SequentialMode> modes) {
                    SequentialModeAdapter adapter = new SequentialModeAdapter(modes);
                    adapter.setOnItemClickListener(mode -> showSelectedSequentialModeFragment(mode));
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
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = GeneralInfo.getToken(this);
            new GetBasicModesTask(this, token, selectedDispenser.getMac(), new GetBasicModesTask.BasicModesCallback() {
                @Override
                public void onModesLoaded(List<BasicMode> modes) {
                    BasicModeAdapter adapter = new BasicModeAdapter(modes);
                    adapter.setOnItemClickListener(mode -> showSelectedBasicModeFragment(mode));
                    schedulesRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ScheduleActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    private void showSelectedSingleModeFragment(SingleMode mode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearFragments();
        SelectedSingleModeFragment fragment = SelectedSingleModeFragment.newInstance(mode);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_schedule, fragment, FRAGMENT_TAG)
                .commit();
    }

    private void showSelectedSequentialModeFragment(SequentialMode mode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearFragments();
        SelectedSequentialModeFragment fragment = SelectedSequentialModeFragment.newInstance(mode);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_schedule, fragment, FRAGMENT_TAG)
                .commit();
    }

    private void showSelectedBasicModeFragment(BasicMode mode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        clearFragments();
        SelectedBasicModeFragment fragment = SelectedBasicModeFragment.newInstance(mode);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_schedule, fragment, FRAGMENT_TAG)
                .commit();
    }

    private void clearFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (currentFragment != null) {
            fragmentManager.beginTransaction()
                    .remove(currentFragment)
                    .commitNow();
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