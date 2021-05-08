package com.example.a1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CityTimeZoneDbHelper extends SQLiteOpenHelper {

    public static final String CITY_TIME = "CITY_TIME";
    public static final String CITY_TIMEZONE_TABLE = CITY_TIME + "ZONE_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String CITY_NAME = "CITY_NAME";
    public static final String SELECTED_CITY = "SELECTED_CITY";
    public static final String CITYTIMEZONE_DB = "citytimezone.db";

    public CityTimeZoneDbHelper(@Nullable Context context) {
        super(context, CITYTIMEZONE_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CITY_TIMEZONE_TABLE + " (" + COLUMN_ID + " Integer Primary Key AUTOINCREMENT, " + CITY_NAME + " text, " + CITY_TIME + " text, " + SELECTED_CITY + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + CITY_TIMEZONE_TABLE;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addCityTimeZone(CityTimeZone cityTimeZone) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityTimeZone.getName());
        cv.put(CITY_TIME, cityTimeZone.getTime());
        cv.put(SELECTED_CITY, cityTimeZone.isSelected());

        long row;
        row = database.insert(CITY_TIMEZONE_TABLE, null, cv);
        database.close();
        if (row == -1)
            return false;
        return true;
    }

    public boolean deleteCityTimeZone(CityTimeZone cityTimeZone) {
        SQLiteDatabase database = getWritableDatabase();
        int deletedCount = database.delete(CITY_TIMEZONE_TABLE, CITY_NAME + " =? ", new String[]{cityTimeZone.getName()});
        database.close();
        if (deletedCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<CityTimeZone> getCityTimeZones() {
        ArrayList<CityTimeZone> cityTimeZoneArrayList = new ArrayList<>();
        String queryString = "Select * FROM " + CITY_TIMEZONE_TABLE;
        SQLiteDatabase database = getReadableDatabase();
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

    public int deleteDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        int rowsDeleted = database.delete(CITY_TIMEZONE_TABLE, "1", null);
        database.close();
        return rowsDeleted;
    }
}
