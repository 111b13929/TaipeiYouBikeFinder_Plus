package com.example.taipeiyoubikefinder;

public class YouBikeStation {
    private final String sno;       // 站點ID
    private final String sna;       // 站點名稱
    private final String sarea;     // 區域
    private final String ar;        // 地址
    private final int total;        // 總車輛數
    private final int available_rent_bikes; // 可租車輛數
    private final int available_return_bikes; // 可還車輛數

    public YouBikeStation(String sno, String sna, String sarea, String ar, int total, int available_rent_bikes, int available_return_bikes) {
        this.sno = sno;
        this.sna = sna;
        this.sarea = sarea;
        this.ar = ar;
        this.total = total;
        this.available_rent_bikes = available_rent_bikes;
        this.available_return_bikes = available_return_bikes;
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
}
