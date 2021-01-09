package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CustomListofListAsapter  extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private ArrayList<listt> li;

    public CustomListofListAsapter(AppCompatActivity activity, ArrayList<listt> li) {
        this.activity = activity;
        this.li = li;
    }

    @Override
    public int getCount() {
        return li.size();
    }

    @Override
    public Object getItem(int location) {
        return li.get(location);
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
            convertView = inflater.inflate(R.layout.list_of_lists, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        ImageView img =(ImageView)  convertView.findViewById(R.id.img);

        // getting movie data for the row
        listt m = li.get(position);
        img.setImageResource(R.drawable.list);

        // title
        title.setText(m.getName());
        if(m.getName()=="ADD NEW LIST"){
            img.setImageResource(R.drawable.ic_add_black_24dp);
            title.setTextSize(13);

        }


        return convertView;
    }

}