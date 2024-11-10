package com.example.pillulebox.adapters;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.DefineScheduleActivity;
import com.example.pillulebox.GeneralInfo;
import com.example.pillulebox.R;

import java.util.List;
import java.util.Locale;

import AsyncTasks.Schedules.DeleteBasicModeTask;
import AsyncTasks.Schedules.DeleteSingleModeTask;
import Models.ScheduleModes.BasicMode;

public class BasicModeAdapter extends RecyclerView.Adapter<BasicModeAdapter.ViewHolder> {
    private List<BasicMode> modes;
    private final SimpleDateFormat dateFormat;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BasicMode mode);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public BasicModeAdapter(List<BasicMode> modes) {
        this.modes = modes;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(mode);
            }
        });

        holder.editModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DefineScheduleActivity.class);
            intent.putExtra("mode", mode);
            intent.putExtra("mode_type", "basic");
            v.getContext().startActivity(intent);
        });

        holder.deleteModeButton.setOnClickListener(v -> {
            DeleteBasicModeTask deleteTask = new DeleteBasicModeTask(
                    v.getContext(),
                    GeneralInfo.getToken(v.getContext()),
                    GeneralInfo.getSelectedDispenser(v.getContext()).getMac(),
                    mode,
                    new DeleteBasicModeTask.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            modes.remove(position);
                            notifyItemRemoved(position);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(v.getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            deleteTask.execute();
        });
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView medicineName;
        final Button editModeButton;
        final ImageButton deleteModeButton;
        ViewHolder(View view) {
            super(view);
            medicineName = view.findViewById(R.id.medicine_name);
            editModeButton = view.findViewById(R.id.edit_basic_mode_button);
            deleteModeButton = view.findViewById(R.id.delete_basic_mode_button);
        }
    }
}