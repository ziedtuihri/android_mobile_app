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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class historicSingleListActivity extends AppCompatActivity implements View.OnClickListener{

    private CustomListAdapterSingleHistoric customListAdapterSingleHistoric ;
    private GridView gridView;
    Dialog myDialog;
    private List<ListProductHistorique> historicList ;
    private TextView textViewTitle ;
    private String idList , nameList ;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_list);

        // hide App Title Bar
        getSupportActionBar().hide();

        // Intent
        Intent intent = getIntent() ;
        idList = intent.getStringExtra("idList") ;
        nameList = intent.getStringExtra("nameList") ;

        // imageView
        imageViewBack = findViewById(R.id.back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        // TextView
        textViewTitle = findViewById(R.id.title_historic) ;
        textViewTitle.setText(nameList);

        // Arraylist
        historicList = new ArrayList<>() ;

        // gridView
        gridView = (GridView) findViewById(R.id.listoflist);


        myDialog = new Dialog(this);



        customListAdapterSingleHistoric = new CustomListAdapterSingleHistoric(this, historicList);

        gridView.setAdapter(customListAdapterSingleHistoric);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListProductHistorique historique =(ListProductHistorique) parent.getItemAtPosition(position) ;
                Intent intent = new Intent(getApplicationContext(),basketsOfHistoric.class);
                intent.putExtra("idHistoric" ,historique.getIdHistorique())  ;
                intent.putExtra("nameList",nameList) ;
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListProductHistorique historique = (ListProductHistorique) parent.getItemAtPosition(position) ;
                ShowPopupDelete(historique) ;
                return true;
            }
        });

        getHistoricsOfList() ;

    }


    public void getHistoricsOfList()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/historiqueList/byId",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response) ;
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                ListProductHistorique p =new ListProductHistorique(jsonObj.getString("date"),jsonObj.getString("idHistoriqueList"),jsonObj.getString("idList"));
                                historicList.add(p);
                            }

                            System.out.println(historicList);
                            customListAdapterSingleHistoric.notifyDataSetChanged();

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
                params.put("idList", idList);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.back:
                onBackPressed();
                break;
        }
    }

    public void ShowPopupDelete(final ListProductHistorique listProductHistorique) {

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
                deleteHistoric(listProductHistorique);
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    public void deleteHistoric(final ListProductHistorique listProductHistorique)
    {
        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idHistoriqueList", listProductHistorique.getIdHistorique());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/historiqueList/delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        historicList.remove(listProductHistorique) ;
                        customListAdapterSingleHistoric.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idHistoriqueList", listProductHistorique.getIdHistorique());
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