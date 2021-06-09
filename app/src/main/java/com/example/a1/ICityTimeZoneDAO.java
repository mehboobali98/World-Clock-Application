package com.example.a1;

import java.util.ArrayList;

public interface ICityTimeZoneDAO {
    boolean addCityTimeZone(CityTimeZone cityTimeZone);

    boolean deleteCityTimeZone(CityTimeZone cityTimeZone);

    ArrayList<CityTimeZone> getCityTimeZones();

    int deleteDatabase();

    boolean isEmpty();
}
