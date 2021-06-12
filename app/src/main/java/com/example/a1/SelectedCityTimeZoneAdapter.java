package com.example.a1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SelectedCityTimeZoneAdapter extends ArrayAdapter<CityTimeZone> implements Filterable {
    private List<CityTimeZone> cityTimeZoneList;
    private List<CityTimeZone> filteredTimeZoneList;
    private Context context;
    private LayoutInflater inflater;
    private SelectedCityTimeZoneAdapter.SelectedCityTimeZoneFilter filter;

    public SelectedCityTimeZoneAdapter(List<CityTimeZone> cityTimeZoneList, Context context) {
        super(context, R.layout.selected_city_adapter_view, cityTimeZoneList);
        this.cityTimeZoneList = cityTimeZoneList;
        this.filteredTimeZoneList = cityTimeZoneList;
        this.context = context;
    }

    public CityTimeZone getItem(int position) {
        return filteredTimeZoneList.get(position);
    }

    @Override
    public int getCount() {
        return filteredTimeZoneList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new SelectedCityTimeZoneAdapter.SelectedCityTimeZoneFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        SelectedCityTimeZoneAdapter.SelectedCityTimeZoneHolder holder = new SelectedCityTimeZoneAdapter.SelectedCityTimeZoneHolder();

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.selected_city_adapter_view, null);

            holder.cityName = v.findViewById(R.id.city_tv_first_activity);
            holder.cityTime = v.findViewById(R.id.time_tv_first_activity);
            holder.cityFlag = v.findViewById(R.id.city_flag_tv);
            holder.checkBox = v.findViewById(R.id.check_box_first_activity);
            holder.timeDifference = v.findViewById(R.id.time_diff_tv);
            holder.checkBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) context);
            v.setTag(holder);
        } else {
            holder = (SelectedCityTimeZoneAdapter.SelectedCityTimeZoneHolder) v.getTag();
        }

        CityTimeZone cityTimeZone = filteredTimeZoneList.get(position);
        holder.cityName.setText(cityTimeZone.getName());
        holder.cityTime.setText(Helper.convertTimeZoneToTime(cityTimeZone.getName()));
        holder.cityFlag.setText(Helper.convertCountryCodeToFlag(cityTimeZone.getCountryCode()));
        holder.timeDifference.setText(Helper.getTimeDifference(cityTimeZone.getName()));
        holder.checkBox.setChecked(cityTimeZone.isSelected());
        holder.checkBox.setTag(cityTimeZone);
        return v;
    }

    private class SelectedCityTimeZoneFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<CityTimeZone> filteredList = new ArrayList<>();
                for (int i = 0; i < cityTimeZoneList.size(); i++) {
                    if (cityTimeZoneList.get(i).getName().contains(constraint.toString())) {
                        filteredList.add(cityTimeZoneList.get(i));
                    }
                }
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            } else {
                filterResults.count = cityTimeZoneList.size();
                filterResults.values = cityTimeZoneList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTimeZoneList = (ArrayList<CityTimeZone>) results.values;
            notifyDataSetChanged();
        }
    }

    private static class SelectedCityTimeZoneHolder {
        public TextView cityName;
        public TextView cityTime;
        public TextView cityFlag;
        public TextView timeDifference;
        public CheckBox checkBox;
    }
}

