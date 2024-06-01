package com.example.taipeiyoubikefinder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YouBikeAdapter extends RecyclerView.Adapter<YouBikeAdapter.ViewHolder> {
    private final Context context;
    private final List<YouBikeStation> stations;

    public YouBikeAdapter(Context context, List<YouBikeStation> stations, OnItemClickListener listener) {
        this.context = context;
        this.stations = stations;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private final OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.youbike_station_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        YouBikeStation station = stations.get(position);
        holder.stationName.setText(station.getSna());
        holder.availableBikes.setText(context.getString(R.string.available_bikes, station.getAvailableRentBikes()));
        holder.location.setText(station.getAr());

        // 處理點擊事件
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", station.getSna());
            intent.putExtra("location", station.getAr());
            intent.putExtra("availableRentBikes", station.getAvailableRentBikes());
            intent.putExtra("availableReturnBikes", station.getAvailableReturnBikes());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stationName;
        TextView availableBikes;
        TextView location;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            stationName = itemView.findViewById(R.id.stationName);
            availableBikes = itemView.findViewById(R.id.availableBikes);
            location = itemView.findViewById(R.id.location);

            itemView.setOnClickListener(v -> listener.onItemClick(getBindingAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(getBindingAdapterPosition());
                return true;
            });
        }
    }
}