package com.example.sami.ads;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sami.ads.entities.City;

import java.util.ArrayList;
import java.util.List;


public class SelectCityActivity extends AppCompatActivity {

    ArrayAdapter<City> adapter;
    ListView listView;
    List<City> filterCites;
    private List<City> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        prepareElement();
    }

    private void prepareElement() {
        cities = new ArrayList<>();
        City allCity = new City();
        allCity.setId(0);
        allCity.setName(getString(R.string.all_city));
        cities.add(allCity);
        cities.addAll(City.getCityList());

        filterCites = new ArrayList<>();
        filterCites.addAll(cities);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, filterCites) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(filterCites.get(position).getName());
                return view;
            }

        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City city = cities.get(i);
                Intent intent = new Intent();
                intent.putExtra("id", city.getId());
                intent.putExtra("name", city.getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override

            public boolean onQueryTextSubmit(String query) {

                // perform query here


                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used

                // see https://code.google.com/p/android/issues/detail?id=24599

                searchView.clearFocus();
                SelectCityActivity.this.filter(query);
                return false;
            }

            @Override

            public boolean onQueryTextChange(String newText) {

                SelectCityActivity.this.filter(newText);
                return false;
            }

        });

        searchItem.expandActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        return super.onCreateOptionsMenu(menu);

    }

    public void filter(String filterText) {

        filterText = filterText.toLowerCase();
        filterCites.clear();
        if (filterText.length() == 0) {
            filterCites.addAll(cities);

        } else {
            for (City city : cities) {
                if (filterText.length() != 0 && city.getName().toLowerCase().contains(filterText)) {
                    filterCites.add(city);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
