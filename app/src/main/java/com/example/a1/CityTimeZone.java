package com.example.a1;

import android.os.Parcel;
import android.os.Parcelable;

public class CityTimeZone implements Parcelable {
    private String name;
    private String time;
    private String countryCode;
    private boolean selected;

    public CityTimeZone(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
        this.time = null;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    protected CityTimeZone(Parcel in) {
        name = in.readString();
        time = in.readString();
        countryCode = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<CityTimeZone> CREATOR = new Creator<CityTimeZone>() {
        @Override
        public CityTimeZone createFromParcel(Parcel in) {
            return new CityTimeZone(in);
        }

        @Override
        public CityTimeZone[] newArray(int size) {
            return new CityTimeZone[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(countryCode);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CityTimeZone)) {
            return false;
        }
        CityTimeZone ctz = (CityTimeZone) obj;
        return name.equals(ctz.name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
