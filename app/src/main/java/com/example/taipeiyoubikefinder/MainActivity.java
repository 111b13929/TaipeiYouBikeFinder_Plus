package com.example.taipeiyoubikefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private YouBikeAdapter adapter;
    private final List<YouBikeStation> stationList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchStationData);

        adapter = new YouBikeAdapter(this, stationList, new YouBikeAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                onItemClicked(position);
            }

            public void onItemLongClick(int position) {
                onItemLongClicked(position);
            }
        });
        recyclerView.setAdapter(adapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchStationData();

        findViewById(R.id.addButton).setOnClickListener(this::onAddButtonClicked);

        getLastKnownLocation();
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
                    adapter.updateData(stationList);
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

    @SuppressLint("NotifyDataSetChanged")
    private void onAddButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增站點");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_station, null);
        builder.setView(customLayout);

        builder.setPositiveButton("新增", (dialog, which) -> {
            EditText editSno = customLayout.findViewById(R.id.editSno);
            EditText editSna = customLayout.findViewById(R.id.editSna);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editSarea = customLayout.findViewById(R.id.editSarea);
            EditText editAr = customLayout.findViewById(R.id.editAr);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTotal = customLayout.findViewById(R.id.editTotal);
            EditText editRentBikes = customLayout.findViewById(R.id.editRentBikes);
            EditText editReturnBikes = customLayout.findViewById(R.id.editReturnBikes);
            EditText editLat = customLayout.findViewById(R.id.editLat);
            EditText editLng = customLayout.findViewById(R.id.editLng);

            String sno = editSno.getText().toString();
            String sna = editSna.getText().toString();
            String sarea = editSarea.getText().toString();
            String ar = editAr.getText().toString();
            int total = Integer.parseInt(editTotal.getText().toString());
            int rentBikes = Integer.parseInt(editRentBikes.getText().toString());
            int returnBikes = Integer.parseInt(editReturnBikes.getText().toString());
            double lat = Double.parseDouble(editLat.getText().toString());
            double lng = Double.parseDouble(editLng.getText().toString());

            YouBikeStation newStation = new YouBikeStation(sno, sna, sarea, ar, total, rentBikes, returnBikes, lat, lng);
            stationList.add(newStation);
            adapter.updateData(stationList);

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

    @SuppressLint("NotifyDataSetChanged")
    private void onItemLongClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("刪除站點");
        builder.setMessage("確定要刪除此站點嗎？");

        builder.setPositiveButton("刪除", (dialog, which) -> {
            stationList.remove(position);
            adapter.updateData(stationList);
            Toast.makeText(this, "站點已刪除", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    fetchNearbyStations(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void fetchNearbyStations(double latitude, double longitude) {
        List<YouBikeStation> nearbyStations = new ArrayList<>();
        for (YouBikeStation station : stationList) {
            double stationLat = station.getLat();
            double stationLng = station.getLng();
            float[] results = new float[1];
            Location.distanceBetween(latitude, longitude, stationLat, stationLng, results);
            float distance = results[0];
            if (distance <= 1000) { // 以1公里為範圍
                nearbyStations.add(station);
            }
        }
        adapter.updateData(nearbyStations);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            }
        }
    }
}
