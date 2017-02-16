package com.example.sami.ads.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sami.ads.AdsActivity;
import com.example.sami.ads.Config;
import com.example.sami.ads.R;
import com.example.sami.ads.UserAdActivity;
import com.example.sami.ads.entities.Ad;
import com.example.sami.ads.helper.Auth;
import com.example.sami.ads.helper.Utility;
import com.example.sami.ads.http.HttpManger;
import com.example.sami.ads.uinterface.OnActivityGetAds;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sami on 2/3/2017.
 */
public class AdAdapter extends ArrayAdapter<Ad> {

    private final Context context;
    private final int resource;
    private final List<Ad> objects;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public AdAdapter(Context activity, int resource, List<Ad> objects) {
        super(activity, resource, objects);
        this.context = activity;
        this.resource = resource;
        this.objects = objects;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Ad ad = objects.get(position);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.ad_item, parent, false);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_title);
        TextView textViewCategory = (TextView) view.findViewById(R.id.textViewCategory);
        TextView textViewCity = (TextView) view.findViewById(R.id.textView_city);
        TextView textViewUser = (TextView) view.findViewById(R.id.textView_user);
        TextView textViewPrice = (TextView) view.findViewById(R.id.textview_price);
        TextView textViewDate = (TextView) view.findViewById(R.id.textview_date);
        TextView textViewDeactive = (TextView) view.findViewById(R.id.textView_deactive);
        TextView textViewIsDeactive = (TextView) view.findViewById(R.id.textView_is_deactive);
        TextView textViewUserLabel = (TextView) view.findViewById(R.id.textView_user_label);

        if (context instanceof UserAdActivity) {
            if (ad.isActive()) {
                textViewDeactive.setVisibility(View.VISIBLE);
            } else {
                textViewIsDeactive.setVisibility(View.VISIBLE);
            }
            textViewDeactive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((UserAdActivity) context).deactiveAd(position);
                }
            });

            textViewUser.setVisibility(View.GONE);
            textViewUserLabel.setVisibility(View.GONE);
        }

        ImageView imageViewPrimary = (ImageView) view.findViewById(R.id.imageView_ad);

        if (ad.imageUrl != null) {
            ImageLoader.getInstance().displayImage(Config.IMAGE_PATH + ad.imageUrl, imageViewPrimary, options, animateFirstListener);
        }

        /*
        if (ad.getPrimaryBitmap() != null) {
            ImageView imageViewPrimary = (ImageView) view.findViewById(R.id.imageView_ad);
            imageViewPrimary.setImageBitmap(ad.primaryBitmap);
        } else {
            if (ad.imageUrl != null) {
                    AdView container = new AdView();
                container.ad = ad;
                container.view = view;
                ImageLoaderSync imageLoader = new ImageLoaderSync();
                imageLoader.execute(container);
            }
        }
        */


        textViewTitle.setText(ad.getTitle());
        textViewCategory.setText(ad.getCategory().getName());
        textViewCity.setText(ad.getCity().getName());
        textViewUser.setText(ad.getUser().getUsername());
        textViewPrice.setText(ad.getPrice() + "");
        textViewDate.setText(Utility.getTimeAgo(ad.getDate(), context));

        if (position >= Config.LIMIT_ROW_COUNT - 1)
            if (reachedEndOfList(position)) {
                OnActivityGetAds onActivityGetAds = (OnActivityGetAds) context;
                onActivityGetAds.addMoreItem();
            }

        return view;
    }

    private void ShowElement() {

    }

    private boolean reachedEndOfList(int position) {
        // can check if close or exactly at the end
        return position == objects.size() - 1;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
