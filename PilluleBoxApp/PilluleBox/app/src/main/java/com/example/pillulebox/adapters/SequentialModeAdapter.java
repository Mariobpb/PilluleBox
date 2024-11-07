package com.example.pillulebox.adapters;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.R;

import java.util.List;
import java.util.Locale;

import Models.ScheduleModes.SequentialMode;

public class SequentialModeAdapter extends RecyclerView.Adapter<SequentialModeAdapter.ViewHolder> {
    private List<SequentialMode> modes;
    private final SimpleDateFormat dateFormat;

    public SequentialModeAdapter(List<SequentialMode> modes) {
        this.modes = modes;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sequential_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SequentialMode mode = modes.get(position);
        holder.medicineName.setText(mode.getMedicineName());
        holder.startDate.setText(dateFormat.format(mode.getStartDate()));
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView medicineName;
        final TextView startDate;

        ViewHolder(View view) {
            super(view);
            medicineName = view.findViewById(R.id.medicineName);
            startDate = view.findViewById(R.id.startDate);
        }
    }
}