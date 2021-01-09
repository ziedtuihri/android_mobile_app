package com.example.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class historiquelistActivity extends AppCompatActivity implements View.OnClickListener {
    private SessionManager sessionManager;
    private Dialog myDialog;
    private ProgressBar progressBar ;
    private List<ListProductHistorique> ListHistorique =new ArrayList<ListProductHistorique>() ;
    private GridView gridView;
    private CustomListAdapterHistoriqueList adapter;
    private int userId;
    private String codeimei;
    DeviceSession deviceSession;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historiquelist);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        sessionManager =new SessionManager(this);
        myDialog = new Dialog(this);

        progressBar = findViewById(R.id.progress_bar) ;
        gridView = (GridView) findViewById(R.id.listoflist);
        adapter = new CustomListAdapterHistoriqueList(historiquelistActivity.this, ListHistorique);

        // device session to get id user and id device
        deviceSession = new DeviceSession(this);
        userId = Integer.parseInt(deviceSession.sharedPreferences.getString("userId","0"));
        codeimei = deviceSession.sharedPreferences.getString("imei", "0");

        gridView.setAdapter(adapter);

        getHistorique();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListProductHistorique historique = (ListProductHistorique) parent.getItemAtPosition(position) ;
                Intent intent = new Intent(getApplicationContext(),basketsOfHistoric.class) ;
                intent.putExtra("idHistoric" , historique.getIdHistorique()) ;
                intent.putExtra("nameList",historique.getName()) ;
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




    }

    public void getHistorique(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idUser", "1");
            jsonBodyObj.put("codeImei", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/list/getHistory",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ListHistorique.clear();

                            JSONArray allHistorique = new JSONArray(response) ;

                            for (int i = 0 ; i <allHistorique.length() ; i++)
                            {
                                try {
                                    JSONArray historique = allHistorique.getJSONArray(i) ;
                                    for(int j=0;j<historique.length();j++){
                                        JSONObject jsonObj = historique.getJSONObject(j);
                                        ListProductHistorique p =new ListProductHistorique(jsonObj.getString("nameList"),jsonObj.getString("date"),jsonObj.getString("idHistoriqueList"),jsonObj.getString("idList"));
                                        ListHistorique.add(p);
                                }

                            } catch (Exception e) {}


                            }
                                progressBar.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

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
                params.put("idUser", "1");
                params.put("codeImei", "1");
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
        Stringrequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(Stringrequest);
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
                        ListHistorique.remove(listProductHistorique) ;
                        adapter.notifyDataSetChanged();

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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back:
                onBackPressed();
                break;

        }
    }
}
