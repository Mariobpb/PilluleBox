package com.example.pillulebox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<History> historyList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(History history);
    }

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void updateHistory(List<History> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView medicineNameTextView;
        private TextView statusTextView;
        private TextView dateTextView;
        private TextView reasonTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineNameTextView = itemView.findViewById(R.id.history_item_name);
            statusTextView = itemView.findViewById(R.id.history_item_status);
            dateTextView = itemView.findViewById(R.id.history_item_date);
            reasonTextView = itemView.findViewById(R.id.history_item_reason);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(historyList.get(position));
                    }
                }
            });
        }

        public void bind(History history) {
            medicineNameTextView.setText(history.getMedicineName());
            statusTextView.setText(formatStatus(history.getConsumptionStatus()));
            dateTextView.setText(formatDate(history.getDateConsumption()));

            if (history.getReason() != null && !history.getReason().trim().isEmpty()) {
                reasonTextView.setText(history.getReason());
                reasonTextView.setVisibility(View.VISIBLE);
            } else {
                reasonTextView.setVisibility(View.GONE);
            }

            setStatusColor(history.getConsumptionStatus());
        }

        private String formatStatus(String status) {
            switch (status) {
                case "1":
                    return "Consumido";
                case "0":
                    return "No consumido";
                default:
                    return "Desconocido";
            }
        }

        private String formatDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "Fecha no disponible";
            }

            try {
                if (dateString.contains("T") && dateString.endsWith("Z")) {
                    java.time.Instant instant = java.time.Instant.parse(dateString);
                    java.util.Date date = java.util.Date.from(instant);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return outputFormat.format(date);
                }

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateString;
            }
        }

        private void setStatusColor(String status) {
            int colorRes;

            if (status == null) {
                colorRes = android.R.color.darker_gray;
            } else {
                switch (status.toLowerCase()) {
                    case "taken":
                    case "1":
                        colorRes = android.R.color.holo_green_dark;
                        break;
                    case "missed":
                    case "0":
                        colorRes = android.R.color.holo_red_dark;
                        break;
                    case "pending":
                        colorRes = android.R.color.holo_orange_dark;
                        break;
                    default:
                        colorRes = android.R.color.darker_gray;
                        break;
                }
            }

            statusTextView.setTextColor(itemView.getContext().getResources().getColor(colorRes));
        }
    }
}