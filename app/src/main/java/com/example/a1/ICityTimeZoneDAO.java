package com.example.a1;

import java.util.ArrayList;

public interface ICityTimeZoneDAO {
    boolean addCityTimeZone(CityTimeZone cityTimeZone);

    boolean addSelectedCityTimeZone(CityTimeZone cityTimeZone);

    boolean deleteCityTimeZone(CityTimeZone cityTimeZone);

    boolean deleteSelectedCityTimeZone(CityTimeZone cityTimeZone);

    ArrayList<CityTimeZone> getCityTimeZones();

    ArrayList<CityTimeZone> getSelectedCityTimeZones();

    int deleteDatabase();

    boolean isEmpty();

}
