package com.example.taipeiyoubikefinder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 接收傳遞的數據
        String name = getIntent().getStringExtra("name");
        String location = getIntent().getStringExtra("location");
        int availableRentBikes = getIntent().getIntExtra("availableRentBikes", 0);
        int availableReturnBikes = getIntent().getIntExtra("availableReturnBikes", 0);

        // 綁定視圖並設置數據
        TextView nameTextView = findViewById(R.id.stationNameDetail);
        TextView locationTextView = findViewById(R.id.stationLocationDetail);
        TextView availableRentBikesTextView = findViewById(R.id.availableRentBikesDetail);
        TextView availableReturnBikesTextView = findViewById(R.id.availableReturnBikesDetail);

        nameTextView.setText(name);
        locationTextView.setText(location);
        availableRentBikesTextView.setText("可租車輛: " + availableRentBikes);
        availableReturnBikesTextView.setText("可還車輛: " + availableReturnBikes);
    }
}
