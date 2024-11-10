package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pillulebox.Fragments.DefineSchedule.DefineBasicModeFragment;
import com.example.pillulebox.Fragments.DefineSchedule.DefineSequentialModeFragment;
import com.example.pillulebox.Fragments.DefineSchedule.DefineSingleModeFragment;

import Models.ScheduleModes.BasicMode;
import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;

public class DefineScheduleActivity extends AppCompatActivity {
    private String modeType;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_schedule);
        getModeFromExtras();
        setupToolbar();
    }
    private void getModeFromExtras() {
        Intent intent = getIntent();
        modeType = intent.getStringExtra("mode_type");

        if (modeType != null) {
            if (modeType.equals("single")) {
                SingleMode singleMode = (SingleMode) intent.getSerializableExtra("mode");
                DefineSingleModeFragment fragment = DefineSingleModeFragment.newInstance(singleMode);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_edit_schedule, fragment)
                        .commit();
            } else if (modeType.equals("sequential")) {
                SequentialMode sequentialMode = (SequentialMode) intent.getSerializableExtra("mode");
                DefineSequentialModeFragment fragment = DefineSequentialModeFragment.newInstance(sequentialMode);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_edit_schedule, fragment)
                        .commit();
            } else if (modeType.equals("basic")) {
                BasicMode basicMode = (BasicMode) intent.getSerializableExtra("mode");
                DefineBasicModeFragment fragment = DefineBasicModeFragment.newInstance(basicMode);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_edit_schedule, fragment)
                        .commit();
            }
        }
    }
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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