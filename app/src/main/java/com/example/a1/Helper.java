package com.example.a1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class Helper {

    //Returns Cities whose checkbox have been selected
    public static ArrayList<CityTimeZone> getCheckedCities(ArrayList<CityTimeZone> cityTimeZoneArrayList) {
        ArrayList<CityTimeZone> checkedCities = new ArrayList<>();

        for (int i = 0; i < cityTimeZoneArrayList.size(); i++) {
            if (cityTimeZoneArrayList.get(i).isSelected() == true) {
                CityTimeZone ctz = new CityTimeZone(cityTimeZoneArrayList.get(i).getName(), cityTimeZoneArrayList.get(i).getTime());
                checkedCities.add(ctz);
            }
        }
        return checkedCities;
    }

    //Get Available Timezones
    public static ArrayList<String> getTimeZones()
    {
        Calendar calNewYork = Calendar.getInstance();
        ArrayList<String> timeZones = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
        ArrayList<String> newTimeZones = new ArrayList<>();
        for (String s : timeZones) {
            calNewYork.setTimeZone(TimeZone.getTimeZone(s));
            String time = calNewYork.get(Calendar.HOUR_OF_DAY) + ":"
                    + calNewYork.get(Calendar.MINUTE) + ":" + calNewYork.get(Calendar.SECOND);
            newTimeZones.add(s + " " + time);
        }
        return newTimeZones;
    }
}
