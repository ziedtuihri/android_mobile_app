package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CustomlistAdapterAnalyse extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<ProductsOfPos> productsOfPos;

    public CustomlistAdapterAnalyse(AppCompatActivity activity, List<ProductsOfPos> productsOfPos) {
        this.activity = activity;
        this.productsOfPos = productsOfPos;
    }

    @Override
    public int getCount() {
        return productsOfPos.size();
    }

    @Override
    public Object getItem(int location) {
        return productsOfPos.get(location);
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
            convertView = inflater.inflate(R.layout.listitem_circuit, null);

        TextView namePos = (TextView) convertView.findViewById(R.id.namePos);
        TextView numProducts = convertView.findViewById(R.id.numProducts);
        TextView totalPrice = convertView.findViewById(R.id.total_price) ;
        TextView idPos = convertView.findViewById(R.id.idPos) ;
        // getting historiques data for the row

        ProductsOfPos p = productsOfPos.get(position);
        namePos.setText(p.getNamePos());
        numProducts.setText("- " +p.getNumProducts() + " Products");
        totalPrice.setText("- " +String.valueOf(p.getTotalprice()) + " DT");
        idPos.setText(p.getIdPos());
        return convertView;

    }
}
