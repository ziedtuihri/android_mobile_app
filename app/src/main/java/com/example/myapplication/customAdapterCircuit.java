package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class customAdapterCircuit  extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<Product> listProducts ;

    public customAdapterCircuit(AppCompatActivity activity, List<Product> listProducts) {
        this.activity = activity;
        this.listProducts = listProducts;
    }
    public void remove(int position) {
        listProducts.remove(position);

        Product m = listProducts.get(position);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listProducts.size();
    }

    @Override
    public Object getItem(int location) {
        return listProducts.get(location);
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
            convertView = inflater.inflate(R.layout.listproduct, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView mark = (TextView) convertView.findViewById(R.id.mark);
        TextView model = (TextView) convertView.findViewById(R.id.model);
        TextView quantityUnit = (TextView) convertView.findViewById(R.id.quantityUnit);
        TextView category = (TextView) convertView.findViewById(R.id.category);

        Product m = listProducts.get(position);

        // name Product in popup add_product_to_list.xml
        title.setText(m.getproductName());

        // get mark in popup add_product_to_list.xml
        mark.setText(m.getMark()) ;

        // set the category in popup add_product_to_list.xml
        category.setText(m.getCategory()) ;

        // get model selected in popup add_product_to_list.xml
        model.setText(m.getModel()) ;

        // get quantity and unit in popup add_product_to_list.xml
        quantityUnit.setText(m.getQuantity() + " " + m.getUnit());


        return convertView;
    }
}
