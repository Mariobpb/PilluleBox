package com.example.pillulebox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.R;

import java.util.List;

import Models.ScheduleModes.BasicMode;

public class BasicModeAdapter extends RecyclerView.Adapter<BasicModeAdapter.ViewHolder> {
    private List<BasicMode> modes;

    public BasicModeAdapter(List<BasicMode> modes) {
        this.modes = modes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_basic_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BasicMode mode = modes.get(position);
        holder.medicineName.setText(mode.getMedicineName());
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView medicineName;

        ViewHolder(View view) {
            super(view);
            medicineName = view.findViewById(R.id.medicineName);
        }
    }
}