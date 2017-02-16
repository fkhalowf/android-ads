package com.example.sami.ads.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.sami.ads.R;
import com.example.sami.ads.entities.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by sami on 1/30/2017.
 */
public class CityAdapter extends ArrayAdapter<City> {
    private final Context context;
    private final int resource;
    private final List<City> objects;
    List<City> filterCity;

    public CityAdapter(Context context, int resource, int item, List<City> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.filterCity = new ArrayList<>();
        filterCity.addAll(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_select_item, parent, false);
        City city = objects.get(position);
        TextView textView = (TextView) view.findViewById(R.id.textView_category_select);
        textView.setText(city.getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }


}
