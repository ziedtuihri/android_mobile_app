package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class analyseActivity extends AppCompatActivity implements LocationListener {
    private RadioGroup radioGroup;
    private EditText editTextLocation;
    private Button btnLocation, btnAnalyse, btnHistoricalAnalyse ;
    private ImageView imageViewBack ;
    private Switch switchLocation, switchPointOfSale;
    private LinearLayout linearLayoutLocation;
    private String  trafic = "1", idList, mapLocation = "36.8060872,10.1797706" , nameList;
    private ArrayList<pos> arrayPos ;
    private List<String> listPos ;
    private ListView listViewPointOfSale , listViewAnalysePos ;
    private Dialog myDialogPointOfSale ;
    private ArrayAdapter<String> adapter ;
    static JSONArray POS ;
    private static final int ADDRESS_PICKER_REQUEST = 1020;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        // hide App Title Bar
        getSupportActionBar().hide();

        // listView
        listViewAnalysePos = findViewById(R.id.listViewPos) ;

        btnHistoricalAnalyse = findViewById(R.id.historicAnalyse) ;
        btnHistoricalAnalyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), historicAnalyseActivity.class) ;
                startActivity(intent);
            }
        });

        Intent intent = getIntent() ;
        idList = intent.getStringExtra("idList") ;
        nameList = intent.getStringExtra("nameList") ;
        listPos = new ArrayList<>() ;
        POS = new JSONArray() ;

        // radioGroup
        radioGroup = findViewById(R.id.radioGroup);

        // Switch
        switchLocation = findViewById(R.id.switchLocation);
        switchPointOfSale = findViewById(R.id.switchPointOfSales);

        // ImageView
        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        // button
        btnLocation = findViewById(R.id.btnLocation);
        btnAnalyse = findViewById(R.id.btnAnalyse) ;

        btnAnalyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyseListMs() ;
                //Intent intent = new Intent(getApplicationContext(),analyseActivity.class) ;
                //startActivity(intent);
            }
        });

        // EditText
        editTextLocation = findViewById(R.id.EditTextLocation);

        // LinearLayout
        linearLayoutLocation = findViewById(R.id.linearLayoutLocation);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonCar:
                        trafic = "5";
                        break;
                    case R.id.radioButtonMan:
                        trafic = "1";
                        break;
                    case R.id.radioButtonMoto:
                        trafic = "3";
                        break;
                }
            }
        });

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    linearLayoutLocation.setVisibility(View.VISIBLE);
                else {
                    linearLayoutLocation.setVisibility(View.GONE);
                    editTextLocation.setText("");
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapUtility.apiKey = getResources().getString(R.string.api_key);
                Intent i = new Intent(getApplicationContext(), LocationPickerActivity.class);
                startActivityForResult(i, ADDRESS_PICKER_REQUEST);

                listViewAnalysePos.setVisibility(View.GONE);
                switchPointOfSale.setChecked(false);
            }
        });

        switchPointOfSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    getPointsOfSale() ;
                } else
                {
                    listViewAnalysePos.setVisibility(View.GONE);
                    mapLocation = "36.8060872,10.1797706" ;
                }



            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    editTextLocation.setText(String.valueOf(currentLatitude) +"," + String.valueOf(currentLongitude));
                    mapLocation = String.valueOf(currentLatitude) +"," + String.valueOf(currentLongitude);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    public void getPointsOfSale(){

        // location to get latitude and longtitude
        // example for latitude and longtitude "36.8060872,10.1797706"
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("localisation", mapLocation);
            jsonBodyObj.put("Trafic", trafic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.pointOfSale + "/pointOfSale/location",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("isFound")){
                                Toast.makeText(getApplication(), "No point of sale found!", Toast.LENGTH_LONG).show();
                            }else {
                                arrayPos = new ArrayList<>() ;
                                JSONArray jsonAry = jsonObject.getJSONArray("result") ;
                                for (int i = 0; i < jsonAry.length(); i++) {
                                    JSONObject jsonObj = jsonAry.getJSONObject(i);

                                    arrayPos.add(new pos(false, jsonObj.getString("designation"), jsonObj.getString("address"), jsonObj.getString("idPointOfSale")));
                                }

                                popupPointOfSale();
                            }
                        } catch(JSONException e){
                            System.out.println(e);

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("localisation", mapLocation);
                params.put("Trafic", trafic ) ;
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "ChEdLiZiEdKhOuLoUdYaSsInEsAiF");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(Stringrequest);

    }

    public void popupPointOfSale(){

        TextView txtclose, textViewzeroPOS;
        Button cancel_btn, Ok;
        myDialogPointOfSale = new Dialog(this);

        myDialogPointOfSale.setContentView(R.layout.popup_check_point_of_sale);

        cancel_btn = myDialogPointOfSale.findViewById(R.id.cancel_btn);
        txtclose =(TextView) myDialogPointOfSale.findViewById(R.id.close);
        Ok = myDialogPointOfSale.findViewById(R.id.addPos);
        textViewzeroPOS = myDialogPointOfSale.findViewById(R.id.textViewzeroPOS);


        listViewPointOfSale = (ListView) myDialogPointOfSale.findViewById(R.id.simpleListView);

        if(arrayPos.size() == 0){
            textViewzeroPOS.setText("you don't have Point Of Sale");
        }

        final CutomAdapterAnalysePos adapterPoint = new CutomAdapterAnalysePos(this, arrayPos);
        listViewPointOfSale.setAdapter(adapterPoint);

        myDialogPointOfSale.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialogPointOfSale.show();
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogPointOfSale.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialogPointOfSale.dismiss();
            }
        });

        Ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                List<pos> resPos = adapterPoint.getListPos() ;

                // final List of pos
                resPos.removeIf(new Predicate<pos>() {
                    @Override
                    public boolean test(pos pos) {
                        return (!pos.isSelected) ;

                    }
                }) ;

                listPos.clear();
                for (pos p :resPos) {
                    JSONObject jsonObject = new JSONObject() ;
                    try {
                        listPos.add(p.getNameP()) ;
                        jsonObject.put("idPointOfSale", p.getIdP()) ;
                        jsonObject.put("namePos", p.getNameP()) ;
                        POS.put(jsonObject) ;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                myDialogPointOfSale.dismiss();
                updateListViewPos() ;

            }
        });
    }


    public void analyseListMs()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try {

            jsonBodyObj.put("localisation", "36.809759, 10.137251");
            jsonBodyObj.put("Trafic", trafic );
            jsonBodyObj.put("idList" , idList) ;
            jsonBodyObj.put("POS",POS) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/analyseList",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            HashMap<String,ProductsOfPos> res = new HashMap<>() ;
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("circuit") ;


                            for (int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject jsonObj = jsonArray.getJSONObject(i) ;
                                String idPos = jsonObj.getString("idPos") ;
                                int price = jsonObj.getInt("price") ;
                                if (res.containsKey(idPos))
                                {
                                    ProductsOfPos productsOfPos = res.get(idPos) ;
                                    productsOfPos.incrementPrduct();
                                    productsOfPos.addPrice(price);
                                    res.put(idPos,productsOfPos) ;
                                } else
                                {
                                    String namePos = jsonObj.getString("POS") ;
                                    ProductsOfPos productsOfPos = new ProductsOfPos(idPos,namePos,price,1) ;
                                    res.put(idPos,productsOfPos) ;
                                }

                            }


                            ArrayList<ProductsOfPos> arrProductsOfPos = new ArrayList<>() ;
                            for (String idPos : res.keySet())
                            {
                                ProductsOfPos productsOfPos = res.get(idPos) ;
                                arrProductsOfPos.add(productsOfPos) ;
                            }

                            Intent intent = new Intent(getApplicationContext(), circuitActivity.class) ;
                            intent.putExtra("arrayProductPos",  arrProductsOfPos) ;
                            intent.putExtra("trafic", trafic);
                            intent.putExtra("idList", idList);
                            intent.putExtra("nameList",nameList) ;
                            intent.putExtra("mapLocation", mapLocation);
                            intent.putExtra("POS", String.valueOf(POS));
                            startActivity(intent);
                        } catch(JSONException e){
                            System.out.println(e +" catch analyse List");

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("localisation", "36.8060872,10.1797706");
                params.put("Trafic", trafic ) ;
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "Dlihjm3SE9rhH6I8VB9jx3Roz6uP9r6tghnChe");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(Stringrequest);
    }

    public void  updateListViewPos()
    {
        listViewAnalysePos.setVisibility(View.VISIBLE);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listPos);

        listViewAnalysePos.setAdapter(adapter);
    }



    @Override
    public void onLocationChanged(Location location) {
       String defaultLocation = location.getLatitude() + "," + location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}