package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.modecourseActivity.productsPanier;
import static com.example.myapplication.productsOfListActivity.nameList;


public class basketActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),productsOfListActivity.class) ;
        intent.putExtra("title",nameList) ;
        intent.putExtra("listIdIntoMsList",idListMS) ;
        startActivity(intent);
        super.onBackPressed();
    }

    LinearLayout buy;
    private JSONArray Cart;
    private String idListMS, idHistoriqueList, idPointOfSale;
    private float sumPrices;
    private Boolean check ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panier);

        getSupportActionBar().hide();
        Intent panierIntent = getIntent();
        Bundle bundle = panierIntent.getExtras();
        idListMS = (String) bundle.get("idListMS");
        idPointOfSale = (String) bundle.get("idPointOfSale");
        sumPrices = (float) bundle.get("sumPrices");
        check = bundle.getBoolean("check") ;
        CustomListAdapterListProduct_course_mode adapter2 = new CustomListAdapterListProduct_course_mode(basketActivity.this, productsPanier);
        ListView listView = (ListView) findViewById(R.id.panier);
        listView.setAdapter(adapter2);

        // LinearLayout
        buy = (LinearLayout)findViewById(R.id.buy) ;

        Cart = new JSONArray();

        if (check)
        {
            buy.setVisibility(View.INVISIBLE);
        }

        // setOnclickListener
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productsPanier.size() != 0 )
                {
                    check = true ;
                    createCart();
                }

                else
                    Toast.makeText(basketActivity.this, "empty list", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addCart(){

        // 78ef18ee14e79da0354dc203592b99e5cb2c36e8f261912664868f08115d09e3

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idPos", idPointOfSale);
            jsonBodyObj.put("idHistoriqueList", idHistoriqueList);
            jsonBodyObj.put("prixTotal", String.valueOf(sumPrices));
            jsonBodyObj.put("listAchat", Cart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/Panier/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(basketActivity.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();

                        } catch(JSONException e){

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
                params.put("idPos", idPointOfSale);
                params.put("idHistoriqueList", idHistoriqueList);
                params.put("prixTotal", String.valueOf(sumPrices));
                params.put("listAchat", String.valueOf(Cart));
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

    public void createCart(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idListMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/historiqueList/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = new JSONObject();
                        Cart = new JSONArray();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // idModel , unit , quantity , prix

                            idHistoriqueList = jsonObject.getString("idHistoriqueList");
                            for(int i=0;i<productsPanier.size();i++){
                                jsonObj.put("idModel", productsPanier.get(i).getIdModelP());
                                jsonObj.put("unit", productsPanier.get(i).getUnitP());
                                jsonObj.put("quantity", productsPanier.get(i).getQuantityP());
                                jsonObj.put("prix", productsPanier.get(i).getPriceP());
                                Cart.put(jsonObj);
                                jsonObj = new JSONObject();
                            }

                            addCart();
                        } catch(JSONException e){

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(basketActivity.this, "Error connexion no price found", Toast.LENGTH_LONG).show();
                System.out.println(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idList", idListMS);
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
