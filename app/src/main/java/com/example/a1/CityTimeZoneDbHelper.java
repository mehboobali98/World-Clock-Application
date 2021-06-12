package com.example.a1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class CityTimeZoneDbHelper extends SQLiteOpenHelper {
    public static final String CITY_TIMEZONE_TABLE = "CITY_TIMEZONE_TABLE";
    public static final String SELECTED_CITY_TIMEZONE_TABLE = "SELECTED_CITY_TIMEZONE_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String CITY_NAME = "CITY_NAME";
    public static final String CITY_COUNTRY_CODE = "CITY_COUNTRY_CODE";
    public static final String SELECTED_CITY = "SELECTED_CITY";
    public static final String CITYTIMEZONE_DB = "citytimezone.db";

    public CityTimeZoneDbHelper(@Nullable Context context) {
        super(context, CITYTIMEZONE_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CITY_TIMEZONE_TABLE + " (" + COLUMN_ID + " Integer Primary Key AUTOINCREMENT, " + CITY_NAME + " text, " + CITY_COUNTRY_CODE + " text, " + SELECTED_CITY + " BOOL)";
        db.execSQL(createTableStatement);
        createTableStatement = "CREATE TABLE " + SELECTED_CITY_TIMEZONE_TABLE + " (" + COLUMN_ID + " Integer Primary Key AUTOINCREMENT, " + CITY_NAME + " text, " + CITY_COUNTRY_CODE + " text, " + SELECTED_CITY + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + CITY_TIMEZONE_TABLE;
        db.execSQL(dropTableQuery);
        dropTableQuery = "DROP TABLE IF EXISTS " + SELECTED_CITY_TIMEZONE_TABLE;
        db.execSQL(dropTableQuery);
        Log.d("MyDbHelper", "Tables created");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
