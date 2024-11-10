package com.example.pillulebox.Fragments.SelectedSchedule;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pillulebox.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import Models.ScheduleModes.BasicMode;


public class SelectedBasicModeFragment extends Fragment {

    private static final String ARG_MODE = "mode";
    private BasicMode mode;

    public SelectedBasicModeFragment() {
        // Required empty public constructor
    }

    public static SelectedBasicModeFragment newInstance(BasicMode mode) {
        SelectedBasicModeFragment fragment = new SelectedBasicModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = (BasicMode) getArguments().getSerializable(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_basic_mode, container, false);

        TextView medicineNameText = view.findViewById(R.id.medicine_name);
        TextView morningHourRangeText = view.findViewById(R.id.morning_hour_range);
        TextView afternoonHourRangeText = view.findViewById(R.id.afternoon_hour_range);
        TextView nightHourRangeText = view.findViewById(R.id.night_hour_range);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        medicineNameText.setText(mode.getMedicineName());

        morningHourRangeText.setText((mode.getMorningStartTime() != null ?
                timeFormat.format(mode.getMorningStartTime()) : "") + " - " +
                (mode.getMorningEndTime() != null ?
                timeFormat.format(mode.getMorningEndTime()) : ""));
        afternoonHourRangeText.setText((mode.getAfternoonStartTime() != null ?
                timeFormat.format(mode.getAfternoonStartTime()) : "") + " - " +
                (mode.getAfternoonEndTime() != null ?
                        timeFormat.format(mode.getAfternoonEndTime()) : ""));
        nightHourRangeText.setText((mode.getNightStartTime() != null ?
                timeFormat.format(mode.getNightStartTime()) : "") + " - " +
                (mode.getNightEndTime() != null ?
                        timeFormat.format(mode.getNightEndTime()) : ""));
        return view;
    }
}