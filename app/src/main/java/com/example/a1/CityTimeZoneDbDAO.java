package com.example.a1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CityTimeZoneDbDAO implements ICityTimeZoneDAO {
    private Context context;

    public CityTimeZoneDbDAO() {

    }

    public CityTimeZoneDbDAO(Context ctx) {
        context = ctx;
    }

    @Override
    public boolean addCityTimeZone(CityTimeZone cityTimeZone) {
        CityTimeZoneDbHelper dbHelper = new CityTimeZoneDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.CITY_NAME, cityTimeZone.getName());
        cv.put(dbHelper.CITY_TIME, cityTimeZone.getTime());
        cv.put(dbHelper.SELECTED_CITY, cityTimeZone.isSelected());

        long row;
        row = database.insert(dbHelper.CITY_TIMEZONE_TABLE, null, cv);
        database.close();
        if (row == -1)
            return false;
        return true;
    }

    @Override
    public boolean deleteCityTimeZone(CityTimeZone cityTimeZone) {
        CityTimeZoneDbHelper dbHelper = new CityTimeZoneDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int deletedCount = database.delete(dbHelper.CITY_TIMEZONE_TABLE, dbHelper.CITY_NAME + " =? ", new String[]{cityTimeZone.getName()});
        database.close();
        if (deletedCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<CityTimeZone> getCityTimeZones() {
        CityTimeZoneDbHelper dbHelper = new CityTimeZoneDbHelper(context);
        ArrayList<CityTimeZone> cityTimeZoneArrayList = new ArrayList<>();
        String queryString = "Select * FROM " + dbHelper.CITY_TIMEZONE_TABLE;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.moveToFirst()) //check if tuples exist and add to list
        {
            do {
                String cityName = cursor.getString(1);
                String cityTime = cursor.getString(2);
                boolean isSelected = cursor.getInt(3) == 1 ? true : false;
                CityTimeZone cityTimeZone = new CityTimeZone(cityName, cityTime);
                cityTimeZone.setSelected(isSelected);
                cityTimeZoneArrayList.add(cityTimeZone);
            } while (cursor.moveToNext());
        } else {
            //to-do:
        }
        database.close();
        return cityTimeZoneArrayList;
    }

    @Override
    public int deleteDatabase() {
        CityTimeZoneDbHelper dbHelper = new CityTimeZoneDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted = database.delete(dbHelper.CITY_TIMEZONE_TABLE, "1", null);
        database.close();
        return rowsDeleted;
    }
}
