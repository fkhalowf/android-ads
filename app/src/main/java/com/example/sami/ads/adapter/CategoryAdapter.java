package com.example.sami.ads.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sami.ads.Config;
import com.example.sami.ads.R;
import com.example.sami.ads.entities.Category;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;


import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sami on 1/21/2017.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.categoryList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = categoryList.get(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.category_item, parent, false);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(category.getName());
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        String categoryUrl = Config.CATEGORY_IMAGE_PATH + category.getId() + ".png";


        if (category.getImage() != null) {
            imageView.setImageBitmap(category.getImage());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(categoryUrl, imageView,
                    new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.loading)
                            .showImageForEmptyUri(R.drawable.ic_empty)
                            .showImageOnFail(R.drawable.ic_error)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .displayer(new SimpleBitmapDisplayer())
                            .build());
            /*
            CategoryView categoryView = new CategoryView();
            categoryView.category = category;
            categoryView.view = view;

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.execute(categoryView);*/
        }


        return view;
    }

    private Bitmap getImage(String path) {
        try {
            Thread.sleep(1);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(path);
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

    class CategoryView {
        public Category category;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<CategoryView, Void, CategoryView> {

        @Override
        protected CategoryView doInBackground(CategoryView... categoryViews) {
            CategoryView container = categoryViews[0];
            Category category = container.category;
            Bitmap image = getImage(Config.CATEGORY_IMAGE_PATH + category.getId() + ".png");
            container.bitmap = image;
            return container;
        }

        @Override
        protected void onPostExecute(CategoryView result) {
            ImageView imageView = (ImageView) result.view.findViewById(R.id.imageView);
            imageView.setImageBitmap(result.bitmap);
            result.category.setImage(result.bitmap);
        }
    }
}
