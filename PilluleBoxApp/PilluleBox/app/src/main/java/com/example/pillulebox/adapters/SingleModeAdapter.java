package com.example.pillulebox.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.EditScheduleActivity;
import com.example.pillulebox.LogInActivity;
import com.example.pillulebox.R;
import com.example.pillulebox.ScheduleActivity;
import com.example.pillulebox.SignUpActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import Models.ScheduleModes.SingleMode;

public class SingleModeAdapter extends RecyclerView.Adapter<SingleModeAdapter.ViewHolder> {
    private List<SingleMode> modes;
    private final SimpleDateFormat dateFormat;
    public SingleModeAdapter(List<SingleMode> modes) {
        this.modes = modes;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SingleMode mode = modes.get(position);
        holder.medicineName.setText(mode.getMedicineName());
        holder.dispensingDate.setText(dateFormat.format(mode.getDispensingDate()));
        holder.editModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditScheduleActivity.class);
            intent.putExtra("mode", mode);
            intent.putExtra("mode_type", "single");
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView medicineName;
        final TextView dispensingDate;
        final Button editModeButton;

        ViewHolder(View view) {
            super(view);
            medicineName = view.findViewById(R.id.medicineName);
            dispensingDate = view.findViewById(R.id.dispensingDate);
            editModeButton = view.findViewById(R.id.edit_single_mode_button);
        }
    }
}