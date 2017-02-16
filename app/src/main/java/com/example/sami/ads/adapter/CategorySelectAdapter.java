package com.example.sami.ads.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sami.ads.R;
import com.example.sami.ads.entities.Category;

import java.util.List;

public class CategorySelectAdapter extends ArrayAdapter<Category> {

    private final Context context;
    private final int resource;
    private final List<Category> objects;

    public CategorySelectAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = objects.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_select_item, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.textView_category_select);
        textView.setText(category.getName());
        return view;
    }
}
