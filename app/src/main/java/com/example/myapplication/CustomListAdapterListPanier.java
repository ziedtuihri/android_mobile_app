package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CustomListAdapterListPanier  extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<productPointOfSale> movieItems ;

    public CustomListAdapterListPanier(AppCompatActivity activity, List<productPointOfSale> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }
    public void remove(int position) {
        movieItems.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_product_mode_course, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.quantityM);

        // getting movie data for the row
        productPointOfSale m = movieItems.get(position);

        // thumbnail image

        // title
        title.setText(m.getNameP());
        // thumbnail image
        genre.setText(m.getUnitP());
        // title
        // title.setText(m.get_productname());

        year.setText(m.getPriceP());
        // rating
        // rating.setText("Price: " + String.valueOf(m.getPrix()*m.getQuantite()));

        rating.setText(m.getQuantityP());
        // rating
        // rating.setText("Price: " + String.valueOf(m.getPrix()*m.getQuantite()));


        // release year
        // year.setText(String.valueOf(String.valueOf( m.getQuantite())+" "+m.getUnite()));
        // genre.setText("");

        return convertView;
    }

}