package com.example.taipeiyoubikefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private YouBikeAdapter adapter;
    private final List<YouBikeStation> stationList = new ArrayList<>();
    private final Stack<YouBikeStation> deletedStations = new Stack<>();
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

        getLastKnownLocation();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_show_all) {
                onShowAllButtonClicked(null);
            } else if (id == R.id.nav_show_nearby) {
                onShowNearbyButtonClicked(null);
            } else if (id == R.id.nav_add_station) {
                onAddButtonClicked();
            } else if (id == R.id.nav_undo) {
                // Handle undo action here
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        EditText searchBar = findViewById(R.id.searchBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在這裡不需要做任何事情
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 當文字改變時，過濾站點列表
                filterStations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在這裡不需要做任何事情
            }
        });
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
    private void onAddButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增站點");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_station, null);
        builder.setView(customLayout);

        builder.setPositiveButton("新增", (dialog, which) -> {
            EditText editSno = customLayout.findViewById(R.id.editSno);
            EditText editSna = customLayout.findViewById(R.id.editSna);
            EditText editAr = customLayout.findViewById(R.id.editAr);

            String sno = editSno.getText().toString();
            String sna = editSna.getText().toString();
            String ar = editAr.getText().toString();

            // 為了簡化，我們將其他欄位設置為預設值
            int total = 0;
            int rentBikes = 0;
            int returnBikes = 0;
            double lat = 0.0;
            double lng = 0.0;

            YouBikeStation newStation = new YouBikeStation(sno, sna, "", ar, total, rentBikes, returnBikes, lat, lng);
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
            YouBikeStation deletedStation = stationList.remove(position);
            deletedStations.push(deletedStation);
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
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                fetchNearbyStations(location.getLatitude(), location.getLongitude());
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

    @SuppressLint("NotifyDataSetChanged")
    private void onShowAllButtonClicked(View view) {
        adapter.updateData(stationList);
    }

    private void onShowNearbyButtonClicked(View view) {
        getLastKnownLocation();
    }

    private void filterStations(String query) {
        List<YouBikeStation> filteredStations = new ArrayList<>();
        for (YouBikeStation station : stationList) {
            if (station.getSna().toLowerCase().contains(query.toLowerCase())) {
                filteredStations.add(station);
            }
        }
        adapter.updateData(filteredStations);
    }
}