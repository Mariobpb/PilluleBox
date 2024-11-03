package com.example.pillulebox.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pillulebox.ContextActivity;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;

import Managers.CellStateManager;
import Models.Dispenser;

public class DispenserSelectedFragment extends Fragment {
    private TextView dispenserName;
    private TextView dispenserContext;
    private AppCompatButton defineContextButton;
    private View[] cell = new View[14];
    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
     */

    public DispenserSelectedFragment() {
        // Required empty public constructor
    }

    public static DispenserSelectedFragment newInstance() {
        DispenserSelectedFragment fragment = new DispenserSelectedFragment();
        /*
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

         */
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
             */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dispenser_selected, container, false);
        initializeViews(view);
        setupListeners();
        updateDispenserInfo();
        updateAllCells();

        return view;
    }

    private void initializeViews(View view) {
        dispenserName = view.findViewById(R.id.tv_dispenser_name_menu);
        dispenserContext = view.findViewById(R.id.tv_dispenser_context_menu);
        int[] cellIds = {
                R.id.cell_1_menu, R.id.cell_2_menu, R.id.cell_3_menu, R.id.cell_4_menu, R.id.cell_5_menu,
                R.id.cell_6_menu, R.id.cell_7_menu, R.id.cell_8_menu, R.id.cell_9_menu, R.id.cell_10_menu,
                R.id.cell_11_menu, R.id.cell_12_menu, R.id.cell_13_menu, R.id.cell_14_menu,
        };
        for (int i = 0; i < cell.length; i++) {
            cell[i] = view.findViewById(cellIds[i]);
        }
        defineContextButton = view.findViewById(R.id.define_context_button);
    }

    public void updateDispenserInfo() {
        Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(requireContext());
        if (selectedDispenser != null) {
            dispenserName.setText(selectedDispenser.getName());
            dispenserContext.setText(selectedDispenser.getContextDispenser() == 0 ? "Unknown" : "#" + String.valueOf(selectedDispenser.getContextDispenser()));
        }
    }

    private void setupListeners() {
        defineContextButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContextActivity.class);
            startActivity(intent);
        });
    }

    private void updateAllCells() {
        for (int i = 0; i < cell.length; i++) {
            CellStateManager.updateCellState(cell[i], 2, 3, getContext());
        }
    }
}