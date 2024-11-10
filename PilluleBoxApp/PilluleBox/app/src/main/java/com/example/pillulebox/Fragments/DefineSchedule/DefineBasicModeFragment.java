package com.example.pillulebox.Fragments.DefineSchedule;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pillulebox.GeneralInfo;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import AsyncTasks.Schedules.AddBasicModeTask;
import AsyncTasks.Schedules.UpdateBasicModeTask;
import Models.Dispenser;
import Models.ScheduleModes.BasicMode;

public class DefineBasicModeFragment extends Fragment {

    private static final String ARG_MODE = "basic_mode";
    private static final String TAG = "DefineBasicModeFragment";
    private BasicMode basicMode;
    private boolean isEditingMode = false;
    private EditText medicineNameInput;
    private EditText startMorningHour;
    private EditText endMorningHour;
    private EditText startAfternoonHour;
    private EditText endAfternoonHour;
    private EditText startNightHour;
    private EditText endNightHour;
    private Button saveButton;
    private SimpleDateFormat timeFormatter;
    private Calendar calendar;

    private static final int MORNING_START = 4;
    private static final int MORNING_END = 11;
    private static final int AFTERNOON_START = 12;
    private static final int AFTERNOON_END = 19;
    private static final int NIGHT_START = 20;
    private static final int NIGHT_END = 3;

    public DefineBasicModeFragment() {
    }

