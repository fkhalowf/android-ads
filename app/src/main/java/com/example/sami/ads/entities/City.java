package com.example.sami.ads.entities;

import com.example.sami.ads.Config;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.CategoryJSON;
import com.example.sami.ads.parsers.CityJSON;

import java.util.List;

public class City {
    private static List<City> cityList;
    private int id;
    private String name;

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public City() {

    }

    public static List<City> getCityList() {
        if (cityList == null) {
            String responseText = HttpManger.getString(Config.CITY_URL, null, null);
            if (responseText != null) {
                cityList = CityJSON.parser(responseText);
            }
        }
        return cityList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
