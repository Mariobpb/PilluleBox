package com.example.pillulebox.Fragments.EditSchedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pillulebox.General;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import AsyncTasks.UpdateSingleModeTask;
import Models.Dispenser;
import Models.ScheduleModes.SingleMode;

public class EditSingleModeFragment extends Fragment {

    private static final String ARG_MODE = "single_mode";
    private SingleMode singleMode;
    private EditText medicineNameInput;
    private EditText dateTimeInput;
    private Button saveButton;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormatter;

    public EditSingleModeFragment() {
        // Required empty public constructor
    }

    public static EditSingleModeFragment newInstance(SingleMode mode) {
        EditSingleModeFragment fragment = new EditSingleModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            singleMode = (SingleMode) getArguments().getSerializable(ARG_MODE);
        }
        calendar = Calendar.getInstance();
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_single_mode, container, false);

        medicineNameInput = view.findViewById(R.id.medicine_name_input);
        dateTimeInput = view.findViewById(R.id.datetime_input);
        saveButton = view.findViewById(R.id.save_button);

        // Set current values
        if (singleMode != null) {
            medicineNameInput.setText(singleMode.getMedicineName());
            dateTimeInput.setText(dateTimeFormatter.format(singleMode.getDispensingDate()));
        }

        // Setup datetime picker
        dateTimeInput.setOnClickListener(v -> showDateTimePicker());

        // Setup save button
        saveButton.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                dateTimeInput.setText(dateTimeFormatter.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveChanges() {
        String medicineName = medicineNameInput.getText().toString().trim();
        String dateTimeStr = dateTimeInput.getText().toString().trim();

        if (medicineName.isEmpty()) {
            medicineNameInput.setError("El nombre del medicamento es requerido");
            return;
        }

        try {
            Date dispensingDate = dateTimeFormatter.parse(dateTimeStr);
            singleMode.setMedicineName(medicineName);
            singleMode.setDispensingDate(dispensingDate);

            String token = General.getToken(getContext());
            Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(requireContext());
            if (selectedDispenser != null) {
                String macAddress = selectedDispenser.getMac();
                new UpdateSingleModeTask(requireContext(), token, macAddress, singleMode,
                        new UpdateSingleModeTask.UpdateCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireContext(), "Modo actualizado exitosamente",
                                        Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(requireContext(),
                                        "Error al actualizar: " + error, Toast.LENGTH_LONG).show();
                            }
                        }).execute();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Error al procesar la fecha", Toast.LENGTH_SHORT).show();
        }
    }
}