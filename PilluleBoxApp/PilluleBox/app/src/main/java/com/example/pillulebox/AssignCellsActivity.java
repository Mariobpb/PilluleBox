package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import AsyncTasks.GetDispenserCellsTask;
import AsyncTasks.UpdateCellsTask;
import Managers.CellStateManager;
import Models.Cell;
import Models.Dispenser;
import Models.ScheduleModes.BasicMode;
import Models.ScheduleModes.SequentialMode;
import Models.ScheduleModes.SingleMode;

public class AssignCellsActivity extends AppCompatActivity {
    private String modeType;
    private Integer currentModeId;
    private Toolbar toolbar;
    private View[] cells = new View[14];
    private AppCompatButton saveButton;
    private List<Integer> selectedCells;
    private List<Cell> initialCells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_cells);

        selectedCells = new ArrayList<>();
        initializeViews();
        setupToolbar();
        restartCells();
        getModeFromExtras();
        loadDispenserInfo();
        setupClickListeners();
        setupButton();
    }

    private void initializeViews() {
        int[] cellIds = {
                R.id.cell_1, R.id.cell_2, R.id.cell_3, R.id.cell_4, R.id.cell_5,
                R.id.cell_6, R.id.cell_7, R.id.cell_8, R.id.cell_9, R.id.cell_10,
                R.id.cell_11, R.id.cell_12, R.id.cell_13, R.id.cell_14,
        };
        for (int i = 0; i < cells.length; i++) {
            cells[i] = findViewById(cellIds[i]);
        }
        saveButton = findViewById(R.id.save_button);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void restartCells() {
        for (View cell : cells) {
            CellStateManager.updateCellState(cell, CellStateManager.STATE_EMPTY,
                    CellStateManager.SECONDARY_STATE_NONE, this);
        }
    }

    private void getModeFromExtras() {
        Intent intent = getIntent();
        modeType = intent.getStringExtra("mode_type");

        if (modeType != null) {
            switch (modeType) {
                case "single":
                    SingleMode singleMode = (SingleMode) intent.getSerializableExtra("mode");
                    if (singleMode != null) {
                        currentModeId = singleMode.getId();
                    }
                    break;
                case "sequential":
                    SequentialMode sequentialMode = (SequentialMode) intent.getSerializableExtra("mode");
                    if (sequentialMode != null) {
                        currentModeId = sequentialMode.getId();
                    }
                    break;
                case "basic":
                    BasicMode basicMode = (BasicMode) intent.getSerializableExtra("mode");
                    if (basicMode != null) {
                        currentModeId = basicMode.getId();
                    }
                    break;
            }
        }
    }

    private void setupClickListeners() {
        for (int i = 0; i < cells.length; i++) {
            final int cellIndex = i;
            cells[i].setOnClickListener(v -> handleCellClick(cellIndex));
        }
    }

    private void handleCellClick(int cellIndex) {
        if (initialCells == null || cellIndex >= initialCells.size()) {
            return;
        }

        Cell cellInfo = initialCells.get(cellIndex);
        if (cellInfo == null) {
            return;
        }

        boolean hadOriginalMode = false;
        switch (modeType) {
            case "single":
                hadOriginalMode = cellInfo.getSingleModeId() != null &&
                        cellInfo.getSingleModeId().equals(currentModeId);
                break;
            case "sequential":
                hadOriginalMode = cellInfo.getSequentialModeId() != null &&
                        cellInfo.getSequentialModeId().equals(currentModeId);
                break;
            case "basic":
                hadOriginalMode = cellInfo.getBasicModeId() != null &&
                        cellInfo.getBasicModeId().equals(currentModeId);
                break;
        }

        if (selectedCells.contains(cellIndex)) {
            selectedCells.remove(Integer.valueOf(cellIndex));

            if (hadOriginalMode) {
                int mainState = cellInfo.getCurrentMedicineDate() != null ?
                        CellStateManager.STATE_AVAILABLE : CellStateManager.STATE_EMPTY;
                int secondaryState = isSevenDaysAgo(cellInfo.getCurrentMedicineDate()) ?
                        CellStateManager.SECONDARY_STATE_WARNING : CellStateManager.SECONDARY_STATE_NONE;

                CellStateManager.updateCellState(cells[cellIndex], mainState, secondaryState, this);
            } else {
                CellStateManager.updateCellState(cells[cellIndex],
                        defineMainColorCell(cellInfo),
                        defineSecondaryColorCell(cellInfo),
                        this);
            }
        } else {
            selectedCells.add(cellIndex);
            CellStateManager.updateCellState(cells[cellIndex],
                    CellStateManager.STATE_SELECTED_CELL,
                    defineSecondaryColorCell(cellInfo),
                    this);
        }
    }

    private void setupButton() {
        saveButton.setOnClickListener(v -> {
            List<Integer> cellsWithExistingModes = new ArrayList<>();

            for (Integer selectedIndex : selectedCells) {
                Cell cell = initialCells.get(selectedIndex);
                boolean hasCurrentMode = false;

                switch (modeType) {
                    case "single":
                        hasCurrentMode = cell.getSingleModeId() != null &&
                                cell.getSingleModeId().equals(currentModeId);
                        break;
                    case "sequential":
                        hasCurrentMode = cell.getSequentialModeId() != null &&
                                cell.getSequentialModeId().equals(currentModeId);
                        break;
                    case "basic":
                        hasCurrentMode = cell.getBasicModeId() != null &&
                                cell.getBasicModeId().equals(currentModeId);
                        break;
                }
                if ((cell.getSingleModeId() != null ||
                        cell.getSequentialModeId() != null ||
                        cell.getBasicModeId() != null) && !hasCurrentMode) {
                    cellsWithExistingModes.add(cell.getNumCell());
                }
            }
            if (!cellsWithExistingModes.isEmpty()) {
                showOverwriteWarning(cellsWithExistingModes, () -> proceedWithSave());
            } else {
                proceedWithSave();
            }
        });
    }

    private void showOverwriteWarning(List<Integer> cellsWithModes, Runnable onConfirm) {
        StringBuilder message = new StringBuilder();
        message.append("Las siguientes celdas ya tienen un modo de horario asignado:\n\n");

        for (Integer cellNum : cellsWithModes) {
            Cell cell = initialCells.get(cellNum - 1);
            message.append("- Celda ").append(cellNum).append(": ");

            if (cell.getSingleModeId() != null) {
                message.append("Modo Único");
            } else if (cell.getSequentialModeId() != null) {
                message.append("Modo Secuencial");
            } else if (cell.getBasicModeId() != null) {
                message.append("Modo Básico");
            }
            message.append("\n");
        }

        message.append("\n¿Desea sobreescribir los modos existentes?");

        new AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage(message.toString())
                .setPositiveButton("Continuar", (dialog, which) -> {
                    dialog.dismiss();
                    onConfirm.run();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void proceedWithSave() {
        List<Cell> cellsToUpdate = new ArrayList<>();

        for (Cell originalCell : initialCells) {
            boolean hadOriginallyCurrentMode = false;
            boolean isSelected = selectedCells.contains(originalCell.getNumCell() - 1);

            switch (modeType) {
                case "single":
                    hadOriginallyCurrentMode = originalCell.getSingleModeId() != null &&
                            originalCell.getSingleModeId().equals(currentModeId);
                    break;
                case "sequential":
                    hadOriginallyCurrentMode = originalCell.getSequentialModeId() != null &&
                            originalCell.getSequentialModeId().equals(currentModeId);
                    break;
                case "basic":
                    hadOriginallyCurrentMode = originalCell.getBasicModeId() != null &&
                            originalCell.getBasicModeId().equals(currentModeId);
                    break;
            }

            if ((hadOriginallyCurrentMode && !isSelected) || isSelected) {
                Cell updatedCell = new Cell(
                        originalCell.getId(),
                        originalCell.getMacDispenser(),
                        originalCell.getNumCell(),
                        originalCell.getCurrentMedicineDate(),
                        null,
                        null,
                        null
                );

                if (isSelected) {
                    switch (modeType) {
                        case "single":
                            updatedCell = new Cell(
                                    originalCell.getId(),
                                    originalCell.getMacDispenser(),
                                    originalCell.getNumCell(),
                                    originalCell.getCurrentMedicineDate(),
                                    currentModeId,
                                    null,
                                    null
                            );
                            break;
                        case "sequential":
                            updatedCell = new Cell(
                                    originalCell.getId(),
                                    originalCell.getMacDispenser(),
                                    originalCell.getNumCell(),
                                    originalCell.getCurrentMedicineDate(),
                                    null,
                                    currentModeId,
                                    null
                            );
                            break;
                        case "basic":
                            updatedCell = new Cell(
                                    originalCell.getId(),
                                    originalCell.getMacDispenser(),
                                    originalCell.getNumCell(),
                                    originalCell.getCurrentMedicineDate(),
                                    null,
                                    null,
                                    currentModeId
                            );
                            break;
                    }
                }

                cellsToUpdate.add(updatedCell);
            }
        }

        if (cellsToUpdate.isEmpty()) {
            finish();
            return;
        }

        String token = GeneralInfo.getToken(this);
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);

        new UpdateCellsTask(this, token, selectedDispenser.getMac(), cellsToUpdate,
                new UpdateCellsTask.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> finish());
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(AssignCellsActivity.this,
                                    "Error al actualizar las celdas: " + error,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                }).execute();
    }

    public void loadDispenserInfo() {
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);
        if (selectedDispenser != null) {
            String token = GeneralInfo.getToken(this);

            new GetDispenserCellsTask(this, token, selectedDispenser.getMac(),
                    new GetDispenserCellsTask.CellsCallback() {
                        @Override
                        public void onCellsLoaded(List<Cell> cells) {
                            initialCells = cells;
                            updateCells(initialCells);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AssignCellsActivity.this,
                                    "Error al cargar las celdas: " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).execute();
        }
    }

    private int defineMainColorCell(Cell cellInfo) {
        if (cellInfo == null) {
            return CellStateManager.STATE_EMPTY;
        }
        if (selectedCells.contains(cellInfo.getNumCell() - 1)) {
            return CellStateManager.STATE_SELECTED_CELL;
        }
        if ((modeType.equals("single") && cellInfo.getSingleModeId() != null &&
                cellInfo.getSingleModeId().equals(currentModeId)) ||
                (modeType.equals("sequential") && cellInfo.getSequentialModeId() != null &&
                        cellInfo.getSequentialModeId().equals(currentModeId)) ||
                (modeType.equals("basic") && cellInfo.getBasicModeId() != null &&
                        cellInfo.getBasicModeId().equals(currentModeId))) {
            return CellStateManager.STATE_SELECTED_CELL;
        }

        if (cellInfo.getSingleModeId() != null) {
            return CellStateManager.STATE_SINGLE_MODE;
        }
        if (cellInfo.getSequentialModeId() != null) {
            return CellStateManager.STATE_SEQUENTIAL_MODE;
        }
        if (cellInfo.getBasicModeId() != null) {
            return CellStateManager.STATE_BASIC_MODE;
        }
        if (cellInfo.getCurrentMedicineDate() != null) {
            return CellStateManager.STATE_AVAILABLE;
        }

        return CellStateManager.STATE_EMPTY;
    }

    private int defineSecondaryColorCell(Cell cellInfo) {
        if (cellInfo.getCurrentMedicineDate() != null &&
                isSevenDaysAgo(cellInfo.getCurrentMedicineDate())) {
            return CellStateManager.SECONDARY_STATE_WARNING;
        }

        if ((cellInfo.getSingleModeId() != null ||
                cellInfo.getSequentialModeId() != null ||
                cellInfo.getBasicModeId() != null) &&
                (cellInfo.getCurrentMedicineDate() == null)) {
            return CellStateManager.SECONDARY_STATE_REQUIRED;
        }

        return CellStateManager.SECONDARY_STATE_NONE;
    }

    private void updateCells(List<Cell> cells) {
        for (Cell cellInfo : cells) {
            int cellIndex = cellInfo.getNumCell() - 1;
            if (cellIndex >= 0 && cellIndex < this.cells.length) {
                boolean hasCurrentMode = false;
                switch (modeType) {
                    case "single":
                        hasCurrentMode = cellInfo.getSingleModeId() != null &&
                                cellInfo.getSingleModeId().equals(currentModeId);
                        break;
                    case "sequential":
                        hasCurrentMode = cellInfo.getSequentialModeId() != null &&
                                cellInfo.getSequentialModeId().equals(currentModeId);
                        break;
                    case "basic":
                        hasCurrentMode = cellInfo.getBasicModeId() != null &&
                                cellInfo.getBasicModeId().equals(currentModeId);
                        break;
                }
                if (hasCurrentMode && !selectedCells.contains(cellIndex)) {
                    selectedCells.add(cellIndex);
                }
                CellStateManager.updateCellState(
                        this.cells[cellIndex],
                        defineMainColorCell(cellInfo),
                        defineSecondaryColorCell(cellInfo),
                        this
                );
            }
        }
    }

    private boolean isSevenDaysAgo(Date date) {
        if (date == null) {
            return false;
        }
        Date currentDate = new Date();
        long diffInMillies = currentDate.getTime() - date.getTime();
        long diffInDays = diffInMillies / (86400000);
        return diffInDays >= 7;
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
