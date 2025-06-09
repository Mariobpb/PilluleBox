package com.example.pillulebox.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pillulebox.R;

public class NoDispenserSelectedFragment extends Fragment {

    public NoDispenserSelectedFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_dispenser_selected, container, false);
    }
}
