package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

public class basketAnalyseActivity extends AppCompatActivity {

    private String idList, trafic, mapLocation;
    private JSONArray POS;
    private ImageView imageViewBack ;
    private ArrayList<ProductsOfPos> arrProductsOfPos = new ArrayList<>() ;
    private CustomlistAdapterAnalyse customlistAdapterAnalyse ;
    private GridView gridView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basket_analyse);

        getSupportActionBar().hide();

        // GridView
        gridView = findViewById(R.id.listBasket) ;

        // ImageView
        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        // get Attribute from another activity
        Intent intent = getIntent() ;

        idList = intent.getStringExtra("idList") ;
        trafic = intent.getStringExtra("trafic");
        mapLocation = intent.getStringExtra("mapLocation");
        try {
            POS = new JSONArray(intent.getStringExtra("POS"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getBaskect();
    }

    public void getBaskect()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try {

            jsonBodyObj.put("localisation", mapLocation);
            jsonBodyObj.put("Trafic", trafic );
            jsonBodyObj.put("idList" , idList) ;
            jsonBodyObj.put("POS",POS) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/analyseBaskets",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("my response :: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("msg");

                            for (int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject jsonObj = jsonArray.getJSONObject(i) ;

                                ProductsOfPos productsOfPos = new ProductsOfPos(jsonObj.getString("idPOS"),
                                        jsonObj.getString("Pos"),
                                        Float.valueOf(jsonObj.getString("priceTotal")),
                                        Integer.valueOf(jsonObj.getString("nbrProduit"))) ;
                                arrProductsOfPos.add(productsOfPos);
                            }
                            customlistAdapterAnalyse = new CustomlistAdapterAnalyse(basketAnalyseActivity.this, arrProductsOfPos) ;
                            gridView.setAdapter(customlistAdapterAnalyse);
                            customlistAdapterAnalyse.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("idList", idList);
                params.put("POS", String.valueOf(POS));
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
}
