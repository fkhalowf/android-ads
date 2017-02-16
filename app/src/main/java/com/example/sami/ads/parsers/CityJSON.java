package com.example.sami.ads.parsers;

import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami on 1/30/2017.
 */
public class CityJSON {
    public static List<City> parser(String input) {
        try {
            JSONArray array = new JSONArray(input);
            List<City> cityList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String cityName = object.getString("name");
                int cityId = object.getInt("id");
                City city = new City(cityId, cityName);
                cityList.add(city);
            }
            return cityList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
