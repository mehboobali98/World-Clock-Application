package com.example.a1;

import java.util.ArrayList;

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
}
