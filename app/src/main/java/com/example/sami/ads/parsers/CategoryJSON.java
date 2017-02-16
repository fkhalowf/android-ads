package com.example.sami.ads.parsers;

import com.example.sami.ads.entities.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami on 1/21/2017.
 */
public class CategoryJSON {
    public static List<Category> parser(String inputData) {
        try {
            JSONArray array = new JSONArray(inputData);
            List<Category> categoryList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Category category = new Category();
                JSONObject object = array.getJSONObject(i);
                category.setName(object.getString("name"));
                category.setId(object.getInt("id"));
                categoryList.add(category);
            }
            return categoryList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
