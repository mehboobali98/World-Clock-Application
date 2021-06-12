package com.example.a1;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class Helper {


    public static ArrayList<CityTimeZone> mergeCityTimeZoneArrayLists(ArrayList<CityTimeZone> cityTimeZoneArrayList, ArrayList<CityTimeZone> dbCityTimeZoneArrayList) {
        int size = dbCityTimeZoneArrayList.size();
        for (int i = 0; i < size; i++) {
            if (!cityTimeZoneArrayList.contains(dbCityTimeZoneArrayList.get(i))) {
                cityTimeZoneArrayList.add(dbCityTimeZoneArrayList.get(i));
            }
        }
        return new ArrayList<>(cityTimeZoneArrayList);
    }

    //Returns Cities whose checkbox have been selected
    public static ArrayList<CityTimeZone> getCheckedCities(ArrayList<CityTimeZone> cityTimeZoneArrayList) {
        ArrayList<CityTimeZone> checkedCities = new ArrayList<>();

        for (int i = 0; i < cityTimeZoneArrayList.size(); i++) {
            if (cityTimeZoneArrayList.get(i).isSelected()) {
                CityTimeZone ctz = new CityTimeZone(cityTimeZoneArrayList.get(i).getName(), cityTimeZoneArrayList.get(i).getCountryCode());
                ctz.setTime(cityTimeZoneArrayList.get(i).getTime());
                ctz.setSelected(cityTimeZoneArrayList.get(i).isSelected());
                checkedCities.add(ctz);
            }
        }
        return checkedCities;
    }

    public static String convertTimeZoneToTime(String timeZone) {
        String time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        time = calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
        if (calendar.get(Calendar.AM_PM) == 0)
            time = time + " AM";
        else
            time = time + " PM";
        return time;
    }

    public static String convertCountryCodeToFlag(String countryCode) {
        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        int firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset;

        String flag = new String(Character.toChars(firstChar))
                + new String(Character.toChars(secondChar));
        return flag;
    }


}
