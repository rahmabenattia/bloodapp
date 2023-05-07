package com.example.bloodapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RendezVousAdapter extends RecyclerView.Adapter<RendezVousAdapter.ViewHolder> {
    private List<Rendezvoushis> rendezVousList;

    public RendezVousAdapter(List<Rendezvoushis> rendezVousList) {
        this.rendezVousList = rendezVousList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_rendezvous_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rendezvoushis rendezVous = rendezVousList.get(position);
        holder.dateTextView.setText(rendezVous.getDate());
        holder.datetimeTextView.setText(rendezVous.getTime());
    }

    @Override
    public int getItemCount() {
        return rendezVousList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView datetimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            datetimeTextView = itemView.findViewById(R.id.datetime_text_view);
        }
    }
}
