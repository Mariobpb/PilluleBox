package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import Models.ScheduleModes.BasicMode;
import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;

public class EditScheduleActivity extends AppCompatActivity {
    private String modeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_schedule);
        getModeFromExtras();

    }
    private void getModeFromExtras() {
        Intent intent = getIntent();
        modeType = intent.getStringExtra("mode_type");

        if (modeType != null) {
            if (modeType.equals("sequential")) {
                SingleMode singleMode = (SingleMode) intent.getSerializableExtra("mode");
            } else if (modeType.equals("sequential")) {
                SequentialMode sequentialMode = (SequentialMode) intent.getSerializableExtra("mode");
            } else if (modeType.equals("basic")) {
                BasicMode basicMode = (BasicMode) intent.getSerializableExtra("mode");
            }
        }
    }
}