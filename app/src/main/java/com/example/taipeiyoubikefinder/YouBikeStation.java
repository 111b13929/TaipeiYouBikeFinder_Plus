package com.example.taipeiyoubikefinder;

public class YouBikeStation {
    private final String sno;
    private final String sna;
    private final String sarea;
    private final String ar;
    private final int total;
    private final int available_rent_bikes;
    private final int available_return_bikes;
    private final double lat; // 經度
    private final double lng; // 緯度

    public YouBikeStation(String sno, String sna, String sarea, String ar, int total, int available_rent_bikes, int available_return_bikes, double lat, double lng) {
        this.sno = sno;
        this.sna = sna;
        this.sarea = sarea;
        this.ar = ar;
        this.total = total;
        this.available_rent_bikes = available_rent_bikes;
        this.available_return_bikes = available_return_bikes;
        this.lat = lat;
        this.lng = lng;
    }

    public String getSno() {
        return sno;
    }

    public String getSna() {
        return sna;
    }

    public String getSarea() {
        return sarea;
    }

    public String getAr() {
        return ar;
    }

    public int getTotal() {
        return total;
    }

    public int getAvailableRentBikes() {
        return available_rent_bikes;
    }

    public int getAvailableReturnBikes() {
        return available_return_bikes;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
