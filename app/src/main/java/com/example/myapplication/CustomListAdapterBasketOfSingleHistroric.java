package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CustomListAdapterBasketOfSingleHistroric extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<Basket> baskets;

    public CustomListAdapterBasketOfSingleHistroric(AppCompatActivity activity, List<Basket> baskets) {
        this.activity = activity;
        this.baskets = baskets;
    }

    @Override
    public int getCount() {
        return baskets.size();
    }

    @Override
    public Object getItem(int location) {
        return baskets.get(location);
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
            convertView = inflater.inflate(R.layout.list_items_baskets, null);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView idPanier = convertView.findViewById(R.id.idHistoricList);
        TextView pos = convertView.findViewById(R.id.pos) ;
        // getting historiques data for the row

        Basket basket = baskets.get(position);
        idPanier.setText(basket.getIdPanier());
        date.setText(basket.getDateAchat());
        pos.setText(basket.getPos());

        return convertView;

    }
}
