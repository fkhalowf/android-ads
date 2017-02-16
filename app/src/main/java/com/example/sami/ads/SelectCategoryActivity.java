package com.example.sami.ads;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sami.ads.adapter.CategoryAdapter;
import com.example.sami.ads.adapter.CategorySelectAdapter;
import com.example.sami.ads.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoryActivity extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_category);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listView = (ListView) findViewById(R.id.listViewCategorySelect);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_select_category);
        AsyncTask<Void, Void, Void> execute = new CategoriesData().execute();

        categoryList = new ArrayList<>();
        String lastActivity = getIntent().getStringExtra("activity");
        if (lastActivity.equals("filter")) {
            Category category = new Category();
            category.setId(0);
            category.setName(getString(R.string.all_categories));
            categoryList.add(category);
        }
    }

    private void viewCategories() {
        CategorySelectAdapter categorySelectAdapter = new CategorySelectAdapter(this, R.layout.category_select_item, categoryList);
        listView.setAdapter(categorySelectAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category = categoryList.get(i);
                Intent intent = new Intent();
                intent.putExtra("name", category.getName());
                intent.putExtra("id", category.getId());
                setResult(RESULT_OK, intent);
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

    private class CategoriesData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... strings) {

            try {
                categoryList.addAll(Category.categoryList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            progressBar.setVisibility(View.INVISIBLE);
            viewCategories();
        }
    }
}
