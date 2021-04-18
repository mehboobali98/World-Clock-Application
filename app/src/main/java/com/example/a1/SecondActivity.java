package com.example.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SecondActivity";
    private ListView lv;
    private Button saveCitiesButton;
    private EditText editText;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private TimeZoneAdapter timeZoneAdapter;
    private ArrayList<String> timeZones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setTitle(R.string.second_activity_title);
        lv = findViewById(R.id.second_activity_list);
        saveCitiesButton = findViewById(R.id.save_cities_button);
        editText = findViewById(R.id.second_activity_filter);

        //timeZones = readFile("timezones.txt");
        timeZones = Helper.getTimeZones();
        displayCityTimeZoneList(timeZones);

        //Returning to Main Activity after pressing save button and using finish()
        saveCitiesButton.setOnClickListener(v -> {
            prepareResult();
            finish();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeZoneAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void prepareResult() {
        Intent resultIntent = new Intent();
        ArrayList<CityTimeZone> checkedCities;
        checkedCities = Helper.getCheckedCities(cityTimeZoneArrayList);
        resultIntent.putExtra("result", checkedCities);
        resultIntent.putExtra("Size", checkedCities.size());
        setResult(RESULT_OK, resultIntent);
    }

    //Returning to Main Activity if Back/Return is Pressed
    @Override
    public void onBackPressed() {
        prepareResult();
        super.onBackPressed();
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
}