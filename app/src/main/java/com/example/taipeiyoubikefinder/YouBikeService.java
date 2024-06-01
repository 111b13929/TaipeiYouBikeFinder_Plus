package com.example.taipeiyoubikefinder;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface YouBikeService {
    @GET("youbike/v2/youbike_immediate.json")
    Call<List<YouBikeStation>> getStations();
}
