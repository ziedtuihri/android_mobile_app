package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class productsOfBasketActivity extends AppCompatActivity {
    private TextView textViewTotalPrice , textViewNumberOfProducts , textViewNamePos , textViewDateBasket , textViewNameList;
    private String namePos , idBasket , nameList , dateBasket;
    private CustomListAdapterListProduct customListAdapterListProduct ;
    private List<ProductMicroService> listOfProducts ;
    private ListView listViewProducts ;
    private ProductMicroService product ;
    private ImageView imageViewBack ;
    private ProgressBar progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_of_basket);

        // hide App Title Bar
        getSupportActionBar().hide();

        progressBar = findViewById(R.id.progress_bar) ;


        // textView
        textViewTotalPrice = findViewById(R.id.total_price) ;
        textViewNumberOfProducts = findViewById(R.id.numberOfProduct) ;
        textViewNamePos = findViewById(R.id.namePos) ;
        textViewDateBasket = findViewById(R.id.dateBasket) ;
        textViewNameList = findViewById(R.id.nameList) ;


        // imageViewBack
        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        // listView
        listViewProducts = findViewById(R.id.listProduct) ;

        // Intent
        Intent intent = getIntent() ;
        namePos = intent.getStringExtra("namePos") ;
        idBasket = intent.getStringExtra("idBasket") ;
        nameList = intent.getStringExtra("nameList") ;
        dateBasket = intent.getStringExtra("dateBasket")  ;

        // set nameList , dateBasket
        textViewNameList.setText(nameList);
        textViewDateBasket.setText(dateBasket);

        // set name of pos
        textViewNamePos.setText(namePos);

        listOfProducts = new ArrayList<>() ;

        customListAdapterListProduct = new CustomListAdapterListProduct(this,listOfProducts) ;
        listViewProducts.setAdapter(customListAdapterListProduct);
        getProducts();
    }


    public void getProducts()
    {
        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idPanier", idBasket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/Panier/byid",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response) ;
                            String price = jsonObject.getString("prixTotal") ;
                            String numberOfProducts = jsonObject.getString("nb") ;

                            // set number of products
                            textViewNumberOfProducts.setText(numberOfProducts);
                            // set total price
                            textViewTotalPrice.setText(price);

                            JSONArray allHistorique = new JSONArray(jsonObject.getString("listAchat"));


                            for(int i=0;i<allHistorique.length();i++){
                                JSONObject jsonObj = allHistorique.getJSONObject(i);

                                product =new ProductMicroService(jsonObj.getString("nameProduct"),
                                        jsonObj.getString("nameMark"),
                                        jsonObj.getString("nameModel"),
                                        jsonObj.getString("unit") ,
                                        jsonObj.getString("quantity") ,
                                        jsonObj.getString("nameCategory") ,
                                        Integer.parseInt(jsonObj.getString("prix"))
                                        );
                                listOfProducts.add(product);
                            }
                            progressBar.setVisibility(View.GONE);
                            customListAdapterListProduct.notifyDataSetChanged();

                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idPanier", idBasket);
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