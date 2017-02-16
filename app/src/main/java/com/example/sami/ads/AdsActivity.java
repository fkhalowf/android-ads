package com.example.sami.ads;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.sami.ads.adapter.AdAdapter;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.parsers.AdJSON;
import com.example.sami.ads.uinterface.OnActivityGetAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdsActivity extends AppCompatActivity implements AbsListView.OnScrollListener, OnActivityGetAds {

    private static final int CATEGORY_FILTER_ID = 1001;
    private static final int CITY_FILTER_ID = 1002;
    public List<Ad> ads;
    Category category;
    City city;
    String price = "0";
    ListView listViewAds;
    ProgressBar progressBar;
    AdAdapter adAdapter;
    TextView textViewCategory;
    TextView textViewCity;
    TextView textViewFilter;
    LinearLayout linearLayoutWrapper;
    boolean flag_loading = false;
    private AsyncAdsData asyncAdsData;
    private int currentScrollState;
    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        // add back arrow to toolbar
        category = new Category();
        city = new City();
        city.setId(0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        category.setName(getIntent().getExtras().getString("name"));
        category.setId(getIntent().getExtras().getInt("id"));


        listViewAds = (ListView) findViewById(R.id.listView_ads);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_ads_activity);

        ads = new ArrayList<>();
        adAdapter = new AdAdapter(this, R.layout.ad_item, ads);
        listViewAds.setAdapter(adAdapter);
        listViewAds.setOnScrollListener(this);


        listViewAds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ad tempAd = ads.get(i);
                Intent intent = new Intent(AdsActivity.this, AdViewActivity.class);
                intent.putExtra("id", tempAd.getId());
                intent.putExtra("primary_image", tempAd.imageUrl);
                startActivity(intent);
            }
        });


        prepareFilter();
        setTitleOfActivity();

        getAdsData(0);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        /*
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            Log.d("sos" ,firstVisibleItem +" "+ visibleItemCount);
            if (flag_loading == false) {
                flag_loading = true;
                //addMoreitems();
            }
        }
        */

        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
            Log.d("sos", currentVisibleItemCount + " " + currentScrollState);
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
           /* if(!isLoading){
                isLoading = true;
                loadMoreData();
            }*/
        }
    }

    @Override
    public void addMoreItem() {
        if (flag_loading == false) {
            flag_loading = true;
            getAdsData(ads.size());
        }
    }

    private void viewDataInListView() {
        progressBar.setVisibility(View.GONE);
        linearLayoutWrapper.setVisibility(View.VISIBLE);
        adAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareFilter() {
        linearLayoutWrapper = (LinearLayout) findViewById(R.id.layout_wrapper);

        textViewCategory = (TextView) findViewById(R.id.text_view_category);
        textViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdsActivity.this, SelectCategoryActivity.class);
                intent.putExtra("activity", "filter");
                startActivityForResult(intent, CATEGORY_FILTER_ID);
            }
        });

        textViewCity = (TextView) findViewById(R.id.textView_city);
        textViewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AdsActivity.this, SelectCityActivity.class), CITY_FILTER_ID);
            }
        });

        textViewFilter = (TextView) findViewById(R.id.text_view_filter);
        textViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sort_by));
        View dialogView = getLayoutInflater().inflate(R.layout.choose_dialog, null);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.filter_group);
        builder.setView(dialogView);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(getString(R.string.select), null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                textViewFilter.setText(radioButton.getText());
                dialog.dismiss();
                switch (radioButton.getId()) {
                    case R.id.radioButton_recent:
                        price = "0";
                        break;
                    case R.id.radioButton_hi_price:
                        price = "max";
                        break;
                    case R.id.radioButton_low_price:
                        price = "min";
                        break;
                }
                getAdsData(0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CATEGORY_FILTER_ID) {
                category.setId(data.getIntExtra("id", 0));
                category.setName(data.getStringExtra("name"));
                getAdsData(0);
                setTitleOfActivity();
            } else if (requestCode == CITY_FILTER_ID) {
                city.setId(data.getIntExtra("id", 0));
                city.setName(data.getStringExtra("name"));
                textViewCity.setText(city.getName());
                getAdsData(0);
            }
        }
    }

    private void getAdsData(int start) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("category_id", category.getId() + "");
        requestParams.put("city_id", city.getId() + "");
        requestParams.put("price", price);
        requestParams.put("start", start + "");

        if (start == 0) {
            linearLayoutWrapper.setVisibility(View.GONE);
            this.ads.clear();
        }
        asyncAdsData = new AsyncAdsData();
        asyncAdsData.execute(requestParams);
    }

    private void setTitleOfActivity() {
        if (category.getId() != 0) {
            setTitle(getString(R.string.ads_in) + category.getName());
            textViewCategory.setText(category.getName());
        } else {
            setTitle(R.string.title_activity_all_ads);
            textViewCategory.setText(R.string.all_categories);
        }
    }

    public class AsyncAdsData extends AsyncTask<Map, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Map... data) {
            String result = HttpManger.getString(Config.AD_BY_CATEGORY, data[0], null);
            if (result != null) {
                if (!result.equals("null")) {
                    List<Ad> tempAds = AdJSON.parser(result);
                    if (tempAds != null) {
                        ads.addAll(tempAds);
                        AdsActivity.this.flag_loading = false;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewDataInListView();
        }
    }
}
