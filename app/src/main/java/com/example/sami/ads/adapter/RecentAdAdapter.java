package com.example.sami.ads.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sami.ads.Config;
import com.example.sami.ads.R;
import com.example.sami.ads.entities.Ad;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

public class RecentAdAdapter extends RecyclerView.Adapter<RecentAdAdapter.ViewHolder> implements View.OnClickListener {

    // Define listener member variable
    private OnItemClickListener listener;
    private List<Ad> recentAds;

    public RecentAdAdapter(List<Ad> recentAds) {
        this.recentAds = recentAds;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_ad_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ad ad = recentAds.get(position);
        holder.textViewTitle.setText(ad.getTitle());
        holder.textViewPrice.setText(ad.getPrice() + "");
        if (ad.imageUrl != null) {
            ImageLoader.getInstance().displayImage(Config.IMAGE_PATH + ad.imageUrl, holder.imageViewAdPhoto, new AnimateFirstDisplayListener());
        }
    }

    @Override
    public int getItemCount() {
        return recentAds.size();
    }

    @Override
    public void onClick(View view) {

    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewPrice;
        public ImageView imageViewAdPhoto;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.textView_title);
            textViewPrice = (TextView) view.findViewById(R.id.textView_price);
            imageViewAdPhoto = (ImageView) view.findViewById(R.id.imageView_ad);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}
