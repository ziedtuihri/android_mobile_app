package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class CustomListAdapterListProduct_course_mode extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<productPointOfSale> products ;

    public CustomListAdapterListProduct_course_mode(AppCompatActivity activity, List<productPointOfSale> products) {
        this.activity = activity;
        this.products = products;
    }
    public void remove(int position) {
        products.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int location) {
        return products.get(location);
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

        productPointOfSale product = products.get(position);

        title.setText(product.getNameP());
        genre.setText(product.getUnitP());

        year.setText(String.valueOf (Float.parseFloat(product.getPriceP()) / 1000));

        rating.setText(product.getQuantityP());


        return convertView;
    }

}