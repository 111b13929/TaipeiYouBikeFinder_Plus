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
    private final OnItemClickListener listener;

    public YouBikeAdapter(Context context, List<YouBikeStation> stations, OnItemClickListener listener) {
        this.context = context;
        this.stations = stations;
        this.listener = listener;
    }

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

        // 設置點擊事件處理
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("sno", station.getSno());
            intent.putExtra("name", station.getSna());
            intent.putExtra("location", station.getAr());
            intent.putExtra("availableRentBikes", station.getAvailableRentBikes());
            intent.putExtra("availableReturnBikes", station.getAvailableReturnBikes());
            context.startActivity(intent);
        });

        // 設置長按事件處理（用於刪除）
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public void addStation(YouBikeStation station) {
        stations.add(station);
        notifyItemInserted(stations.size() - 1);
    }

    public void removeStation(int position) {
        stations.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stationName;
        TextView availableBikes;
        TextView location;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            stationName = itemView.findViewById(R.id.stationName);
            availableBikes = itemView.findViewById(R.id.availableBikes);
            location = itemView.findViewById(R.id.location);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
}
