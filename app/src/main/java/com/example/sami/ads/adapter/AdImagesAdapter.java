package com.example.sami.ads.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.sami.ads.R;
import com.example.sami.ads.helper.UploadImage;

import java.io.IOException;
import java.util.List;

public class AdImagesAdapter extends ArrayAdapter<Uri> {

    Context context;
    List<Uri> bitmaps;
    private OnDeleteIconClicked onDeleteIconClicked;

    public AdImagesAdapter(Context context, int resource, List<Uri> objects) {
        super(context, resource, objects);
        this.context = context;
        this.bitmaps = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Uri uri = bitmaps.get(position);
        Bitmap myBitmap = null;
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = inflater.inflate(R.layout.ad_photo_item, parent, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewAd);
        ImageView imageDelete = (ImageView) view.findViewById(R.id.imageview_delete);
        if (uri.getScheme().equals("file")) {
            imageView.setImageBitmap(UploadImage.getScaledBitmap(uri, 800, 800));
        } else {
            imageView.setImageBitmap(UploadImage.getScaledBitmap(context, uri, 800, 800));
        }
        //imageView.setImageURI(bitmaps.get(position));

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdImagesAdapter.this.onDeleteIconClicked.onDeleteIconClicked(uri);
            }
        });
        return view;
    }

    public void setOnDeleteIconClicked(OnDeleteIconClicked listener) {
        this.onDeleteIconClicked = listener;
    }

    public interface OnDeleteIconClicked {
        void onDeleteIconClicked(Uri uri);
    }
}
