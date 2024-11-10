package com.example.pillulebox.Fragments.DefineSchedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pillulebox.GeneralInfo;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import AsyncTasks.Schedules.AddSingleModeTask;
import AsyncTasks.Schedules.UpdateSingleModeTask;
import Models.Dispenser;
import Models.ScheduleModes.SingleMode;

public class DefineSingleModeFragment extends Fragment {

    private static final String ARG_MODE = "single_mode";
    private static final String TAG = "DefineSingleModeFragment";
    private SingleMode singleMode;
    private EditText medicineNameInput;
    private EditText dateTimeInput;
    private Button saveButton;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormatter;
    private boolean isEditingMode = false;

    public DefineSingleModeFragment() {
        // Required empty public constructor
    }

    public static DefineSingleModeFragment newInstance(SingleMode mode) {
        DefineSingleModeFragment fragment = new DefineSingleModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();

        if (getArguments() != null) {
            singleMode = (SingleMode) getArguments().getSerializable(ARG_MODE);
            isEditingMode = singleMode != null;
            if (isEditingMode) {
                calendar.setTime(singleMode.getDispensingDate());
            }
        }
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_single_mode, container, false);
        initializeViews(view);
        if (isEditingMode) setCurrentValues();
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        medicineNameInput = view.findViewById(R.id.medicine_name_input);
        dateTimeInput = view.findViewById(R.id.datetime_input);
        saveButton = view.findViewById(R.id.save_button);
    }

    private void setCurrentValues() {
        if (singleMode != null) {
            medicineNameInput.setText(singleMode.getMedicineName());
            dateTimeInput.setText(dateTimeFormatter.format(singleMode.getDispensingDate()));
        }
    }

    private void setupListeners() {
        dateTimeInput.setOnClickListener(v -> showDateTimePicker());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private boolean isValidFutureDateTime(Date selectedDate) {

        Calendar now = Calendar.getInstance();

        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar selected = Calendar.getInstance();
        selected.setTime(selectedDate);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);

        return selected.after(now);
    }

    private void showDateTimePicker() {
        Calendar minDate = Calendar.getInstance();

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

                                if (isValidFutureDateTime(calendar.getTime())) {
                                    dateTimeInput.setText(dateTimeFormatter.format(calendar.getTime()));
                                    dateTimeInput.setError(null);
                                } else {
                                    dateTimeInput.setText("");
                                    dateTimeInput.setError("La fecha y hora deben ser posteriores a la actual");
                                    Toast.makeText(requireContext(),
                                            "Seleccione una fecha y hora futuras",
                                            Toast.LENGTH_SHORT).show();
                                }
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

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void saveChanges() {
        String medicineName = medicineNameInput.getText().toString().trim();
        String dateTimeStr = dateTimeInput.getText().toString().trim();

        if (medicineName.isEmpty()) {
            medicineNameInput.setError("El nombre del medicamento es requerido");
            return;
        }
        if (dateTimeStr.isEmpty()) {
            dateTimeInput.setError("La fecha y hora son requeridas");
            return;
        }

        try {
            Date dispensingDate = dateTimeFormatter.parse(dateTimeStr);
            if (!isValidFutureDateTime(dispensingDate)) {
                dateTimeInput.setError("La fecha y hora deben ser posteriores a la actual");
                Toast.makeText(requireContext(),
                        "La fecha y hora seleccionadas ya han pasado",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (isEditingMode && singleMode != null) {
                singleMode.setMedicineName(medicineName);
                singleMode.setDispensingDate(dispensingDate);
            } else {
                singleMode = new SingleMode(medicineName, dispensingDate);
            }

            String token = GeneralInfo.getToken(getContext());
            Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(requireContext());

            if (selectedDispenser == null) {
                Toast.makeText(requireContext(),
                        "No se ha seleccionado un dispensador", Toast.LENGTH_LONG).show();
                return;
            }

            String macAddress = selectedDispenser.getMac();
            if (isEditingMode) {
                new UpdateSingleModeTask(requireContext(), token, macAddress, singleMode,
                        new UpdateSingleModeTask.UpdateCallback() {
                            @Override
                            public void onSuccess() {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                if (getContext() != null) {
                                    Toast.makeText(requireContext(),
                                            "Error al actualizar: " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
            } else {
                new AddSingleModeTask(requireContext(), token, macAddress, singleMode,
                        new AddSingleModeTask.AddCallback() {
                            @Override
                            public void onSuccess() {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                if (getContext() != null) {
                                    Toast.makeText(requireContext(),
                                            "Error al añadir: " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Error al procesar la fecha: " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
    }
}