package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

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

public class productsOfCircuitActivity extends AppCompatActivity {
    private ListView listView ;
    private customAdapterCircuit customAdapterCircuit ;
    private List<Product> listOfProducts ;
    private String idList , idPos , nameList , namePos ;
    private TextView textViewNameList , textViewNamePos ,textViewTotalPrice , textViewNumberOfProducts ;
    private float totalPrice ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_of_circuit);
        listView = findViewById(R.id.listProduct) ;

        // hide App Title Bar
        getSupportActionBar().hide();

        // intent
        Intent intent = getIntent() ;
        idList = intent.getStringExtra("idList") ;
        idPos = intent.getStringExtra("idPos") ;
        namePos = intent.getStringExtra("namePos") ;
        nameList = intent.getStringExtra("nameList") ;
        totalPrice = intent.getFloatExtra("totalPrice",0) ;

        // TextView
        textViewNameList = findViewById(R.id.nameList) ;
        textViewNamePos = findViewById(R.id.namePos) ;
        textViewTotalPrice = findViewById(R.id.total_price) ;
        textViewNumberOfProducts = findViewById(R.id.numberOfProduct) ;

        textViewNamePos.setText(namePos);
        textViewNameList.setText(nameList);
        textViewTotalPrice.setText(String.valueOf(totalPrice)) ;

        listOfProducts = new ArrayList<>() ;

        getProductsCircuit() ;
    }

    public void getProductsCircuit(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idList);
            jsonBodyObj.put("idPOS", idPos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/analyseList/getListProductbyPOS",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("arraymodel") ;
                            listOfProducts.clear();
                            for (int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject jsonobj = jsonArray.getJSONObject(i) ;
                                String category = jsonobj.getString("nameCategory") ;
                                String mark = jsonobj.getString("nameMark") ;
                                String productName =  jsonobj.getString("nameProduct") ;
                                String model =  jsonobj.getString("nameModel") ;
                                String price = jsonobj.getString("price" ) ;
                                String quantity =  jsonobj.getString("quantity") ;
                                String unit = jsonobj.getString("unit" ) ;
                                Product product = new Product(productName,mark,model,unit,quantity,category,Integer.parseInt(price)) ;
                                listOfProducts.add(product) ;
                            }



                            customAdapterCircuit = new customAdapterCircuit(productsOfCircuitActivity.this,listOfProducts) ;
                            listView.setAdapter(customAdapterCircuit);
                            textViewNumberOfProducts.setText(String.valueOf(listOfProducts.size()));


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
                params.put("idList", idList);
                params.put("idPOS", idPos ) ;
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