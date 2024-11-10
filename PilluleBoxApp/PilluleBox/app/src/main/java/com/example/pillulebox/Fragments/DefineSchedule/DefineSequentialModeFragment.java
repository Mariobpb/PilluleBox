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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.text.InputType;
import android.text.TextUtils;

import com.example.pillulebox.GeneralInfo;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.util.Locale;

import AsyncTasks.Schedules.AddSequentialModeTask;
import AsyncTasks.Schedules.UpdateSequentialModeTask;
import Models.Dispenser;
import Models.ScheduleModes.SequentialMode;

public class DefineSequentialModeFragment extends Fragment {

    private static final String ARG_MODE = "sequential_mode";
    private static final String TAG = "DefineSequentialModeFragment";
    private SequentialMode sequentialMode;
    private boolean isEditingMode = false;
    private EditText medicineNameInput;
    private EditText startDateTimeInput;
    private EditText endDateTimeInput;
    private EditText periodTimeInput;
    private EditText limitConsumptionInput;
    private CheckBox affectPeriodsInput;
    private Button saveButton;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private SimpleDateFormat dateTimeFormatter;
    private SimpleDateFormat timeFormatter;

    public DefineSequentialModeFragment() {
        // Required empty public constructor
    }

    public static DefineSequentialModeFragment newInstance(SequentialMode mode) {
        DefineSequentialModeFragment fragment = new DefineSequentialModeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        if (getArguments() != null) {
            sequentialMode = (SequentialMode) getArguments().getSerializable(ARG_MODE);
            isEditingMode = sequentialMode != null;
            if (isEditingMode) {
                startCalendar.setTime(sequentialMode.getStartDate());
                endCalendar.setTime(sequentialMode.getEndDate());
            }
        }

        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_sequential_mode, container, false);

        initializeViews(view);
        setupInputs();
        setCurrentValues();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        medicineNameInput = view.findViewById(R.id.medicine_name_input);
        startDateTimeInput = view.findViewById(R.id.start_datetime_input);
        endDateTimeInput = view.findViewById(R.id.end_datetime_input);
        periodTimeInput = view.findViewById(R.id.period_time_input);
        limitConsumptionInput = view.findViewById(R.id.limit_consumption_input);
        affectPeriodsInput = view.findViewById(R.id.affect_periods_input);
        saveButton = view.findViewById(R.id.save_button);
    }

    private void setupInputs() {
        startDateTimeInput.setInputType(InputType.TYPE_NULL);
        endDateTimeInput.setInputType(InputType.TYPE_NULL);
        periodTimeInput.setInputType(InputType.TYPE_NULL);

        limitConsumptionInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        affectPeriodsInput.setChecked(false);
    }

    private void setCurrentValues() {
        if (isEditingMode && sequentialMode != null) {
            medicineNameInput.setText(sequentialMode.getMedicineName());

            if (sequentialMode.getStartDate() != null) {
                startDateTimeInput.setText(dateTimeFormatter.format(sequentialMode.getStartDate()));
            }

            if (sequentialMode.getEndDate() != null) {
                endDateTimeInput.setText(dateTimeFormatter.format(sequentialMode.getEndDate()));
            }

            if (sequentialMode.getPeriod() != null) {
                periodTimeInput.setText(timeFormatter.format(sequentialMode.getPeriod()));
            }

            if (sequentialMode.getLimitTimesConsumption() > 0) {
                limitConsumptionInput.setText(String.valueOf(sequentialMode.getLimitTimesConsumption()));
            }

            affectPeriodsInput.setChecked(sequentialMode.isAffectedPeriods());
        }
    }

    private void setupListeners() {
        startDateTimeInput.setOnClickListener(v -> showDateTimePicker(startCalendar, startDateTimeInput));
        endDateTimeInput.setOnClickListener(v -> showDateTimePicker(endCalendar, endDateTimeInput));
        periodTimeInput.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void showDateTimePicker(Calendar calendar, EditText targetInput) {
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
                                targetInput.setText(dateTimeFormatter.format(calendar.getTime()));
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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar timeCalendar = Calendar.getInstance();
        new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    timeCalendar.set(Calendar.MINUTE, minute);
                    periodTimeInput.setText(timeFormatter.format(timeCalendar.getTime()));
                },
                timeCalendar.get(Calendar.HOUR_OF_DAY),
                timeCalendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void saveChanges() {
        String medicineName = medicineNameInput.getText().toString().trim();
        String startDateTimeStr = startDateTimeInput.getText().toString().trim();
        String endDateTimeStr = endDateTimeInput.getText().toString().trim();
        String periodTimeStr = periodTimeInput.getText().toString().trim();
        String limitConsumptionStr = limitConsumptionInput.getText().toString().trim();
        boolean affectPeriods = affectPeriodsInput.isChecked();

        if (medicineName.isEmpty()) {
            medicineNameInput.setError("El nombre del medicamento es requerido");
            return;
        }
        if (startDateTimeStr.isEmpty()) {
            startDateTimeInput.setError("La fecha de inicio es requerida");
            return;
        }
        if (endDateTimeStr.isEmpty()) {
            endDateTimeInput.setError("La fecha de fin es requerida");
            return;
        }
        if (periodTimeStr.isEmpty()) {
            periodTimeInput.setError("El período es requerido");
            return;
        }

        if (endCalendar.before(startCalendar)) {
            endDateTimeInput.setError("La fecha final debe ser posterior a la fecha inicial");
            return;
        }

        try {
            Date startDate = new Date(startCalendar.getTimeInMillis());
            Date endDate = new Date(endCalendar.getTimeInMillis());

            Calendar periodCalendar = Calendar.getInstance();
            periodCalendar.setTime(timeFormatter.parse(periodTimeStr));
            Time period = new Time(periodCalendar.getTimeInMillis());

            if (isEditingMode && sequentialMode != null) {
                sequentialMode.setMedicineName(medicineName);
                sequentialMode.setStartDate(startDate);
                sequentialMode.setEndDate(endDate);
                sequentialMode.setPeriod(period);
                sequentialMode.setAffectedPeriods(affectPeriods);

                if (!TextUtils.isEmpty(limitConsumptionStr)) {
                    sequentialMode.setLimitTimesConsumption(Integer.parseInt(limitConsumptionStr));
                } else {
                    sequentialMode.setLimitTimesConsumption(0);
                }
            } else {
                sequentialMode = new SequentialMode(medicineName, startDate, endDate, period,
                        TextUtils.isEmpty(limitConsumptionStr) ? 0 : Integer.parseInt(limitConsumptionStr), affectPeriods);
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
                new UpdateSequentialModeTask(requireContext(), token, macAddress, sequentialMode,
                        new UpdateSequentialModeTask.UpdateCallback() {
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
                new AddSequentialModeTask(requireContext(), token, macAddress, sequentialMode,
                        new AddSequentialModeTask.AddCallback() {
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
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
    }
}