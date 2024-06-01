package com.example.taipeiyoubikefinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchStationData);

        adapter = new YouBikeAdapter(this, stationList, new YouBikeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onItemClicked(position);
            }

            @Override
            public void onItemLongClick(int position) {
                onItemLongClicked(position);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchStationData();

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
                    adapter.notifyDataSetChanged();
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

    private void onAddButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增站點");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_station, null);
        builder.setView(customLayout);

        builder.setPositiveButton("新增", (dialog, which) -> {
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editSno = customLayout.findViewById(R.id.editSno);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editSna = customLayout.findViewById(R.id.editSna);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editAr = customLayout.findViewById(R.id.editAr);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editRentBikes = customLayout.findViewById(R.id.editRentBikes);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editReturnBikes = customLayout.findViewById(R.id.editReturnBikes);

            String sno = editSno.getText().toString();
            String sna = editSna.getText().toString();
            String ar = editAr.getText().toString();
            int rentBikes = Integer.parseInt(editRentBikes.getText().toString());
            int returnBikes = Integer.parseInt(editReturnBikes.getText().toString());

            YouBikeStation newStation = new YouBikeStation(sno, sna, returnBikes);
            adapter.addStation(newStation);

            Toast.makeText(this, "站點已新增", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onItemClicked(int position) {
        YouBikeStation station = stationList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("sno", station.getSno());
        intent.putExtra("name", station.getSna());
        intent.putExtra("location", station.getAr());
        intent.putExtra("availableRentBikes", station.getAvailableRentBikes());
        intent.putExtra("availableReturnBikes", station.getAvailableReturnBikes());
        startActivity(intent);
    }

    private void onItemLongClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除站點");
        builder.setMessage("確定要刪除此站點嗎？");

        builder.setPositiveButton("刪除", (dialog, which) -> {
            adapter.removeStation(position);
            Toast.makeText(this, "站點已刪除", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
