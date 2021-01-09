package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

public class basketsOfHistoric extends AppCompatActivity {
    private String idHistoric , nameList ;
    private List<Basket> Baskets ;
    private Basket basket ;
    private Dialog myDialog;
    private ProgressBar progressBar ;
    private GridView gridView;
    private CustomListAdapterBasketOfSingleHistroric CustomListAdapterBasketOfSingleHistroric ;
    private ImageView imageViewBack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baskets_of_historic);

        // hide App Title Bar
        getSupportActionBar().hide();

        // Dialog
        myDialog = new Dialog(this) ;
        // array of baskets
        Baskets = new ArrayList<>();

        // imageView
        imageViewBack = findViewById(R.id.back) ;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        progressBar = findViewById(R.id.progress_bar) ;


        Intent intent = getIntent() ;
        idHistoric = intent.getStringExtra("idHistoric") ;
        nameList = intent.getStringExtra("nameList") ;
        getBasketsOfList() ;

        // gridView
        gridView = (GridView) findViewById(R.id.listoflist);

        CustomListAdapterBasketOfSingleHistroric = new CustomListAdapterBasketOfSingleHistroric(this, Baskets);

        gridView.setAdapter(CustomListAdapterBasketOfSingleHistroric);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Basket basket = (Basket) parent.getItemAtPosition(position) ;
                Intent intent = new Intent(getApplicationContext(), productsOfBasketActivity.class) ;
                intent.putExtra("namePos",basket.getPos()) ;
                intent.putExtra("idBasket",basket.getIdPanier()) ;
                intent.putExtra("nameList",nameList) ;
                intent.putExtra("dateBasket",basket.getDateAchat()) ;
                startActivity(intent);
            }
        });


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Basket basket = (Basket) parent.getItemAtPosition(position) ;
                ShowPopupDelete(basket) ;
                return true;
            }
        });



    }


    public void getBasketsOfList()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idHistoriqueList", idHistoric);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/Paniers/Historiquelist",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response) ;
                            JSONArray allHistorique = new JSONArray(jsonObject.getString("listPanier"));

                            for(int i=0;i<allHistorique.length();i++){
                                JSONObject jsonObj = allHistorique.getJSONObject(i);
                                 basket =new Basket(jsonObj.getString("Pos"),jsonObj.getString("dateAchat"),jsonObj.getString("idPanier"));
                                Baskets.add(basket);
                            }
                            progressBar.setVisibility(View.GONE);
                            CustomListAdapterBasketOfSingleHistroric.notifyDataSetChanged();

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
                params.put("idHistoriqueList", idHistoric);
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

    public void ShowPopupDelete(final Basket basket)
    {
        myDialog.setContentView(R.layout.delete);
        TextView TextViewClose = myDialog.findViewById(R.id.close);
        Button btnDelete = myDialog.findViewById(R.id.delete);
        Button btnCancel = myDialog.findViewById(R.id.cancel);

        TextViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBasket(basket);
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void deleteBasket(final Basket basket)
    {
        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idPanier", basket.getIdPanier());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/Panier/delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Baskets.remove(basket) ;
                        CustomListAdapterBasketOfSingleHistroric.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idPanier",  basket.getIdPanier());
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