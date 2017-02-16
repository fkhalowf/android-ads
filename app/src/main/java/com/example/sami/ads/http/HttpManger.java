package com.example.sami.ads.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sami.ads.Config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sami on 1/22/2017.
 */
public class HttpManger {

    public static String getString(String url, Map<String, String> params, String api) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = null;
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (String key : params.keySet()) {
                httpBuilder.addQueryParameter(key, params.get(key));
            }
        }

        url = httpBuilder.build().toString();
        builder = new Request.Builder().url(url);
        if (api != null) {
            builder.header("Authorization", api);
        }

        Request request = builder.build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendRegisterRequest(String username, String email, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(Config.REGISTER_URL)
                .post(formBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendPostRequest(String url, Map<String, String> params, String api) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .header("Authorization", api)
                .url(url)
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uploadImage(String sourceImageFile, String api, String adID) {

        try {
            File sourceFile = new File(sourceImageFile);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/") + 1);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("ad_image", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("api", api)
                    .addFormDataPart("ad_id", adID)
                    .build();
            Request request = new Request.Builder()
                    .header("Authorization", api)
                    .url(Config.IMAGE_UPLOAD)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.e("TAG", "Error: " + res);
            return res;

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }


    public static Bitmap getImage(String path) {
        try {
            Thread.sleep(1);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(Config.IMAGE_PATH + path);
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
