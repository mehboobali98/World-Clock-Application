package com.example.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private ListView lv;
    private FloatingActionButton deleteButton;
    private FloatingActionButton openSecondActivity;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private SelectedCityTimeZoneAdapter selectedCityTimeZoneAdapter;
    private ICityTimeZoneDAO iCityTimeZoneDAO;
    private Handler handler;
    private Thread thread;
    private int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.main_activity_title);

        lv = findViewById(R.id.main_activity_List);
        openSecondActivity = findViewById(R.id.select_city_button);
        deleteButton = findViewById(R.id.remove_city_button);
        iCityTimeZoneDAO = new CityTimeZoneDbDAO(this);
        handler = new Handler();
        delay = 1000; //1000 ms = 1 sec

        //To handle screen rotations
        if (savedInstanceState != null) {
            cityTimeZoneArrayList = savedInstanceState.getParcelableArrayList("cityTimeZone");
            selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);
            lv.setAdapter(selectedCityTimeZoneAdapter);
        } else {
            cityTimeZoneArrayList = new ArrayList<>();
        }

        //Updating Times
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(delay);
                        runOnUiThread(() -> updateTime());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

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
                        selectedCityTimeZoneAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                showMessage("0 Cities Selected for Deletion!");
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
                loadItem();
                return true;
            case R.id.save_item: //save selected cities into the database
                saveItem();
                return true;
            case R.id.delete_item: //item delete from the database
                deleteItem();
                return true;
            case R.id.delete_db: //
                deleteDb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //updates time of selected cities using timezone info
    private void updateTime() {
        int size = cityTimeZoneArrayList.size();
        for (int i = 0; i < size; i++) {
            String timeZone = cityTimeZoneArrayList.get(i).getName();
            cityTimeZoneArrayList.get(i).setTime(Helper.convertTimeZoneToTime(timeZone));
            selectedCityTimeZoneAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cityTimeZone", cityTimeZoneArrayList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Save selected items to database in case the app is closed
        saveItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Thread.interrupted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Load items from database when the app is resumed
        loadItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int size = data.getIntExtra("Size", 0);
                if (size == 0) {
                    showMessage("0 Cities Selected!");
                } else {
                    if (cityTimeZoneArrayList.size() == 0) {
                        cityTimeZoneArrayList = data.getParcelableArrayListExtra("result");
                        selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);
                        lv.setAdapter(selectedCityTimeZoneAdapter);
                    } else {
                        ArrayList<CityTimeZone> temp = data.getParcelableArrayListExtra("result");
                        for (int i = 0; i < temp.size(); i++) {
                            if (cityTimeZoneArrayList.contains(temp.get(i))) {
                                showMessage("The City has already been selected.");
                            } else {
                                cityTimeZoneArrayList.add(temp.get(i));
                                selectedCityTimeZoneAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                showMessage("Failure!");
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
            showMessage("Number of cities selected: " + size);
        }
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showCitiesOnListView() {
        cityTimeZoneArrayList = Helper.mergeCityTimeZoneArrayLists(cityTimeZoneArrayList, iCityTimeZoneDAO.getSelectedCityTimeZones());
        selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);
        lv.setAdapter(selectedCityTimeZoneAdapter);
    }

    private void loadItem() {
        ArrayList<CityTimeZone> dbCityTimeZoneArrayList;
        dbCityTimeZoneArrayList = iCityTimeZoneDAO.getSelectedCityTimeZones();
        int size = dbCityTimeZoneArrayList.size();
        if (size > 0) {
            showCitiesOnListView();
            showMessage(size + " Items loaded from the database.");
        } else {
            showMessage("No Items in the database to load.");
        }
    }

    private void saveItem() {
        ArrayList<CityTimeZone> dbCityTimeZoneArrayList;
        ArrayList<CityTimeZone> ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
        dbCityTimeZoneArrayList = iCityTimeZoneDAO.getSelectedCityTimeZones(); //get already existing cities in database
        if (ctz.size() > 0) {
            for (int i = 0; i < ctz.size(); i++) {
                if (dbCityTimeZoneArrayList.contains(ctz.get(i)) == false) { //only add if it does not exist in database already
                    boolean success = iCityTimeZoneDAO.addSelectedCityTimeZone(ctz.get(i));
                    if (!success) {
                        showMessage("Selected City already exists in the Database. Cannot add.");
                    }
                }
            }
        }
    }

    private void deleteItem() {
        ArrayList<CityTimeZone> dbCityTimeZoneArrayList;
        ArrayList<CityTimeZone> ctz;
        ctz = Helper.getCheckedCities(cityTimeZoneArrayList);
        dbCityTimeZoneArrayList = iCityTimeZoneDAO.getSelectedCityTimeZones(); //get already existing cities in database
        if (ctz.size() > 0) {
            for (int i = 0; i < ctz.size(); i++) {
                //only add if it does not exist in database already
                if (dbCityTimeZoneArrayList.contains(ctz.get(i)) == true) {
                    boolean success = iCityTimeZoneDAO.deleteSelectedCityTimeZone(ctz.get(i));
                    if (success) {
                        showMessage("Item Deleted Successfully");
                        /* reading data from database when a city is deleted*/
                        cityTimeZoneArrayList.remove(ctz.get(i));
                        selectedCityTimeZoneAdapter.notifyDataSetChanged();
                        showCitiesOnListView();
                    } else {
                        showMessage("Could not delete item with name: " + ctz.get(i).getName());
                    }
                } else {
                    showMessage("Selected City does not exist in the Database. Cannot delete.");
                }
            }
        }
    }

    private void deleteDb() {
        int rowsDeleted = iCityTimeZoneDAO.deleteDatabase();
        showMessage("Database dropped. Number of tuples deleted: " + rowsDeleted);
    }

}