package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class circuitActivity extends AppCompatActivity {
    private GridView gridView ;
    private CustomlistAdapterAnalyse customlistAdapterAnalyse ;
    private List<Circuit> listCircuit ;
    private ImageView imageViewBack ;
    private Button basket;
    private String idList, trafic, mapLocation , nameList;
    private JSONArray POS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit);

        // hide App Title Bar
        getSupportActionBar().hide();

        // get Attribute from another activity
        Intent intent = getIntent() ;
        ArrayList<? extends ProductsOfPos> arr  = (ArrayList<? extends ProductsOfPos>) intent.getSerializableExtra("arrayProductPos");

        idList = intent.getStringExtra("idList") ;
        trafic = intent.getStringExtra("trafic");
        nameList = intent.getStringExtra("nameList") ;
        mapLocation = intent.getStringExtra("mapLocation");
        try {
            POS = new JSONArray(intent.getStringExtra("POS"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ImageView
        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        gridView = findViewById(R.id.listCircuit) ;

        // Button
        basket = findViewById(R.id.basket);

        // setOnClickListener
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), basketAnalyseActivity.class) ;

                intent.putExtra("trafic", trafic);
                intent.putExtra("idList", idList);
                intent.putExtra("mapLocation", mapLocation);
                intent.putExtra("POS", String.valueOf(POS));
                startActivity(intent);
            }
        });

        customlistAdapterAnalyse = new CustomlistAdapterAnalyse(this, (List<ProductsOfPos>) arr) ;
        gridView.setAdapter(customlistAdapterAnalyse);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductsOfPos productsOfPos =(ProductsOfPos) parent.getItemAtPosition(position) ;
                String idPos = productsOfPos.getIdPos() ;
                String namePos = productsOfPos.getNamePos() ;
                float totalPrice = productsOfPos.getTotalprice() ;
                Intent intent = new Intent(getApplicationContext() , productsOfCircuitActivity.class) ;
                intent.putExtra("idList",idList)  ;
                intent.putExtra("totalPrice", totalPrice) ;
                intent.putExtra("namePos",namePos) ;
                intent.putExtra("nameList",nameList) ;
                intent.putExtra("idPos" , idPos) ;
                startActivity(intent);
            }
        });

    }



}