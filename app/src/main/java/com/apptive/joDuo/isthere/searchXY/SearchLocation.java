package com.apptive.joDuo.isthere.searchXY;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
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

public class SearchLocation {
    SearchTask searchTask;
    OnFinishSearchLocationListener onFinishSearchLocationListener;
    String appId;

    private static final String HEADER_NAME_X_APPID = "x-appid";
    private static final String HEADER_NAME_X_PLATFORM = "x-platform";
    private static final String HEADER_VALUE_X_PLATFORM_ANDROID = "android";

    public static final String DAUM_LOCATION_SEARCH_API_FORMAT = "https://apis.daum.net/local/geo/addr2coord?apikey=%s&q=%s&output=json";

    private class SearchTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            Map<String, String> header = new HashMap<String, String>();
            header.put(HEADER_NAME_X_APPID, appId);
            header.put(HEADER_NAME_X_PLATFORM, HEADER_VALUE_X_PLATFORM_ANDROID);
            String json = fetchData(url, header);
            Location itemList = parse(json);
            if (onFinishSearchLocationListener != null) {
                if (itemList == null) {
                    onFinishSearchLocationListener.onFail();
                } else {
                    onFinishSearchLocationListener.onSuccess(itemList);
                }
            }

            return null;
        }
    }

    // query문 실행
    public void searchDetailAddress(Context applicationContext, String location, String apikey, OnFinishSearchLocationListener onFinishSearchLocationListener) {
        this.onFinishSearchLocationListener = onFinishSearchLocationListener;

        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }

        if (applicationContext != null) {
            appId = applicationContext.getPackageName();
        }
        String url = buildAddressSearchApiUrlString(apikey, location);

        searchTask = new SearchTask();
        searchTask.execute(url);
    }

    // query문 생성
    private String buildAddressSearchApiUrlString(String apikey, String location) {
        return String.format(DAUM_LOCATION_SEARCH_API_FORMAT, apikey, location);
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
    private Location parse(String jsonString) {
        Location location = new Location();

        try {
            JSONObject head = new JSONObject(jsonString);
            JSONObject channel = head.getJSONObject("channel");
            JSONArray items = channel.getJSONArray("item");

            int result = Integer.parseInt(channel.getString("result"));
            if(result != 0){
                location.lattitude = items.getJSONObject(0).getDouble("lat");
                location.logitutde = items.getJSONObject(0).getDouble("lng");
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return location;
    }

    public void cancel() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }

}
