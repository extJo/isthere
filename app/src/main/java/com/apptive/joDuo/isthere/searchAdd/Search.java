package com.apptive.joDuo.isthere.searchAdd;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by joseong-yun on 2017. 6. 8..
 */

public class Search {
    SearchTask searchTask;
    OnFinishSearchListener onFinishSearchListener;
    String appId;

    private static final String HEADER_NAME_X_APPID = "x-appid";
    private static final String HEADER_NAME_X_PLATFORM = "x-platform";
    private static final String HEADER_VALUE_X_PLATFORM_ANDROID = "android";

    public static final String DAUM_LOCATION_DETAIL_ADDRESS_SEARCH_API_FORMAT = "https://apis.daum.net/local/geo/coord2detailaddr?apikey=%s&x=%f&y=%f&inputCoordSystem=WGS84&output=json";

    private class SearchTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            Map<String, String> header = new HashMap<String, String>();
            header.put(HEADER_NAME_X_APPID, appId);
            header.put(HEADER_NAME_X_PLATFORM, HEADER_VALUE_X_PLATFORM_ANDROID);
            String json = fetchData(url, header);
            AddressItem itemList = parse(json);
            if (onFinishSearchListener != null) {
                if (itemList == null) {
                    onFinishSearchListener.onFail();
                } else {
                    onFinishSearchListener.onSuccess(itemList);
                }
            }

            return null;
        }
    }

    // query문 실행
    public void searchDetailAddress(Context applicationContext, double latitude, double longitude, String apikey, OnFinishSearchListener onFinishSearchListener) {
        this.onFinishSearchListener = onFinishSearchListener;

        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        if (applicationContext != null) {
            appId = applicationContext.getPackageName();
        }
        String url = buildAddressSearchApiUrlString(apikey, latitude, longitude);

        searchTask = new SearchTask();
        searchTask.execute(url);
    }

    // query문 생성
    private String buildAddressSearchApiUrlString(String apikey, Double latitude, Double longitude) {
        return String.format(DAUM_LOCATION_DETAIL_ADDRESS_SEARCH_API_FORMAT, apikey, longitude, latitude);
    }

    private String fetchData(final String urlString, final Map<String, String> header) {

        String data = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000 /* milliseconds */);
            conn.setConnectTimeout(7000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (header != null) {
                for (String key : header.keySet()) {
                    conn.addRequestProperty(key, header.get(key));
                }
            }
            conn.connect();
            InputStream is = conn.getInputStream();
            @SuppressWarnings("resource")
            Scanner s = new Scanner(is);
            s.useDelimiter("\\A");
            data = s.hasNext() ? s.next() : "";
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // query가 실행이 된 이후에 받아온 JSON을 이용해서 원하는 값마다 쪼갬
    private AddressItem parse(String jsonString) {
        AddressItem addressItem = new AddressItem();

        try {
            JSONObject head = new JSONObject(jsonString);
            JSONObject newAddress = head.getJSONObject("new");
            JSONObject oldAddress = head.getJSONObject("old");

            addressItem.old_name = oldAddress.getString("name");
            addressItem.old_bunji = oldAddress.getString("bunji");
            addressItem.old_ho = oldAddress.getString("ho");
            addressItem.old_san = oldAddress.getString("san");
            addressItem.new_name = newAddress.getString("name");
            addressItem.new_roadname = newAddress.getString("roadName");
            addressItem.new_bunji = newAddress.getString("bunji");
            addressItem.new_ho = newAddress.getString("ho");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return addressItem;
    }

    public void cancel() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }

}
