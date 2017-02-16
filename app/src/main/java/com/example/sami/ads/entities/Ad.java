package com.example.sami.ads.entities;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.sami.ads.Config;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.AdJSON;

import java.util.List;

/**
 * Created by sami on 1/31/2017.
 */
public class Ad {
    //Todo change to private
    public String imageUrl;
    public Bitmap primaryBitmap;
    private int id;
    private Category category;
    private City city;
    private String title;
    private String detail;
    private int price;
    private long date;
    private List<AdImage> images;
    private String mobile;
    private User user;
    private boolean active;

    public Ad() {

    }

    public Bitmap getPrimaryBitmap() {
        return primaryBitmap;
    }

    public void setPrimaryBitmap(Bitmap primaryBitmap) {
        this.primaryBitmap = primaryBitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<AdImage> getImages() {
        return images;
    }

    public void setImages(List<AdImage> images) {
        this.images = images;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
