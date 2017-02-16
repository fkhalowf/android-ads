package com.example.sami.ads.entities;

import android.graphics.Bitmap;

import com.example.sami.ads.Config;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.CategoryJSON;

import java.util.List;

/**
 * Created by sami on 1/21/2017.
 */
public class Category {
    private static List<Category> categoryList;
    private int id;
    private String name;
    private Bitmap image;

    public Category() {
    }

    public static List<Category> categoryList() {
        if (categoryList == null) {
            String responseText = HttpManger.getString(Config.HOME_SCREEN_URL, null, null);
            categoryList = CategoryJSON.parser(responseText);
        }
        return categoryList;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
