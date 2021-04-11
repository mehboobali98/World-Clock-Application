package com.example.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SecondActivity";
    ListView lv;
    SearchView searchView;
    ArrayList<CityTimeZone> cityTimeZoneArrayList;
    TimeZoneAdapter timeZoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        lv = findViewById(R.id.second_activity_list);
        Button saveCities = findViewById(R.id.save_cities_button);
        searchView = findViewById(R.id.second_activity_filter);

        ArrayList<String> timeZones;
        timeZones = readFile("timezones.txt");
        displayCityTimeZoneList(timeZones);
        lv.setTextFilterEnabled(true);

        saveCities.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            ArrayList<CityTimeZone> checkedCities;
            checkedCities = Helper.getCheckedCities(cityTimeZoneArrayList);
            resultIntent.putExtra("result", checkedCities);
            resultIntent.putExtra("Size", checkedCities.size());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SecondActivity.this.timeZoneAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SecondActivity.this.timeZoneAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void displayCityTimeZoneList(ArrayList<String> timeZones) {
        cityTimeZoneArrayList = new ArrayList<>();
        for (String s : timeZones) {
            String[] splitStr = s.split("\\s+");
            cityTimeZoneArrayList.add(new CityTimeZone(splitStr[0], splitStr[1]));
        }
        timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
        lv.setAdapter(timeZoneAdapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            CityTimeZone ctz = cityTimeZoneArrayList.get(pos);
            ctz.setSelected(isChecked);
            ArrayList<CityTimeZone> checkedCities;
            checkedCities = Helper.getCheckedCities(cityTimeZoneArrayList);
            int size = checkedCities.size();
            Toast.makeText(this, "Number of cities selected: " + size, Toast.LENGTH_SHORT).show();
        }
    }

    //Read timezones from the file
    private ArrayList<String> readFile(String fileName) {
        InputStream is = null;
        BufferedReader reader = null;
        ArrayList<String> timeZones = new ArrayList<>();
        try {
            AssetManager am = getAssets();
            is = am.open(fileName);
            reader = new BufferedReader(new InputStreamReader(is));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                timeZones.add(currentLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return timeZones;
    }
}