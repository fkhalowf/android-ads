package com.example.sami.ads.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by sami on 2/8/2017.
 */
public class Auth {

    private static Boolean isLogin = null;
    private static String api = null;

    public static boolean isLogin(Context context) {
        if (isLogin == null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getString("api", null) == null) {
                Auth.isLogin = false;
            } else {
                Auth.isLogin = true;
            }
        }
        return isLogin;
    }

    public static void logOut(Context context) {
        Auth.isLogin = false;
        Auth.api = null;
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove("api");
        editor.apply();
    }

    public static void login(Context context, String api) {
        Auth.isLogin = true;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("api", api).apply();
        Auth.api = api;
    }

    public static String getApi(Context context) {
        if (Auth.api == null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Auth.api = preferences.getString("api", null);
        }
        return Auth.api;
    }
}
