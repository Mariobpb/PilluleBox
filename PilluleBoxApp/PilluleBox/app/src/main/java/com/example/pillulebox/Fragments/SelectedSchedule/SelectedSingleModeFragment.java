package com.example.pillulebox.Fragments.SelectedSchedule;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pillulebox.AssignCellsActivity;
import com.example.pillulebox.ContextActivity;
import com.example.pillulebox.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import Models.ScheduleModes.SingleMode;

public class SelectedSingleModeFragment extends Fragment {

    private static final String ARG_MODE = "mode";
    private SingleMode mode;

    public SelectedSingleModeFragment() {
        // Required empty public constructor
    }

    public static SelectedSingleModeFragment newInstance(SingleMode mode) {
        SelectedSingleModeFragment fragment = new SelectedSingleModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = (SingleMode) getArguments().getSerializable(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_single_mode, container, false);

        TextView medicineNameText = view.findViewById(R.id.medicine_name);
        TextView dispensingDateText = view.findViewById(R.id.dispensing_date);
        AppCompatButton assignCellsButton = view.findViewById(R.id.assign_cells);

        medicineNameText.setText(mode.getMedicineName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        dispensingDateText.setText(dateFormat.format(mode.getDispensingDate()));

        assignCellsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AssignCellsActivity.class);
            intent.putExtra("mode", mode);
            intent.putExtra("mode_type", "single");
            startActivity(intent);
        });
        return view;
    }
}