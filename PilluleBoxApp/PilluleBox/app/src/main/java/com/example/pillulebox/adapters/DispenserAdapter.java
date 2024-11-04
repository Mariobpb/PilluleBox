package com.example.pillulebox.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillulebox.General;
import com.example.pillulebox.MenuActivity;
import com.example.pillulebox.R;
import com.google.gson.Gson;

import java.util.List;

import Models.Dispenser;

public class DispenserAdapter extends RecyclerView.Adapter<DispenserAdapter.ViewHolder> {
    private final List<Dispenser> dispensers;
    private final Context context;
    public static final String SELECTED_DISPENSER_KEY = "SelectedDispenser";

    public DispenserAdapter(Context context, List<Dispenser> dispensers) {
        this.context = context;
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
        holder.contextText.setText(dispenser.getContextDispenser() == 0 ? "Unknown" : String.valueOf(dispenser.getContextDispenser()));

        holder.itemView.setOnClickListener(v -> {
            saveSelectedDispenser(dispenser);
            notifyDataSetChanged();
        });

        if (isDispenserSelected(dispenser)) {
            holder.itemView.setBackgroundResource(R.drawable.corners1);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.corners4);
        }
    }

    @Override
    public int getItemCount() {
        return dispensers.size();
    }

    private void saveSelectedDispenser(Dispenser dispenser) {
        SharedPreferences prefs = context.getSharedPreferences(General.Archivo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String dispenserJson = gson.toJson(dispenser);
        editor.putString(SELECTED_DISPENSER_KEY, dispenserJson);
        editor.apply();
        if (context instanceof MenuActivity) {
            ((MenuActivity) context).onDispenserSelected(dispenser);
        }
    }

    private boolean isDispenserSelected(Dispenser dispenser) {
        SharedPreferences prefs = context.getSharedPreferences(General.Archivo, Context.MODE_PRIVATE);
        String savedDispenserJson = prefs.getString(SELECTED_DISPENSER_KEY, "");
        if (!savedDispenserJson.isEmpty()) {
            Gson gson = new Gson();
            Dispenser savedDispenser = gson.fromJson(savedDispenserJson, Dispenser.class);
            return savedDispenser.getMac().equals(dispenser.getMac());
        }
        return false;
    }

    public static void clearSelectedDispenser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(General.Archivo, Context.MODE_PRIVATE);
        prefs.edit().remove(SELECTED_DISPENSER_KEY).apply();

        if (context instanceof MenuActivity) {
            ((MenuActivity) context).updateSelectedDispenserName(null);
        }
    }

    public static Dispenser getSelectedDispenser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(General.Archivo, Context.MODE_PRIVATE);
        String savedDispenserJson = prefs.getString(SELECTED_DISPENSER_KEY, "");
        if (!savedDispenserJson.isEmpty()) {
            Gson gson = new Gson();
            return gson.fromJson(savedDispenserJson, Dispenser.class);
        }
        return null;
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