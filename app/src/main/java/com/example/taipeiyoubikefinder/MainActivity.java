package com.example.taipeiyoubikefinder;

import android.content.Intent;
import android.os.Bundle;
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

        adapter = new YouBikeAdapter(this, stationList, this::onItemClick);
        recyclerView.setAdapter(adapter);

        fetchStationData();
    }

    private void fetchStationData() {
        swipeRefreshLayout.setRefreshing(true);
        YouBikeService youBikeService = RetrofitClient.getClient().create(YouBikeService.class);
        Call<List<YouBikeStation>> call = youBikeService.getStations();
        call.enqueue(new Callback<List<YouBikeStation>>() {
            @Override
            public void onResponse(Call<List<YouBikeStation>> call, Response<List<YouBikeStation>> response) {
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
            public void onFailure(Call<List<YouBikeStation>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                System.err.println("網絡錯誤：" + t.getMessage());
            }
        });
    }

    private void onItemClick(int position) {
        YouBikeStation station = stationList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("name", station.getSna());
        intent.putExtra("location", station.getAr());
        intent.putExtra("availableRentBikes", station.getAvailableRentBikes());
        intent.putExtra("availableReturnBikes", station.getAvailableReturnBikes());
        startActivity(intent);
    }
}
