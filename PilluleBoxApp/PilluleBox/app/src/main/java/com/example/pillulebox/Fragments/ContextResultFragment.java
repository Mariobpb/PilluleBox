package com.example.pillulebox.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pillulebox.General;
import com.example.pillulebox.MenuActivity;
import com.example.pillulebox.R;
import com.example.pillulebox.adapters.DispenserAdapter;
import com.google.gson.Gson;

import AsyncTasks.UpdateDispenserContextTask;
import Models.Dispenser;


public class ContextResultFragment extends Fragment implements UpdateDispenserContextTask.OnContextUpdateListener{
    private int contextId;
    private TextView statusText;
    private Button returnButton;

    public ContextResultFragment() {
        // Required empty public constructor
    }

    public static ContextResultFragment newInstance(int contextId) {
        ContextResultFragment fragment = new ContextResultFragment();
        Bundle args = new Bundle();
        args.putInt("contextId", contextId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_context_result, container, false);

        contextId = getArguments().getInt("contextId", 0);
        statusText = view.findViewById(R.id.statusText);
        returnButton = view.findViewById(R.id.returnButton);

        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        updateDispenserContext();
        return view;
    }

    private void updateDispenserContext() {
        Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(getContext());
        if (selectedDispenser != null) {
            statusText.setText("Actualizando contexto a: " + contextId);
            String token = General.getToken(getContext());

            new UpdateDispenserContextTask(
                    getContext(),
                    token,
                    selectedDispenser.getMac(),
                    contextId,
                    this
            ).execute();
        } else {
            statusText.setText("Error: No se encontró el dispensador seleccionado");
        }
    }

    public void onContextUpdateSuccess() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                statusText.setText("¡Contexto actualizado correctamente!");
                returnButton.setVisibility(View.VISIBLE);

                // Actualizar el dispensador en SharedPreferences antes de regresar
                Dispenser selectedDispenser = DispenserAdapter.getSelectedDispenser(getContext());
                if (selectedDispenser != null) {
                    // Actualizar el contexto del dispensador
                    selectedDispenser.setContextDispenser(contextId);

                    // Guardar el dispensador actualizado
                    SharedPreferences prefs = requireContext()
                            .getSharedPreferences(General.Archivo, Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    String dispenserJson = gson.toJson(selectedDispenser);
                    prefs.edit()
                            .putString(DispenserAdapter.SELECTED_DISPENSER_KEY, dispenserJson)
                            .apply();
                }
            });
        }
    }

    @Override
    public void onContextUpdateError(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                statusText.setText("Error: " + message);
                returnButton.setVisibility(View.VISIBLE);
            });
        }
    }
}