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
    private static final String TAG = "AssignCellsActivity";
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

        Log.d(TAG, "Activity created");
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
        Log.d(TAG, "Initializing views");
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
        Log.d(TAG, "Restarting cells to initial state");
        for (View cell : cells) {
            CellStateManager.updateCellState(cell, CellStateManager.STATE_EMPTY,
                    CellStateManager.SECONDARY_STATE_NONE, this);
        }
    }

    private void getModeFromExtras() {
        Intent intent = getIntent();
        modeType = intent.getStringExtra("mode_type");
        Log.d(TAG, "Getting mode from extras: " + modeType);

        if (modeType != null) {
            switch (modeType) {
                case "single":
                    SingleMode singleMode = (SingleMode) intent.getSerializableExtra("mode");
                    if (singleMode != null) {
                        currentModeId = singleMode.getId();
                        Log.d(TAG, "Single mode ID: " + currentModeId);
                    }
                    break;
                case "sequential":
                    SequentialMode sequentialMode = (SequentialMode) intent.getSerializableExtra("mode");
                    if (sequentialMode != null) {
                        currentModeId = sequentialMode.getId();
                        Log.d(TAG, "Sequential mode ID: " + currentModeId);
                    }
                    break;
                case "basic":
                    BasicMode basicMode = (BasicMode) intent.getSerializableExtra("mode");
                    if (basicMode != null) {
                        currentModeId = basicMode.getId();
                        Log.d(TAG, "Basic mode ID: " + currentModeId);
                    }
                    break;
            }
        }
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners");
        for (int i = 0; i < cells.length; i++) {
            final int cellIndex = i;
            cells[i].setOnClickListener(v -> handleCellClick(cellIndex));
        }
    }

    private void handleCellClick(int cellIndex) {
        Log.d(TAG, "Cell clicked: " + (cellIndex + 1));
        if (initialCells == null || cellIndex >= initialCells.size()) {
            Log.e(TAG, "Invalid cell index or currentCells is null");
            return;
        }

        Cell cellInfo = initialCells.get(cellIndex);
        if (cellInfo == null) {
            Log.e(TAG, "Cell info is null for index: " + cellIndex);
            return;
        }

        boolean hadOriginalMode = false;
        // Verificar si la celda tenía originalmente el modo actual
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

        Log.d(TAG, "Cell " + (cellIndex + 1) + " " +
                (selectedCells.contains(cellIndex) ? "selected" : "deselected"));
    }

    private void setupButton() {
        saveButton.setOnClickListener(v -> {
            List<Integer> cellsWithExistingModes = new ArrayList<>();

            // Verificamos las celdas seleccionadas que ya tienen modos asignados
            for (Integer selectedIndex : selectedCells) {
                Cell cell = initialCells.get(selectedIndex);
                boolean hasCurrentMode = false;

                // Verificar si la celda tiene el modo actual
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

        // Agregar un log para mostrar cuántas celdas se van a procesar
        Log.d(TAG, "Procesando " + initialCells.size() + " celdas para actualización");

        for (Cell originalCell : initialCells) {
            boolean hadOriginallyCurrentMode = false;
            boolean isSelected = selectedCells.contains(originalCell.getNumCell() - 1);

            // Verificar si tenía el modo original
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

            // Log para ver el estado de cada celda
            Log.d(TAG, "Celda " + originalCell.getNumCell() +
                    " - Seleccionada: " + isSelected +
                    " - Tenía modo original: " + hadOriginallyCurrentMode);

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

                if (isSelected && !hadOriginallyCurrentMode) {
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
                            Log.d(TAG, "Asignando modo Single a celda " + originalCell.getNumCell());
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
                            Log.d(TAG, "Asignando modo Sequential a celda " + originalCell.getNumCell());
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
                            Log.d(TAG, "Asignando modo Basic a celda " + originalCell.getNumCell());
                            break;
                    }
                } else if (hadOriginallyCurrentMode && !isSelected) {
                    // Si tenía el modo pero ya no está seleccionada, lo quitamos
                    Log.d(TAG, "Quitando modo " + modeType + " de celda " + originalCell.getNumCell());
                }

                cellsToUpdate.add(updatedCell);
            }
        }

        // Log resumen con todas las celdas que se van a actualizar
        Log.d(TAG, "=== RESUMEN DE ACTUALIZACIÓN ===");
        Log.d(TAG, "Total de celdas a actualizar: " + cellsToUpdate.size());
        for (Cell cell : cellsToUpdate) {
            String action;
            if ((modeType.equals("single") && cell.getSingleModeId() != null) ||
                    (modeType.equals("sequential") && cell.getSequentialModeId() != null) ||
                    (modeType.equals("basic") && cell.getBasicModeId() != null)) {
                action = "ASIGNAR " + modeType.toUpperCase();
            } else {
                action = "QUITAR MODO";
            }
            Log.d(TAG, "Celda " + cell.getNumCell() + ": " + action);
        }

        if (cellsToUpdate.isEmpty()) {
            Log.d(TAG, "No hay celdas para actualizar. Finalizando actividad.");
            finish();
            return;
        }

        String token = GeneralInfo.getToken(this);
        Dispenser selectedDispenser = GeneralInfo.getSelectedDispenser(this);

        Log.d(TAG, "Enviando actualización al servidor para " + cellsToUpdate.size() + " celdas del dispensador " + selectedDispenser.getMac());

        new UpdateCellsTask(this, token, selectedDispenser.getMac(), cellsToUpdate,
                new UpdateCellsTask.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Actualización exitosa de celdas");
                        runOnUiThread(() -> finish());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al actualizar celdas: " + error);
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
            Log.d(TAG, "Loading dispenser info for MAC: " + selectedDispenser.getMac());

            new GetDispenserCellsTask(this, token, selectedDispenser.getMac(),
                    new GetDispenserCellsTask.CellsCallback() {
                        @Override
                        public void onCellsLoaded(List<Cell> cells) {
                            Log.d(TAG, "Cells loaded successfully: " + cells.size() + " cells");
                            initialCells = cells;
                            updateCells(initialCells);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error loading cells: " + error);
                            Toast.makeText(AssignCellsActivity.this,
                                    "Error al cargar las celdas: " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).execute();
        } else {
            Log.e(TAG, "No dispenser selected");
        }
    }

    private int defineMainColorCell(Cell cellInfo) {
        if (cellInfo == null) {
            Log.d(TAG, "Cell info is null, returning empty state");
            return CellStateManager.STATE_EMPTY;
        }
        if (selectedCells.contains(cellInfo.getNumCell() - 1)) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " is selected");
            return CellStateManager.STATE_SELECTED_CELL;
        }
        if ((modeType.equals("single") && cellInfo.getSingleModeId() != null &&
                cellInfo.getSingleModeId().equals(currentModeId)) ||
                (modeType.equals("sequential") && cellInfo.getSequentialModeId() != null &&
                        cellInfo.getSequentialModeId().equals(currentModeId)) ||
                (modeType.equals("basic") && cellInfo.getBasicModeId() != null &&
                        cellInfo.getBasicModeId().equals(currentModeId))) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " matches current mode");
            return CellStateManager.STATE_SELECTED_CELL;
        }

        // Determinar el color basado en el modo asignado
        if (cellInfo.getSingleModeId() != null) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has single mode");
            return CellStateManager.STATE_SINGLE_MODE;
        }
        if (cellInfo.getSequentialModeId() != null) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has sequential mode");
            return CellStateManager.STATE_SEQUENTIAL_MODE;
        }
        if (cellInfo.getBasicModeId() != null) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has basic mode");
            return CellStateManager.STATE_BASIC_MODE;
        }
        if (cellInfo.getCurrentMedicineDate() != null) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has medicine");
            return CellStateManager.STATE_AVAILABLE;
        }

        Log.d(TAG, "Cell " + cellInfo.getNumCell() + " is empty");
        return CellStateManager.STATE_EMPTY;
    }

    private int defineSecondaryColorCell(Cell cellInfo) {
        if (cellInfo.getCurrentMedicineDate() != null &&
                isSevenDaysAgo(cellInfo.getCurrentMedicineDate())) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has warning state");
            return CellStateManager.SECONDARY_STATE_WARNING;
        }

        if ((cellInfo.getSingleModeId() != null ||
                cellInfo.getSequentialModeId() != null ||
                cellInfo.getBasicModeId() != null) &&
                (cellInfo.getCurrentMedicineDate() == null)) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has required state");
            return CellStateManager.SECONDARY_STATE_REQUIRED;
        }

        Log.d(TAG, "Cell " + cellInfo.getNumCell() + " has no secondary state");
        return CellStateManager.SECONDARY_STATE_NONE;
    }

    private void updateCells(List<Cell> cells) {
        Log.d(TAG, "Updating cells display");
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
                    Log.d(TAG, "Cell " + cellInfo.getNumCell() + " auto-selected because it has current mode " + modeType);
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