package com.example.myapplication;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



// this activity to add point of sale

public class addPosActivity extends AppCompatActivity {
    private myDbHandlerPOS myDbHandlerPOS ; // database
    private pointOfSale pointOfSale ;
    private EditText designation , adress , email , phone ;
    private Button add , location ;
    private String DesText , adrText , emText , phText ;
    private double currentLatitude , currentLongitude ;
    private static final int ADDRESS_PICKER_REQUEST = 1020;
    private LinearLayout linearLayoutInputs ;
    private ImageView imageViewBack ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide App Title Bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_pos);

        linearLayoutInputs = findViewById(R.id.LinearLayoutInputs) ;

        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        myDbHandlerPOS = new myDbHandlerPOS(getApplicationContext());
        // fields
        designation = findViewById(R.id.pos_designation) ;
        adress = findViewById(R.id.pos_adress) ;
        email = findViewById(R.id.pos_email) ;
        phone = findViewById(R.id.pos_phone) ;
        location = findViewById(R.id.btnGetLocation) ;
        add = findViewById(R.id.btn_addPOS) ; // Button


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtility.apiKey = getResources().getString(R.string.api_key);
                Intent i = new Intent(addPosActivity.this, LocationPickerActivity.class);
                startActivityForResult(i, ADDRESS_PICKER_REQUEST);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields())
                {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(addPosActivity.this, Locale.getDefault());

                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    try {
                        addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                        // If any additional address line present than only,
                        // check with max available address lines by getMaxAddressLineIndex()

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String postalCode = addresses.get(0).getPostalCode();
                        pointOfSale = new pointOfSale(DesText,adrText,address,emText,phText,state,postalCode,city) ;
                        myDbHandlerPOS.addPointOfSale(pointOfSale);
                        List<pointOfSale> list = myDbHandlerPOS.getPointOfSales() ;
                        String res = " " ;
                        for (pointOfSale pointOfSale : list) {
                            res += pointOfSale.toString() + "\n" ;
                        }
                        Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        });
    }

    public Boolean checkFields()
    {
        DesText = designation.getText().toString() ;

        if (DesText.isEmpty())
        {
            designation.setError("designation is required");
            designation.requestFocus() ;
            return false ;
        }

        adrText = adress.getText().toString() ;
        if (adrText.isEmpty())
        {
            adress.setError("adress is required");
            adress.requestFocus();
            return false ;
        }

        emText = email.getText().toString() ;
        if (emText.isEmpty())
        {
            email.setError("email is required");
            email.requestFocus() ;
            return false  ;
        }

        phText = phone.getText().toString() ;
        if (phText.isEmpty())
        {
            phone.setError("phone is required");
            phone.requestFocus() ;
            return false ;
        }
        if (phText.length() != 8)
        {
            phone.setError("phone must be 8 ");
            phone.requestFocus() ;
            return false ;
        }

        return true ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    linearLayoutInputs.setVisibility(View.VISIBLE);
                    adress.setText(currentLatitude + " " + currentLongitude);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
