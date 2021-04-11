package com.example.a1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;


public class TimeZoneAdapter extends ArrayAdapter<CityTimeZone> {
    private List<CityTimeZone> cityTimeZoneList;
    private Context context;

    public TimeZoneAdapter(List<CityTimeZone> cityTimeZoneList, Context context) {
        super(context, R.layout.adapter_view_layout, cityTimeZoneList);
        this.cityTimeZoneList = cityTimeZoneList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cityTimeZoneList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CityTimeZoneHolder holder = new CityTimeZoneHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adapter_view_layout, null);

            holder.cityName = v.findViewById(R.id.city_text_view);
            holder.cityTime = v.findViewById(R.id.time_text_view);
            holder.checkBox = v.findViewById(R.id.check_box);
            holder.checkBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) context);

            v.setTag(holder);
        } else {
            holder = (CityTimeZoneHolder) v.getTag();
        }

        CityTimeZone cityTimeZone = cityTimeZoneList.get(position);
        holder.cityName.setText(cityTimeZone.getName());
        holder.cityTime.setText(cityTimeZone.getTime());
        holder.checkBox.setChecked(cityTimeZone.isSelected());

        holder.checkBox.setTag(cityTimeZone);

        return v;
    }

    private static class CityTimeZoneHolder {
        public TextView cityName;
        public TextView cityTime;
        public CheckBox checkBox;
    }
}
