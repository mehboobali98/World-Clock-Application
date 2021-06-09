package com.example.a1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeZoneDLService extends Service {
    private ICityTimeZoneDAO iCityTimeZoneDAO;
    static boolean isServiceRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        iCityTimeZoneDAO = new CityTimeZoneDbDAO(this);
        isServiceRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyServiceTag", "Download Service Closed.");
        isServiceRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyServiceTag", "Download Service Started.");
        downloadData();
        stopSelf();
        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //download data from api
    private void downloadData()
    {
        Thread thread = new Thread(() -> load());
        thread.start();
    }

    private void load()
    {
        String line;
        try{
            URL url = new URL("https://api.timezonedb.com/v2.1/list-time-zone?key=6099QLC511G1&format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

            while( (line = reader.readLine()) != null ){
                content.append(line);
            }

            line = content.toString();
            parse(line);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parse(String line)
    {
        try {
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = (JSONArray) jsonObject.get("zones");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject timeZone = jsonArray.getJSONObject(i);
                String zoneName = timeZone.getString("zoneName");
                iCityTimeZoneDAO.addCityTimeZone(new CityTimeZone(zoneName));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}
