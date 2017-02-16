package com.example.sami.ads;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sami.ads.adapter.AdImagesAdapter;
import com.example.sami.ads.adapter.CityAdapter;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.entities.Category;
import com.example.sami.ads.entities.City;
import com.example.sami.ads.helper.UploadImage;
import com.example.sami.ads.helper.Validation;
import com.example.sami.ads.http.HttpManger;
import com.soundcloud.android.crop.Crop;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddAdActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1001;
    private static final int SELECT_CATEGORY_REQUEST = 1002;
    private static final int START_CAMERA = 1003;
    static int tempUriIndex;
    private final SimpleDateFormat imageDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
    GridView gridViewAdImages;
    List<Uri> imagesUri;
    List<City> cityList;
    Uri uriCameraPhoto;
    AdImagesAdapter adImagesAdapter;
    CityAdapter cityAdapter;

    EditText editTextCategory;
    EditText editTextTitle;
    EditText editTextDetail;
    EditText editTextPrice;
    EditText editTextPhone;
    ProgressBar progressBar;
    Spinner spinnerCities;
    ImageButton imageButtonCamera;
    ImageButton imageButtonAlbum;
    Button buttonCancel;
    Button buttonSave;
    private Category selectedCategory;
    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        gridViewAdImages = (GridView) findViewById(R.id.gridViewAdImages);
        editTextCategory = (EditText) findViewById(R.id.editTextCategory);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDetail = (EditText) findViewById(R.id.editTextDetail);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        imagesUri = new ArrayList<>();
        cityList = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar_add_ad);
        spinnerCities = (Spinner) findViewById(R.id.spinner_city);
        imageButtonCamera = (ImageButton) findViewById(R.id.imageButton_camera);
        buttonCancel = (Button) findViewById(R.id.cancel_button);
        buttonSave = (Button) findViewById(R.id.save_button);
        imageButtonAlbum = (ImageButton) findViewById(R.id.imageButton_album);


        adImagesAdapter = new AdImagesAdapter(this, R.layout.ad_photo_item, imagesUri);
        gridViewAdImages.setAdapter(adImagesAdapter);
        adImagesAdapter.setOnDeleteIconClicked(new AdImagesAdapter.OnDeleteIconClicked() {
            @Override
            public void onDeleteIconClicked(Uri uri) {

            }
        });

        gridViewAdImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = imagesUri.get(i);
                AddAdActivity.tempUriIndex = i;
                beginCrop(uri);
            }
        });

        adImagesAdapter.setOnDeleteIconClicked(new AdImagesAdapter.OnDeleteIconClicked() {
            @Override
            public void onDeleteIconClicked(Uri uri) {
                imagesUri.remove(uri);
                adImagesAdapter.notifyDataSetChanged();
            }
        });

        editTextCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    Intent intent = new Intent(AddAdActivity.this, SelectCategoryActivity.class);
                    intent.putExtra("activity", "add");
                    startActivityForResult(intent, SELECT_CATEGORY_REQUEST);
                }
                return false;
            }
        });

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                uriCameraPhoto = getTakePhotoUri();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCameraPhoto);
                startActivityForResult(intent, START_CAMERA);
            }
        });

        imageButtonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        AsyncCityData cityData = new AsyncCityData();
        cityData.execute();
    }

    private void validateData() {
        boolean error = false;
        if (Validation.isEmpty(editTextCategory)) {
            error = true;
        }
        if (Validation.isEmpty(editTextTitle)) {
            error = true;
        }
        if (!error) {

            buildAdData();
        }
    }

    private void buildAdData() {
        Ad ad = new Ad();
        ad.setCategory(this.selectedCategory);
        ad.setCity(this.selectedCity);
        ad.setTitle(editTextTitle.getText().toString());
        ad.setDetail(editTextDetail.getText().toString());
        if (!TextUtils.isEmpty(editTextPrice.getText().toString())) {
            ad.setPrice(Integer.parseInt(editTextPrice.getText().toString()));
        }
        ad.setMobile(editTextPhone.getText().toString());
        Map<String, String> requestData = new HashMap<>();

        requestData.put("category_id", ad.getCategory().getId() + "");
        requestData.put("city_id", ad.getCity().getId() + "");

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //requestData.put("user_api", preferences.getString("api", null));
        requestData.put("title", ad.getTitle());
        requestData.put("detail", ad.getDetail());
        requestData.put("price", ad.getPrice() + "");
        requestData.put("mobile", ad.getMobile());

        AsyncSendData asyncSendData = new AsyncSendData();
        asyncSendData.execute(requestData);
    }

    private void fillCitySpinner() {
        cityAdapter = new CityAdapter(this, R.layout.category_select_item, android.R.id.text1, cityList);
        spinnerCities.setAdapter(cityAdapter);
        spinnerCities.setPrompt(getString(R.string.city));
        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCity = cityList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                //Display an error
                return;
            }

            //  Uri imageUri = data.getData();
            imagesUri.addAll(UploadImage.extractUriFromIntent(data));
            adImagesAdapter.notifyDataSetChanged();
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == SELECT_CATEGORY_REQUEST && resultCode == RESULT_OK) {
            String categoryName = data.getStringExtra("name");
            int categoryId = data.getIntExtra("id", 0);
            editTextCategory.setText(categoryName);

            selectedCategory = new Category();
            selectedCategory.setId(categoryId);
            selectedCategory.setName(categoryName);

        } else if (requestCode == START_CAMERA && resultCode == RESULT_OK) {
            imagesUri.add(uriCameraPhoto);
            adImagesAdapter.notifyDataSetChanged();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uriCameraPhoto);
            sendBroadcast(intent);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imagesUri.set(AddAdActivity.tempUriIndex, Crop.getOutput(result));
            adImagesAdapter.notifyDataSetChanged();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getTakePhotoUri() {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        pathBuilder.append('/');
        pathBuilder.append("Camera");
        pathBuilder.append('/');
        pathBuilder.append("IMG_" + this.imageDateFormat.format(new Date()) + ".jpg");
        Uri uri = Uri.parse("file://" + pathBuilder.toString());
        File file = new File(uri.toString());
        file.getParentFile().mkdirs();
        return uri;
    }

    private void showSuccessMessage() {
        final Snackbar snackbar = Snackbar.make(buttonSave, "تم الإضافة الإعلان", Snackbar.LENGTH_SHORT);
        snackbar.show();
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                startActivity(new Intent(AddAdActivity.this, UserAdActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncCityData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cityList = City.getCityList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            fillCitySpinner();
        }
    }

    private class AsyncSendData extends AsyncTask<Map, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Map... data) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddAdActivity.this);
            String api = preferences.getString("api", null);
            String adId = HttpManger.sendPostRequest(Config.ADD_AD, data[0], api);
            for (Uri uri : imagesUri) {
                if (uri.getScheme().equals("file")) {
                    HttpManger.uploadImage(uri.getPath(), api, adId);
                } else {
                    HttpManger.uploadImage(UploadImage.getRealPathFromURI(AddAdActivity.this, uri), api, adId);
                }
            }
            return adId;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            showSuccessMessage();
        }
    }
}
