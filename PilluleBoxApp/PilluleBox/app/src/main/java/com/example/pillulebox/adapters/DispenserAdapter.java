package com.example.pillulebox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pillulebox.R;
import java.util.List;

import Classes.Dispenser;

public class DispenserAdapter extends RecyclerView.Adapter<DispenserAdapter.ViewHolder> {
    private final List<Dispenser> dispensers;

    public DispenserAdapter(List<Dispenser> dispensers) {
        this.dispensers = dispensers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dispensers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dispenser dispenser = dispensers.get(position);
        holder.macAddressText.setText(dispenser.getMac());
        holder.nameText.setText(dispenser.getName());
        holder.contextText.setText(dispenser.getContextDispenser() == -1 ? "Unknown" : String.valueOf(dispenser.getContextDispenser()));
    }

    @Override
    public int getItemCount() {
        return dispensers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView macAddressText;
        TextView nameText;
        TextView contextText;

        ViewHolder(View itemView) {
            super(itemView);
            macAddressText = itemView.findViewById(R.id.dispenser_item_mac);
            nameText = itemView.findViewById(R.id.dispenser_item_name);
            contextText = itemView.findViewById(R.id.dispenser_item_context);
        }
    }
}