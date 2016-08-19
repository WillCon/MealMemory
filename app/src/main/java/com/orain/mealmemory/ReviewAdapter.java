package com.orain.mealmemory;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by William on 1/1/2016.
 */
public class ReviewAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private int resource;
    private ArrayList<Review> data;
    private static SharedPreferences sharedPref;

    public ReviewAdapter(Context context, int resource, ArrayList<Review> data){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.data = data;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getCount(){
        return this.data.size();
    }

    public long getItemId(int position){
        return position;
    }

    public Review getItem(int position){
        return data.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view;

        if (convertView == null) {
            view = this.inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        // bind the data to the view object
        return this.bindData(view, position);
    }

    public View bindData(View view, int position) {
        // make sure it's worth drawing the view
        if (this.data.get(position) == null) {
            return view;
        }

        // pull out the object
        Review item = this.data.get(position);

        // extract the view object
        View viewElement = view.findViewById(R.id.title);
        // cast to the correct type
        TextView tv = (TextView)viewElement;
        // set the value
        tv.setText(item.getMealName());

        int rating = item.getRating();

        final boolean color = sharedPref.getBoolean("color_switch", true);

        if(color) {
            viewElement = view.findViewById(R.id.rating);
            tv = (TextView) viewElement;
            tv.setText("");
            ImageView img = (ImageView) view.findViewById(R.id.ratingImg);
            if (rating >= 8) {
                img.setImageResource(R.mipmap.ic_green);
            } else if (rating >= 4) {
                img.setImageResource(R.mipmap.ic_orange);
            } else {
                img.setImageResource(R.mipmap.ic_red);
            }
        }
        else {
            ImageView img = (ImageView) view.findViewById(R.id.ratingImg);
            img.setImageResource(android.R.color.transparent);
            viewElement = view.findViewById(R.id.rating);
            tv = (TextView) viewElement;
            tv.setText(Integer.toString(rating));
        }

        // return the final view object
        return view;
    }
}
