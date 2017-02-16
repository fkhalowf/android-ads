package com.example.sami.ads.entities;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URL;

/**
 * Created by sami on 1/31/2017.
 */
public class AdImage {
    private Uri uri;
    private String url;
    private Bitmap bitmap;


    public AdImage() {

    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
