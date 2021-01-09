package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class historicAnalyseActivity extends AppCompatActivity {
    private GridView gridView ;
    private CustomlistAdapterAnalyse customlistAdapterAnalyse ;
    private List<Circuit> listCircuit ;
    private ImageView imageViewBack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_analyse);

        // hide App Title Bar
        getSupportActionBar().hide();

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


    }
}