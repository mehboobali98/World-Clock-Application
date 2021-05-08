package com.example.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    //private CityTimeZoneDbHelper cityTimeZoneDbHelper;
    private ICityTimeZoneDAO iCityTimeZoneDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.main_activity_title);

        lv = findViewById(R.id.main_activity_List);
        openSecondActivity = findViewById(R.id.select_city_button);
        deleteButton = findViewById(R.id.remove_city_button);
        //cityTimeZoneDbHelper = new CityTimeZoneDbHelper(this);
        iCityTimeZoneDAO = new CityTimeZoneDbDAO(this);

        //To handle screen rotations
        if (savedInstanceState != null) {
            cityTimeZoneArrayList = savedInstanceState.getParcelableArrayList("cityTimeZone");
            timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
            lv.setAdapter(timeZoneAdapter);
        } else {
            cityTimeZoneArrayList = new ArrayList<>();
        }

        /* reading data from database when the app launches */
        showCitiesOnListView();

        //Action Listener to Open Second Activity
        openSecondActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        });

        //Action Listener for Delete button. It only deletes items from the list view and not the database.
        deleteButton.setOnClickListener(v -> {
            ArrayList<CityTimeZone> ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
            if (ctz.size() > 0) {
                for (int i = 0; i < ctz.size(); i++) {
                    if (cityTimeZoneArrayList.contains(ctz.get(i))) {
                        cityTimeZoneArrayList.remove(ctz.get(i));
                        timeZoneAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "0 Cities Selected for Deletion!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Create menu options using inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_item: //load cities stored in the database
                ArrayList<CityTimeZone> dbCityTimeZoneArrayList;
                dbCityTimeZoneArrayList = iCityTimeZoneDAO.getCityTimeZones();
                int size = dbCityTimeZoneArrayList.size();
                if (size > 0) {
                    showCitiesOnListView();
                    Toast.makeText(this, size + " Items Loaded from the database.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Items in the database to load.", Toast.LENGTH_SHORT).show();
                }
                return true;
            //save selected cities into the database
            case R.id.save_item:
                ArrayList<CityTimeZone> ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
                dbCityTimeZoneArrayList = iCityTimeZoneDAO.getCityTimeZones(); //get already existing cities in database
                if (ctz.size() > 0) {
                    for (int i = 0; i < ctz.size(); i++) {
                        if (dbCityTimeZoneArrayList.contains(ctz.get(i)) == false) { //only add if it does not exist in database already
                            boolean success = iCityTimeZoneDAO.addCityTimeZone(ctz.get(i));
                            if (success) {
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Selected City already exists in the Database. Cannot add.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "0 Cities Selected for Storing in Database!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete_item://item delete from the database
                ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
                dbCityTimeZoneArrayList = iCityTimeZoneDAO.getCityTimeZones(); //get already existing cities in database
                if (ctz.size() > 0) {
                    for (int i = 0; i < ctz.size(); i++) {
                        //only add if it does not exist in database already
                        if (dbCityTimeZoneArrayList.contains(ctz.get(i)) == true) {
                            boolean success = iCityTimeZoneDAO.deleteCityTimeZone(ctz.get(i));
                            if (success) {
                                Toast.makeText(this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                                /* reading data from database when a city is deleted*/
                                showCitiesOnListView();
                            } else {
                                Toast.makeText(this, "Could not delete item with name: " + ctz.get(i).getName(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Selected City does not exist in the Database. Cannot delete.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "0 Cities Selected for Deletion in Database!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete_db:
                int rowsDeleted = iCityTimeZoneDAO.deleteDatabase();
                Toast.makeText(MainActivity.this, "Database dropped. Number of tuples deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCitiesOnListView() {
        cityTimeZoneArrayList = Helper.mergeCityTimeZoneArrayLists(cityTimeZoneArrayList, iCityTimeZoneDAO.getCityTimeZones());
        timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
        lv.setAdapter(timeZoneAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cityTimeZone", cityTimeZoneArrayList);
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