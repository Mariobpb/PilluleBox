package com.example.pillulebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pillulebox.Fragments.DefineSchedule.DefineBasicModeFragment;
import com.example.pillulebox.Fragments.DefineSchedule.DefineSequentialModeFragment;
import com.example.pillulebox.Fragments.DefineSchedule.DefineSingleModeFragment;
import com.example.pillulebox.adapters.DispenserAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import AsyncTasks.GetDispenserCellsTask;
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
    private List<Cell> currentCells;

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
        setupSaveButton();
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

        if (currentCells == null || cellIndex >= currentCells.size()) {
            Log.e(TAG, "Invalid cell index or currentCells is null");
            return;
        }

        Cell cellInfo = currentCells.get(cellIndex);
        if (cellInfo == null) {
            Log.e(TAG, "Cell info is null for index: " + cellIndex);
            return;
        }

        // Verificar si la celda ya está seleccionada
        if (selectedCells.contains(cellIndex)) {
            // Deseleccionar la celda
            selectedCells.remove(Integer.valueOf(cellIndex));
            CellStateManager.updateCellState(cells[cellIndex],
                    defineMainColorCell(cellInfo),
                    defineSecondaryColorCell(cellInfo),
                    this);
            Log.d(TAG, "Cell " + (cellIndex + 1) + " deselected");
        } else {
            // Seleccionar la celda - Ahora mantiene el estado secundario
            selectedCells.add(cellIndex);
            CellStateManager.updateCellState(cells[cellIndex],
                    CellStateManager.STATE_SELECTED_CELL,
                    defineSecondaryColorCell(cellInfo), // Mantiene el estado secundario
                    this);
            Log.d(TAG, "Cell " + (cellIndex + 1) + " selected with secondary state");
        }
    }


    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveSelectedCells());
    }
    private void saveSelectedCells() {
        if (selectedCells.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos una celda", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Saving selected cells: " + selectedCells.toString());

        // Crear lista de celdas actualizadas
        List<Cell> cellsToUpdate = new ArrayList<>();
        for (Integer cellIndex : selectedCells) {
            Cell originalCell = currentCells.get(cellIndex);

            // Crear nueva celda con el modo actualizado
            Cell updatedCell;
            switch (modeType) {
                case "single":
                    updatedCell = new Cell(
                            originalCell.getId(),
                            originalCell.getMacDispenser(),
                            originalCell.getNumCell(),
                            originalCell.getCurrentMedicineDate(),
                            currentModeId,  // Nuevo SingleModeId
                            null,           // Limpiar SequentialModeId
                            null            // Limpiar BasicModeId
                    );
                    break;
                case "sequential":
                    updatedCell = new Cell(
                            originalCell.getId(),
                            originalCell.getMacDispenser(),
                            originalCell.getNumCell(),
                            originalCell.getCurrentMedicineDate(),
                            null,           // Limpiar SingleModeId
                            currentModeId,  // Nuevo SequentialModeId
                            null            // Limpiar BasicModeId
                    );
                    break;
                case "basic":
                    updatedCell = new Cell(
                            originalCell.getId(),
                            originalCell.getMacDispenser(),
                            originalCell.getNumCell(),
                            originalCell.getCurrentMedicineDate(),
                            null,           // Limpiar SingleModeId
                            null,           // Limpiar SequentialModeId
                            currentModeId   // Nuevo BasicModeId
                    );
                    break;
                default:
                    Log.e(TAG, "Modo no reconocido: " + modeType);
                    continue;
            }

            cellsToUpdate.add(updatedCell);

            // Actualizar la celda en currentCells para reflejar el cambio inmediatamente
            currentCells.set(cellIndex, updatedCell);
        }
        // new UpdateCellsTask(this, token, cellsToUpdate, new UpdateCellsCallback() {
        //     @Override
        //     public void onSuccess() {
        //         Toast.makeText(AssignCellsActivity.this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
        //         // Actualizar la vista
        //         updateCells(currentCells);
        //         // Limpiar selección
        //         selectedCells.clear();
        //     }
        //
        //     @Override
        //     public void onError(String error) {
        //         Toast.makeText(AssignCellsActivity.this, "Error al guardar: " + error, Toast.LENGTH_SHORT).show();
        //     }
        // }).execute();

        // Por ahora, solo mostraremos un mensaje y actualizaremos la vista
        Toast.makeText(this, "Cambios guardados localmente", Toast.LENGTH_SHORT).show();
        updateCells(currentCells);
        selectedCells.clear();
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
                            currentCells = cells;
                            updateCells(cells);
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
    private void restartCells() {
        Log.d(TAG, "Restarting cells to initial state");
        for (View cell : cells) {
            CellStateManager.updateCellState(cell, CellStateManager.STATE_EMPTY,
                    CellStateManager.SECONDARY_STATE_NONE, this);
        }
    }
    private int defineMainColorCell(Cell cellInfo) {
        if (cellInfo == null) {
            Log.d(TAG, "Cell info is null, returning empty state");
            return CellStateManager.STATE_EMPTY;
        }

        // Si la celda está seleccionada, mostrar como seleccionada
        if (selectedCells.contains(cellInfo.getNumCell() - 1)) {
            Log.d(TAG, "Cell " + cellInfo.getNumCell() + " is selected");
            return CellStateManager.STATE_SELECTED_CELL;
        }

        // Si la celda coincide con el modo actual, mostrar en azul (seleccionado)
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
        // Ya no verificamos si la celda está seleccionada
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
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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