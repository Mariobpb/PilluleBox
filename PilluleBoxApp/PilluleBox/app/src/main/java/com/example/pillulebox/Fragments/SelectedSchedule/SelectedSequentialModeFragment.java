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

import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;


public class SelectedSequentialModeFragment extends Fragment {

    private static final String ARG_MODE = "mode";
    private SequentialMode mode;

    public SelectedSequentialModeFragment() {
        // Required empty public constructor
    }

    public static SelectedSequentialModeFragment newInstance(SequentialMode mode) {
        SelectedSequentialModeFragment fragment = new SelectedSequentialModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = (SequentialMode) getArguments().getSerializable(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_sequential_mode, container, false);

        TextView medicineNameText = view.findViewById(R.id.medicine_name);
        TextView startDateTimeText = view.findViewById(R.id.start_datetime);
        TextView endDateTimeText = view.findViewById(R.id.end_datetime);
        TextView periodTimeText = view.findViewById(R.id.period_time);
        TextView limitConsumptionText = view.findViewById(R.id.limit_consumption);
        TextView currentConsumptionText = view.findViewById(R.id.current_consumptions);
        TextView affectedPeriodsText = view.findViewById(R.id.affected_periods);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        medicineNameText.setText(mode.getMedicineName());
        startDateTimeText.setText(dateFormat.format(mode.getStartDate()));

        endDateTimeText.setText(mode.getEndDate() != null ?
                dateFormat.format(mode.getEndDate()) : "");

        periodTimeText.setText(mode.getPeriod() != null ?
                timeFormat.format(mode.getPeriod()) : "");

        limitConsumptionText.setText(mode.getLimitTimesConsumption() == 0 ? "Sin límite" : String.valueOf(mode.getLimitTimesConsumption()));

        currentConsumptionText.setText(String.valueOf(mode.getCurrentTimesConsumption()));

        affectedPeriodsText.setText(mode.isAffectedPeriods() ? "Sí" : "No");

        return view;
    }
}