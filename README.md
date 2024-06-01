# Taipei YouBike Finder

Taipei YouBike Finder 是一個 Android 應用，用於查詢台北市 YouBike 站點的即時資料。此應用使用 RecyclerView 顯示各站點的資訊，並在點選項目後顯示詳細資料。

## 功能

- 顯示台北市所有 YouBike 站點的名稱、可租車輛數量和位置。
- 下拉更新功能以獲取最新的站點資料。
- 點選站點列表中的項目可查看詳細資訊，包括站點名稱、位置、可租車輛數量和可還車輛數量。

## 資料來源

應用的數據來自於台北市政府的 YouBike 2.0 即時資料接口：

[YouBike 2.0 即時資料](https://tcgbusfs.blob.core.windows.net/dotapp/youbike/v2/youbike_immediate.json)

## 如何執行

1. 複製此專案到本地：

    ```sh
    git clone https://github.com/111b13929/TaipeiYouBikeFinder_copy.git
    ```

2. 開啟 Android Studio，並選擇「Open an existing Android Studio project」，導入此專案。

3. 確保您已經連接到網路，並配置好 Android 模擬器或連接了 Android 設備。

4. 執行應用。

## 執行結果畫面

主界面顯示所有 YouBike 站點的列表：

![螢幕擷取畫面 2024-06-01 030258](https://hackmd.io/_uploads/SkilCqv4A.png)


點選某個站點顯示詳細資訊：

![螢幕擷取畫面 2024-06-01 030321](https://hackmd.io/_uploads/By8W09PNA.png)


## 文件結構

- `MainActivity.java`: 主活動，顯示 YouBike 站點列表並處理下拉更新和點選事件。
- `DetailActivity.java`: 詳細資訊活動，顯示選中站點的詳細資訊。
- `YouBikeAdapter.java`: RecyclerView 的適配器，用於綁定站點資料。
- `YouBikeStation.java`: 資料模型，包含站點的基本資訊。
- `RetrofitClient.java`: 用於配置 Retrofit 客戶端。
- `YouBikeService.java`: 定義 API 接口。

## 依賴

```groovy
dependencies {
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
}