    public static DefineBasicModeFragment newInstance(BasicMode mode) {
        DefineBasicModeFragment fragment = new DefineBasicModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            basicMode = (BasicMode) getArguments().getSerializable(ARG_MODE);
            isEditingMode = basicMode != null;
        }
        calendar = Calendar.getInstance();
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_define_basic_mode, container, false);
        initializeViews(view);
        setupInputs();
        setCurrentValues();
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        medicineNameInput = view.findViewById(R.id.medicine_name_input);
        startMorningHour = view.findViewById(R.id.start_morning_hour_input);
        endMorningHour = view.findViewById(R.id.end_morning_hour_input);
        startAfternoonHour = view.findViewById(R.id.start_afternoon_hour_input);
        endAfternoonHour = view.findViewById(R.id.end_afternoon_hour_input);
        startNightHour = view.findViewById(R.id.start_night_hour_input);
        endNightHour = view.findViewById(R.id.end_night_hour_input);
        saveButton = view.findViewById(R.id.save_button);
    }

    private void setupInputs() {
        startMorningHour.setInputType(InputType.TYPE_NULL);
        endMorningHour.setInputType(InputType.TYPE_NULL);
        startAfternoonHour.setInputType(InputType.TYPE_NULL);
        endAfternoonHour.setInputType(InputType.TYPE_NULL);
        startNightHour.setInputType(InputType.TYPE_NULL);
        endNightHour.setInputType(InputType.TYPE_NULL);
    }

    private void setCurrentValues() {
        if (basicMode != null) {
            medicineNameInput.setText(basicMode.getMedicineName());

            if (basicMode.getMorningStartTime() != null) {
                startMorningHour.setText(timeFormatter.format(basicMode.getMorningStartTime()));
            }
            if (basicMode.getMorningEndTime() != null) {
                endMorningHour.setText(timeFormatter.format(basicMode.getMorningEndTime()));
            }
            if (basicMode.getAfternoonStartTime() != null) {
                startAfternoonHour.setText(timeFormatter.format(basicMode.getAfternoonStartTime()));
            }
            if (basicMode.getAfternoonEndTime() != null) {
                endAfternoonHour.setText(timeFormatter.format(basicMode.getAfternoonEndTime()));
            }
            if (basicMode.getNightStartTime() != null) {
                startNightHour.setText(timeFormatter.format(basicMode.getNightStartTime()));
            }
            if (basicMode.getNightEndTime() != null) {
                endNightHour.setText(timeFormatter.format(basicMode.getNightEndTime()));
            }
        }
    }

    private void setupListeners() {
        startMorningHour.setOnClickListener(v -> showTimePicker(startMorningHour, MORNING_START, MORNING_END));
        endMorningHour.setOnClickListener(v -> showTimePicker(endMorningHour, MORNING_START, MORNING_END));
        startAfternoonHour.setOnClickListener(v -> showTimePicker(startAfternoonHour, AFTERNOON_START, AFTERNOON_END));
        endAfternoonHour.setOnClickListener(v -> showTimePicker(endAfternoonHour, AFTERNOON_START, AFTERNOON_END));
        startNightHour.setOnClickListener(v -> showTimePicker(startNightHour, NIGHT_START, 23));
        endNightHour.setOnClickListener(v -> showTimePicker(endNightHour, 0, NIGHT_END));
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void showTimePicker(EditText targetInput, int minHour, int maxHour) {
        Calendar currentTime = Calendar.getInstance();
        new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    if (isTimeInRange(hourOfDay, minHour, maxHour)) {
                        currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        currentTime.set(Calendar.MINUTE, minute);
                        targetInput.setText(timeFormatter.format(currentTime.getTime()));
                    } else {
                        Toast.makeText(requireContext(),
                                "La hora debe estar entre " + formatHour(minHour) + " y " + formatHour(maxHour),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
        ).show();
    }

    private boolean isTimeInRange(int hour, int minHour, int maxHour) {
        if (minHour < maxHour) {
            return hour >= minHour && hour <= maxHour;
        } else {
            // Caso horario nocturno
            return hour >= minHour || hour <= maxHour;
        }
    }

    private String formatHour(int hour) {
        return String.format(Locale.getDefault(), "%02d:00", hour);
    }

    private boolean validateTimeOrder() {
        try {
            Time morningStart = Time.valueOf(startMorningHour.getText().toString() + ":00");
            Time morningEnd = Time.valueOf(endMorningHour.getText().toString() + ":00");
            Time afternoonStart = Time.valueOf(startAfternoonHour.getText().toString() + ":00");
            Time afternoonEnd = Time.valueOf(endAfternoonHour.getText().toString() + ":00");
            Time nightStart = Time.valueOf(startNightHour.getText().toString() + ":00");
            Time nightEnd = Time.valueOf(endNightHour.getText().toString() + ":00");

            if (morningStart.after(morningEnd)) {
                showError("La hora de inicio de la mañana debe ser menor que la hora de fin");
                return false;
            }
            if (afternoonStart.after(afternoonEnd)) {
                showError("La hora de inicio de la tarde debe ser menor que la hora de fin");
                return false;
            }

            if ((nightStart.before(Time.valueOf("20:00:00")) && nightStart.after(Time.valueOf("3:58:00"))) || (nightEnd.after(Time.valueOf("03:59:00")) && nightEnd.before(Time.valueOf("20:01:00")))) {
                showError("El horario nocturno debe estar entre las 20:00 y las 03:59");
                return false;
            }

            return true;
        } catch (Exception e) {
            showError("Error al validar las horas");
            return false;
        }
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveChanges() {
        String medicineName = medicineNameInput.getText().toString().trim();

        if (medicineName.isEmpty()) {
            medicineNameInput.setError("El nombre del medicamento es requerido");
            return;
        }

        if (!validateTimeOrder()) {
            return;
        }

        try {
            if (isEditingMode && basicMode != null) {
                // Actualizar el modo existente
                basicMode.setMedicineName(medicineName);
                basicMode.setMorningStartTime(Time.valueOf(startMorningHour.getText().toString() + ":00"));
                basicMode.setMorningEndTime(Time.valueOf(endMorningHour.getText().toString() + ":00"));
                basicMode.setAfternoonStartTime(Time.valueOf(startAfternoonHour.getText().toString() + ":00"));
                basicMode.setAfternoonEndTime(Time.valueOf(endAfternoonHour.getText().toString() + ":00"));
                basicMode.setNightStartTime(Time.valueOf(startNightHour.getText().toString() + ":00"));
                basicMode.setNightEndTime(Time.valueOf(endNightHour.getText().toString() + ":00"));
            } else {
                // Crear un nuevo modo
                basicMode = new BasicMode(
                        medicineName,
                        Time.valueOf(startMorningHour.getText().toString() + ":00"),
                        Time.valueOf(endMorningHour.getText().toString() + ":00"),
                        Time.valueOf(startAfternoonHour.getText().toString() + ":00"),
                        Time.valueOf(endAfternoonHour.getText().toString() + ":00"),
                        Time.valueOf(startNightHour.getText().toString() + ":00"),
                        Time.valueOf(endNightHour.getText().toString() + ":00")
                );
            }

            String token = GeneralInfo.getToken(getContext());
            Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(requireContext());

            if (selectedDispenser == null) {
                Toast.makeText(requireContext(),
                        "No se ha seleccionado un dispensador",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String macAddress = selectedDispenser.getMac();
            if (isEditingMode) {
                new UpdateBasicModeTask(requireContext(), token, macAddress, basicMode,
                        new UpdateBasicModeTask.UpdateCallback() {
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
                                            "Error al actualizar: " + error,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
            } else {
                new AddBasicModeTask(requireContext(), token, macAddress, basicMode,
                        new AddBasicModeTask.AddCallback() {
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
                                            "Error al añadir: " + error,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Error al procesar los datos: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}