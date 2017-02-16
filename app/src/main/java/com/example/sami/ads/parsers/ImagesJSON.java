package com.example.sami.ads.parsers;

import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.AdImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami on 2/6/2017.
 */
public class ImagesJSON {
    public static List<AdImage> parse(String input) {
        try {
            JSONArray array = new JSONArray(input);
            List<AdImage> adImages = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                AdImage adImage = new AdImage();
                JSONObject object = array.getJSONObject(i);
                adImage.setUrl(object.getString("path"));
                adImages.add(adImage);
            }
            return adImages;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
