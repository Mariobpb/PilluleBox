package com.example.pillulebox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pillulebox.R;
import java.util.List;

import java.util.List;

public class MacAddressAdapter extends RecyclerView.Adapter<MacAddressAdapter.ViewHolder> {
    private final List<String> macAddresses;

    public MacAddressAdapter(List<String> macAddresses) {
        this.macAddresses = macAddresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mac_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String macAddress = macAddresses.get(position);
        holder.macAddressText.setText(macAddress);

        // Opcional: Puedes agregar un onClick listener aquí si necesitas manejar clics
        holder.itemView.setOnClickListener(v -> {
            // Manejar el clic en la dirección MAC
        });
    }

    @Override
    public int getItemCount() {
        return macAddresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView macAddressText;

        ViewHolder(View itemView) {
            super(itemView);
            macAddressText = itemView.findViewById(R.id.text_mac_address);
        }
    }
}