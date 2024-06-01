package com.example.taipeiyoubikefinder;

public class YouBikeStation {
    private final String sno;       // 站點ID
    private final String sna;       // 站點名稱
    // 地址
    private final int available_rent_bikes; // 可租車輛數

    public YouBikeStation(String s, String sno, String sna, int rentBikes, int available_rent_bikes) {
        this.sno = sno;
        this.sna = sna;
        // 區域
        // 總車輛數
        this.available_rent_bikes = available_rent_bikes;
    }

    public String getSno() {
        return sno;
    }

    public String getSna() {
        return sna;
    }

    public String getAr() {
        return null;
    }

    public int getAvailableRentBikes() {
        return available_rent_bikes;
    }

    public int getAvailableReturnBikes() {
        // 可還車輛數
        return 0;
    }
}
