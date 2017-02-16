package com.example.sami.ads.parsers;

import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;
import com.example.sami.ads.entities.User;
import com.example.sami.ads.helper.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami on 2/4/2017.
 */
public class AdJSON {
    public static List<Ad> parser(String input) {
        try {

            JSONArray array = new JSONArray(input);
            List<Ad> ads = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                Ad ad = new Ad();
                ad.setId(object.getInt("id"));

                //set category values
                Category category = new Category();
                category.setId(object.getInt("category_id"));
                category.setName(object.getString("category_name"));
                ad.setCategory(category);

                //set city
                City city = new City();
                city.setId(object.getInt("city_id"));
                city.setName(object.getString("city_name"));
                ad.setCity(city);

                //set User
                User user = new User();
                user.setId(object.getInt("user_id"));
                user.setUsername(object.getString("user_name"));
                ad.setUser(user);

                //set other data
                ad.setTitle(object.getString("title"));
                ad.setDetail(object.getString("detail"));
                ad.setPrice(object.getInt("price"));
                ad.setMobile(object.getString("mobile"));
                ad.setDate(Utility.dateTimeFromMysql(object.getString("date")));
                ad.setActive(object.getInt("active") != 0);

                //temp data
                String path = object.getString("path");
                if (!path.equals("null")) {
                    ad.imageUrl = path;
                }
                ads.add(ad);
            }
            return ads;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
