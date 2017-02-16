package com.example.sami.ads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.sami.ads.helper.Auth;
import com.example.sami.ads.http.HttpManger;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonSignIn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialView();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initialView() {
        editTextUsername = (EditText) findViewById(R.id.email_sign);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignIn = (Button) findViewById(R.id.email_sign_in_button);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<>();
                params.put("email", editTextUsername.getText().toString());
                params.put("password", editTextPassword.getText().toString());
                AsyncSignIn asyncSignIn = new AsyncSignIn();
                asyncSignIn.execute(params);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.sign_in_progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void showResultMessage(String result) {
        Snackbar snackbar;
        if (result.equals("error")) {
            snackbar = Snackbar.make(buttonSignIn, R.string.error_password, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Auth.login(this, result);
            //getPreferences().edit().putString("api", result).apply();
            snackbar = Snackbar.make(buttonSignIn, R.string.logined, Snackbar.LENGTH_SHORT);
            snackbar.show();
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    //Intent intent = new Intent(SigninActivity.this, CategoryActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);
                    finish();
                }
            });

        }
    }

    protected SharedPreferences getPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
    }

    private class AsyncSignIn extends AsyncTask<Map, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Map... parmas) {
            String result = HttpManger.sendPostRequest(Config.SIGN_IN, parmas[0], "");
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            showResultMessage(result);
        }
    }
}
