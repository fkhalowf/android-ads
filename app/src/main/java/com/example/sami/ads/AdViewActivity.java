package com.example.sami.ads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sami.ads.adapter.ImagesViewAdapter;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.AdImage;
import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;
import com.example.sami.ads.entities.User;
import com.example.sami.ads.helper.Utility;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.ImagesJSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

public class AdViewActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout linearLayoutWrapper;
    ScrollView scrollView;
    ViewPager pager;
    TextView textViewTitle;
    TextView textViewDetail;
    TextView textViewUser;
    TextView textViewMobile;
    TextView textViewCity;
    TextView textViewDate;
    TextView textViewPrice;
    private int id;
    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        id = getIntent().getExtras().getInt("id");

        progressBar = (ProgressBar) findViewById(R.id.progressBar_ad_view);
        linearLayoutWrapper = (LinearLayout) findViewById(R.id.linearLayout_ad_view_wrapper);
        scrollView = (ScrollView) findViewById(R.id.scrollView_wrapper);
        pager = (ViewPager) findViewById(R.id.pager);

        textViewTitle = (TextView) findViewById(R.id.textView_view_title);
        textViewDetail = (TextView) findViewById(R.id.textView_view_detail);
        textViewUser = (TextView) findViewById(R.id.textView_detail_user);
        textViewMobile = (TextView) findViewById(R.id.textView_detail_mobile);
        textViewCity = (TextView) findViewById(R.id.textView_detail_city);
        textViewDate = (TextView) findViewById(R.id.textView_detail_date);
        textViewPrice = (TextView) findViewById(R.id.textView_detail_price);

        ad = new Ad();
        ad.setId(id);

        AsyncImageUrls asyncImageUrls = new AsyncImageUrls();
        asyncImageUrls.execute(id + "");

        AsyncAdData asyncAdData = new AsyncAdData();
        asyncAdData.execute(id + "");


        //Hide image pager if ads has not images


    }

    private void getImagesData() {
        pager.setVisibility(View.VISIBLE);
        pager.setAdapter(new ImagesViewAdapter(this, ad.getImages()));
    }

    private void fillDataOnField(String adData) {
        try {
            JSONObject object = new JSONObject(adData);

            ad.setTitle(object.getString("title"));
            ad.setDetail(object.getString("detail"));
            ad.setPrice(object.getInt("price"));
            ad.setMobile(object.getString("mobile"));

            //Set Category
            Category category = new Category();
            category.setId(object.getInt("category_id"));
            category.setName(object.getString("category_name"));
            ad.setCategory(category);

            //Set City
            City city = new City();
            city.setId(object.getInt("city_id"));
            city.setName(object.getString("city_name"));
            ad.setCity(city);

            //Set User
            User user = new User();
            user.setUsername(object.getString("user_name"));
            user.setId(object.getInt("user_id"));
            ad.setUser(user);

            textViewTitle.setText(ad.getTitle());
            textViewDetail.setText(ad.getDetail());
            textViewUser.setText(ad.getUser().getUsername());
            textViewMobile.setText(ad.getMobile());
            textViewCity.setText(ad.getCity().getName());
            textViewPrice.setText(ad.getPrice() + "");

            Long time = Utility.dateTimeFromMysql(object.getString("date"));
            String adDate = Utility.getTimeAgo(time, this);
            textViewDate.setText(adDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncAdData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... ids) {
            Map<String, String> params = new HashMap<>();
            params.put("id", ids[0]);
            return HttpManger.getString(Config.AD_VIEW, params, null);
        }

        @Override
        protected void onPostExecute(String adData) {
            progressBar.setVisibility(View.GONE);
            linearLayoutWrapper.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            if (adData != null) {
                fillDataOnField(adData);
            }
        }
    }

    private class AsyncImageUrls extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... ids) {
            Map<String, String> params = new HashMap<>();
            params.put("id", ids[0]);
            return HttpManger.getString(Config.IMAGES_AD, params, null);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            if (!result.equals("[]")) {
                List<AdImage> adImages = ImagesJSON.parse(result);
                ad.setImages(adImages);
                getImagesData();
            }
        }
    }

}
