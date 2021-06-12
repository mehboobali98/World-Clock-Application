package com.example.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;


public class SecondActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SecondActivity";
    private ListView lv;
    private FloatingActionButton saveCitiesButton;
    private EditText editText;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private TimeZoneAdapter timeZoneAdapter;
    private ICityTimeZoneDAO iCityTimeZoneDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setTitle(R.string.second_activity_title);
        iCityTimeZoneDAO = new CityTimeZoneDbDAO(this);
        lv = findViewById(R.id.second_activity_list);
        saveCitiesButton = findViewById(R.id.save_cities_button);
        editText = findViewById(R.id.second_activity_filter);

        //To handle screen rotations
        if (savedInstanceState != null) {
            cityTimeZoneArrayList = savedInstanceState.getParcelableArrayList("cityTimeZone");
        }

        loadDataFromDb();

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cityTimeZone", cityTimeZoneArrayList);
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

    //Create menu options using inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.second_activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_data: //start the download service
                Intent intent = new Intent(SecondActivity.this, TimeZoneDLService.class);
                if (!iCityTimeZoneDAO.isEmpty())
                    deleteDb();
                startService(intent);
                return true;
            case R.id.load_data_from_db:
                if (TimeZoneDLService.isServiceRunning) {
                    showMessage("Timezone download service is still running. Please wait.");
                } else {
                    loadDataFromDb();
                }
                return true;
            case R.id.delete_db:
                deleteDb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDataFromDb() {
        cityTimeZoneArrayList = iCityTimeZoneDAO.getCityTimeZones();
        int size = cityTimeZoneArrayList.size();
        if (size > 0) {
            timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
            lv.setAdapter(timeZoneAdapter);
            showMessage(size + " Items Loaded from the database.");
        } else {
            showMessage("No Items in the database to load.");
        }
    }

    private void deleteDb() {
        int rowsDeleted = iCityTimeZoneDAO.deleteDatabase();
        cityTimeZoneArrayList.clear();
        timeZoneAdapter.notifyDataSetChanged();
        showMessage("Database dropped. Number of tuples deleted: " + rowsDeleted);
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}