package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CutomAdapterAnalysePos extends BaseAdapter {


    Activity activity;
    List<pos> Pos;
    LayoutInflater inflater;

    public CutomAdapterAnalysePos(Activity activity) {
        this.activity = activity;
    }

    public CutomAdapterAnalysePos(Activity activity, List<pos> users) {
        this.activity   = activity;
        this.Pos      = users;
        inflater  = activity.getLayoutInflater();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.analyse_listpos, null);

        TextView name = (TextView)view.findViewById(R.id.nameP);
        TextView adress = (TextView)view.findViewById(R.id.adressP);
        CheckBox checkBox = view.findViewById(R.id.iv_check_box) ;

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(Pos.get(i).isSelected)
                    {
                        Pos.get(i).setSelected(false);
                    } else
                        Pos.get(i).setSelected(true);
                } catch (Exception e) {}

            }
        });

        try {
            if (Pos.get(i).isSelected) {

                checkBox.setChecked(true);
            }
            else {
                checkBox.setChecked(false);
            }
        } catch (Exception e) {}


        pos model = Pos.get(i);

        name.setText(model.getNameP());
         adress.setText(model.getAdressP());

        return view;

    }

    public List<pos> getListPos()
    {
        return Pos ;
    }

}
