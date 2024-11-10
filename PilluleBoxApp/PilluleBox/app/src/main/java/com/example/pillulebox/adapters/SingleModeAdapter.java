package com.example.pillulebox.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.DefineScheduleActivity;
import com.example.pillulebox.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import Models.ScheduleModes.SingleMode;


public class SingleModeAdapter extends RecyclerView.Adapter<SingleModeAdapter.ViewHolder> {
    private List<SingleMode> modes;
    private final SimpleDateFormat dateFormat;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SingleMode mode);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(mode);
            }
        });

        holder.editModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DefineScheduleActivity.class);
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
            medicineName = view.findViewById(R.id.medicine_name);
            dispensingDate = view.findViewById(R.id.dispensing_date);
            editModeButton = view.findViewById(R.id.edit_single_mode_button);
        }
    }
}