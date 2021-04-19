package com.example.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private ListView lv;
    private Button deleteButton;
    private Button openSecondActivity;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private TimeZoneAdapter timeZoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.main_activity_title);

        cityTimeZoneArrayList = new ArrayList<>();
        lv = findViewById(R.id.main_activity_List);
        openSecondActivity = findViewById(R.id.select_city_button);
        deleteButton = findViewById(R.id.remove_city_button);

        openSecondActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        });

        deleteButton.setOnClickListener(v -> {
            ArrayList<CityTimeZone> ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
            if (ctz.size() > 0) {
                for (int i = 0; i < ctz.size(); i++) {
                    if (cityTimeZoneArrayList.contains(ctz.get(i))) {
                        cityTimeZoneArrayList.remove(ctz.get(i));
                        timeZoneAdapter.remove(ctz.get(i));
                        timeZoneAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "0 Cities Selected for Deletion!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int size = data.getIntExtra("Size", 0);
                if (size == 0) {
                    Toast.makeText(MainActivity.this, "0 Cities Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    if (cityTimeZoneArrayList.size() == 0) {
                        cityTimeZoneArrayList = data.getParcelableArrayListExtra("result");
                        timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
                        lv.setAdapter(timeZoneAdapter);
                    } else {
                        ArrayList<CityTimeZone> temp = data.getParcelableArrayListExtra("result");
                        for (int i = 0; i < temp.size(); i++) {
                            if (cityTimeZoneArrayList.contains(temp.get(i))) {
                                Toast.makeText(MainActivity.this, "The City has already been selected.", Toast.LENGTH_SHORT).show();
                            } else {
                                cityTimeZoneArrayList.add(temp.get(i));
                                timeZoneAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            CityTimeZone ctz = cityTimeZoneArrayList.get(pos);
            ctz.setSelected(isChecked);
            ArrayList<CityTimeZone> checkedCities = Helper.getCheckedCities(cityTimeZoneArrayList);
            int size = checkedCities.size();
            Toast.makeText(this, "Number of cities selected: " + size, Toast.LENGTH_SHORT).show();
        }
    }
}