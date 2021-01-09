package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class CustomPointOfSaleAdapter extends BaseAdapter {

    Activity activity;
    List<pos> Pos;
    LayoutInflater inflater;

    //short to create constructer using command+n for mac & Alt+Insert for window


    public CustomPointOfSaleAdapter(Activity activity) {
        this.activity = activity;
    }

    public CustomPointOfSaleAdapter(Activity activity, List<pos> users) {
        this.activity   = activity;
        this.Pos      = users;

        inflater        = activity.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return Pos.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null){

            view = inflater.inflate(R.layout.list_view_item, viewGroup, false);

            holder = new ViewHolder();

            holder.name = (TextView)view.findViewById(R.id.nameP);
            holder.adress = (TextView)view.findViewById(R.id.adressP);
            holder.ivCheckBox = (ImageView) view.findViewById(R.id.iv_check_box);

            view.setTag(holder);
        }else
            holder = (ViewHolder)view.getTag();

        pos model = Pos.get(i);

        holder.name.setText(model.getNameP());
        holder.adress.setText(model.getAdressP());

        if (model.isSelected())
            holder.ivCheckBox.setBackgroundResource(R.drawable.checked);

        else
            holder.ivCheckBox.setBackgroundResource(R.drawable.check);

        return view;

    }

    public void updateRecords(List<pos>  users){
        this.Pos = users;

        notifyDataSetChanged();
    }


    class ViewHolder{

        TextView name, adress;
        ImageView ivCheckBox;
    }
}