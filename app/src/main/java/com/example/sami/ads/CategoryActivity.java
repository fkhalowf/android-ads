package com.example.sami.ads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sami.ads.adapter.CategoryAdapter;
import com.example.sami.ads.adapter.RecentAdAdapter;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;
import com.example.sami.ads.helper.Auth;
import com.example.sami.ads.helper.UploadImage;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.AdJSON;
import com.example.sami.ads.parsers.CategoryJSON;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity {


    List<Category> categoryList;
    ProgressBar progressBar;
    ListView listView;
    Button buttonAddAd;
    String api;

    LinearLayout linearLayoutAuth;
    LinearLayout linearLayoutRegister;
    ScrollView scrollView;

    Button buttonLogOut;
    Button buttonRegister;
    Button buttonSignIn;
    Button buttonMyAds;
    TextView buttonShowAllAds;

    //recent ads
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecentAdAdapter recentAdsAdapter;
    List<Ad> recentAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        buttonAddAd = (Button) findViewById(R.id.button_new_ad);

        //set variable for recent ads
        recentAds = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.RecycleViewRecentAds);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);

        recyclerView.setLayoutManager(layoutManager);
        recentAdsAdapter = new RecentAdAdapter(recentAds);
        recyclerView.setAdapter(recentAdsAdapter);

        recentAdsAdapter.setOnItemClickListener(new RecentAdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(CategoryActivity.this, AdViewActivity.class);
                intent.putExtra("id", recentAds.get(position).getId());
                startActivity(intent);
                Log.d("sos", recentAds.get(position).getId() + "");
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
        CategoriesData categoriesData = new CategoriesData();
        categoriesData.execute();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category temp = categoryList.get(i);
                Intent intent = new Intent(CategoryActivity.this, AdsActivity.class);
                intent.putExtra("id", temp.getId());
                intent.putExtra("name", temp.getName());
                startActivity(intent);

            }
        });

        UploadImage.initImageLoader(this);

        buttonAddAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryActivity.this, AddAdActivity.class));
            }
        });

        linearLayoutAuth = (LinearLayout) findViewById(R.id.button_bar_auth);
        linearLayoutRegister = (LinearLayout) findViewById(R.id.button_bar_register);
        buttonLogOut = (Button) findViewById(R.id.button_logout);

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.logOut(CategoryActivity.this);
                Snackbar snackbar = Snackbar.make(buttonLogOut, R.string.sing_out, Snackbar.LENGTH_SHORT);
                snackbar.show();
                showAuthOrRegisterPanel();
            }
        });

        buttonRegister = (Button) findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
            }
        });

        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryActivity.this, SigninActivity.class));
            }
        });

        buttonMyAds = (Button) findViewById(R.id.button_user_ad);
        buttonMyAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryActivity.this, UserAdActivity.class));
            }
        });

        buttonShowAllAds = (TextView) findViewById(R.id.button_all_ads);
        buttonShowAllAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, AdsActivity.class);
                intent.putExtra("id", "0");
                startActivity(intent);
            }
        });

        scrollView = (ScrollView) findViewById(R.id.scrollView);

    }


    public void viewCategories() {
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, R.layout.category_item, categoryList);
        listView.setAdapter(categoryAdapter);
    }

    private Bitmap getImage(String path) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(path);
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        if (this.api != null) {
            //menuRegister.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.api = preferences.getString("api", null);
        showAuthOrRegisterPanel();
    }

    void showAuthOrRegisterPanel() {
        if (Auth.isLogin(this)) {
            linearLayoutAuth.setVisibility(View.VISIBLE);
            linearLayoutRegister.setVisibility(View.GONE);
        } else {
            linearLayoutAuth.setVisibility(View.GONE);
            linearLayoutRegister.setVisibility(View.VISIBLE);
        }
    }

    private class CategoriesData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... strings) {

            try {
                categoryList = Category.categoryList();
                //temp
                HashMap<String, String> params = new HashMap<>();
                params.put("start", "0");
                String result = HttpManger.getString(Config.AD_BY_CATEGORY, params, Auth.getApi(CategoryActivity.this));
                recentAds.addAll(AdJSON.parser(result));
                City.getCityList();
                //Category.categoryList();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {

            recentAdsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            viewCategories();
        }
    }
}
