package com.example.taipeiyoubikefinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private YouBikeAdapter adapter;
    private final List<YouBikeStation> stationList = new ArrayList<>();
    private final List<YouBikeStation> filteredList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchStationData);

        adapter = new YouBikeAdapter(this, filteredList, new YouBikeAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                onItemClicked(position);
            }

            public void onItemLongClick(int position) {
                onItemLongClicked(position);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchStationData();

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.addButton).setOnClickListener(this::onAddButtonClicked);
    }

    private void fetchStationData() {
        swipeRefreshLayout.setRefreshing(true);
        YouBikeService youBikeService = RetrofitClient.getClient().create(YouBikeService.class);
        Call<List<YouBikeStation>> call = youBikeService.getStations();
        call.enqueue(new Callback<List<YouBikeStation>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<YouBikeStation>> call, @NonNull Response<List<YouBikeStation>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    stationList.clear();
                    stationList.addAll(response.body());
                    filterStations("");
                } else {
                    System.err.println("數據加載失敗：" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<YouBikeStation>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                System.err.println("網絡錯誤：" + t.getMessage());
            }
        });
    }

    private void filterStations(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(stationList);
        } else {
            for (YouBikeStation station : stationList) {
                if (station.getSna().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(station);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onAddButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增站點");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_station, null);
        builder.setView(customLayout);

        builder.setPositiveButton("新增", (dialog, which) -> {
            EditText editSno = customLayout.findViewById(R.id.editSno);
            EditText editSna = customLayout.findViewById(R.id.editSna);
            EditText editAr = customLayout.findViewById(R.id.editAr);
            EditText editRentBikes = customLayout.findViewById(R.id.editRentBikes);
            EditText editReturnBikes = customLayout.findViewById(R.id.editReturnBikes);

            String sno = editSno.getText().toString();
            String sna = editSna.getText().toString();
            String ar = editAr.getText().toString();
            int rentBikes = Integer.parseInt(editRentBikes.getText().toString());
            int returnBikes = Integer.parseInt(editReturnBikes.getText().toString());

            YouBikeStation newStation = new YouBikeStation(sno, sna, ar, rentBikes, returnBikes);
            stationList.add(newStation);
            filterStations("");

            Toast.makeText(this, "站點已新增", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onItemClicked(int position) {
        YouBikeStation station = filteredList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("sno", station.getSno());
        intent.putExtra("name", station.getSna());
        intent.putExtra("location", station.getAr());
        intent.putExtra("availableRentBikes", station.getAvailableRentBikes());
        intent.putExtra("availableReturnBikes", station.getAvailableReturnBikes());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onItemLongClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除站點");
        builder.setMessage("確定要刪除此站點嗎？");

        builder.setPositiveButton("刪除", (dialog, which) -> {
            YouBikeStation station = filteredList.get(position);
            stationList.remove(station);
            filterStations("");
            Toast.makeText(this, "站點已刪除", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
