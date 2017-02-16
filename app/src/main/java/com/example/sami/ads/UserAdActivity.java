package com.example.sami.ads;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sami.ads.adapter.AdAdapter;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.helper.Auth;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.AdJSON;
import com.example.sami.ads.uinterface.OnActivityGetAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdActivity extends AppCompatActivity implements OnActivityGetAds {

    List<Ad> ads;
    ListView listViewAds;
    ProgressBar progressBar;
    AdAdapter adAdapter;
    boolean flag_loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ad);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        prepareElement();
        sendDataRequest();
    }

    private void sendDataRequest() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("start", "0");
        AsyncAdsData asyncAdsData = new AsyncAdsData();
        asyncAdsData.execute(requestParams);
    }

    private void prepareElement() {
        ads = new ArrayList<>();
        listViewAds = (ListView) findViewById(R.id.listView_ads);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_ads_activity);
        adAdapter = new AdAdapter(this, R.layout.ad_item, ads);
        listViewAds.setAdapter(adAdapter);
        listViewAds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ad tempAd = ads.get(i);
                Intent intent = new Intent(UserAdActivity.this, AdViewActivity.class);
                intent.putExtra("id", tempAd.getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void addMoreItem() {
        if (flag_loading == false) {
            flag_loading = true;
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("start", ads.size() + "");
            AsyncAdsData asyncAdsData = new AsyncAdsData();
            asyncAdsData.execute(requestParams);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, CategoryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void deactiveAd(int position) {
        Ad ad = ads.get(position);
        AsyncDeactiveAd asyncDeactiveAd = new AsyncDeactiveAd();
        asyncDeactiveAd.execute(ad);
    }

    private class AsyncAdsData extends AsyncTask<Map, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Map... params) {
            String result = HttpManger.getString(Config.USER_ADS, params[0], Auth.getApi(UserAdActivity.this));
            if (result != null) {
                List<Ad> tempAds = AdJSON.parser(result);
                if (tempAds != null) {
                    ads.addAll(tempAds);
                    UserAdActivity.this.flag_loading = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            adAdapter.notifyDataSetChanged();
        }
    }

    private class AsyncDeactiveAd extends AsyncTask<Ad, Void, Void> {

        @Override
        protected Void doInBackground(Ad... Ads) {
            Map<String, String> params = new HashMap<>();
            params.put("id", Ads[0].getId() + "");
            String result = HttpManger.getString(Config.Ad_DEACTIVE, params, Auth.getApi(UserAdActivity.this));
            if (!result.isEmpty()) {
                Ads[0].setActive(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adAdapter.notifyDataSetChanged();
        }
    }
}
