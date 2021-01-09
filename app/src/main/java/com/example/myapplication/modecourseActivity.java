package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.myapplication.productsOfListActivity.idListMS;
import static com.example.myapplication.productsOfListActivity.nameList;
import static com.example.myapplication.productsOfListActivity.nameProductModeCourse;
import static java.lang.String.valueOf;

public class modecourseActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        LocationListener, AdapterView.OnItemSelectedListener {
    SearchView searchView;
    Dialog myDialog, myDialogPointOfSale;
    ListView lstView, listBeforBasket;
    private ZXingScannerView scannerview;
    private Button scan;
    String loc, idPointOfSale;
    ListView listView ;
    CustomListAdapterListProduct_course_mode adapter;
    LinearLayout buy, logoCart;
    protected LocationManager locationManager;
    private static boolean Check_GPS = true;
    private JSONArray pointOfSale, adressPointSale, Cart;
    private TextView numberOfProduct, sumPrice;
    private String idHistoriqueList;
    static ListView listViewPointOfSale;
    List<pos> Pos = new ArrayList<>();
    private float sumPrices;
    private List<productPointOfSale> productPointOfSales = new ArrayList<>();
    private List<productPointOfSale> finalProducts = new ArrayList<>();
    static List<productPointOfSale> productsPanier = new ArrayList<>();
    int preSelectedIndex;
    public boolean check = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modecourse);
        productsPanier = new ArrayList<>();
        getPointsOfSale();

        // Button
        scan=(Button)findViewById(R.id.scan);

        // setOnClickListener
        scan.setOnClickListener(this);


        searchView= findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");
        // Dialog
        myDialog = new Dialog(this);
        myDialogPointOfSale = new Dialog(this);

        logoCart = findViewById(R.id.logoCart);

        logoCart.setOnClickListener(this);
        Intent i = getIntent();

        // listView
        listView = (ListView) findViewById(R.id.listProduct);
        listBeforBasket = (ListView) findViewById(R.id.panier);

        buy = (LinearLayout)findViewById(R.id.buy) ;
        buy.setOnClickListener(this);


        myDialog.setContentView(R.layout.addproduct);



        DBHandlerlist dbHandler = new DBHandlerlist(this, null, null, 1);
        numberOfProduct = (TextView)findViewById(R.id.numberOfProduct);
        sumPrice = (TextView)findViewById(R.id.sumPrice);

        //add Product to panier with swipe
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {

                            }
                        });


        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Object selectedItem = (productPointOfSale) listView.getItemAtPosition(position);

                    productPointOfSale p = (productPointOfSale) selectedItem;
                    productsPanier.add(p);
                    sumPrices += Float.parseFloat(p.getPriceP()) / 1000 * Float.parseFloat(p.getQuantityP());
                    sumPrice.setText(String.valueOf(sumPrices) + "DT");
                    productPointOfSales.remove(p);
                    finalProducts.remove(p) ;
                    adapter.notifyDataSetChanged();

                    numberOfProduct.setText(String.valueOf(productsPanier.size()));

                    listBeforBasket.setAdapter(adapter);


                    adapter.notifyDataSetChanged();

                adapter = new CustomListAdapterListProduct_course_mode(modecourseActivity.this, productPointOfSales);
                listView = (ListView) findViewById(R.id.listProduct);
                listView.setAdapter(adapter);
                    touchListener.undoPendingDismiss();
            }
        });

        lstView = findViewById(R.id.listsearsh);
        lstView.setOnItemClickListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false ;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                productPointOfSales.clear();
                productPointOfSales.addAll(finalProducts) ;
                final String resText = newText ;
                    productPointOfSales.removeIf(new Predicate<productPointOfSale>() {
                        @Override
                        public boolean test(productPointOfSale productPointOfSale) {
                            return (!productPointOfSale.getNameP().toLowerCase().contains(resText.toLowerCase())) ;
                        }
                    }) ;


                adapter.notifyDataSetChanged();

                return false;

            }
        });


        // add for function location GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults).
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Check_GPS = true;



        // end of onCreate.
    }



    public void scancode(){
        scannerview =new ZXingScannerView(this);
        scannerview.setResultHandler(new ZXingScannerResultHandler());
        setContentView(scannerview);
        scannerview.startCamera();
    }


    public void onPause() {
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(Check_GPS) {
            loc = location.getLatitude() + "," + location.getLongitude();
            Check_GPS = false;
        }
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


    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler
    {

        @Override
        public void handleResult(Result result) {
            String ch=result.getText();
            System.out.println(ch);
            ShowPopupedit(ch,"3");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),productsOfListActivity.class) ;
        intent.putExtra("title",nameList) ;
        intent.putExtra("listIdIntoMsList",idListMS) ;
        startActivity(intent);
        super.onBackPressed();
    }

    public void getPointsOfSale(){

        // location to get latitude and longtitude
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("localisation", "36.8060872,10.1797706");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.pointOfSale + "/pointOfSale/localisation",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pointOfSale = new JSONArray();
                        adressPointSale = new JSONArray();
                        JSONObject jsonObj ;
                        Pos = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("isFound")){
                            Toast.makeText(modecourseActivity.this, "No point of sale found!", Toast.LENGTH_LONG).show();
                        }else {
                                JSONArray jsonAry = jsonObject.getJSONArray("result") ;
                            for (int i = 0; i < jsonAry.length(); i++) {
                                jsonObj = jsonAry.getJSONObject(i);
                                pointOfSale.put(jsonObj.getString("designation"));
                                adressPointSale.put(jsonObj.getString("address"));

                                Pos.add(new pos(false, jsonObj.getString("designation"), jsonObj.getString("address"), jsonObj.getString("idPointOfSale")));
                            }
                            popupPointOfSale();
                        }
                        } catch(JSONException e){

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(modecourseActivity.this, "Error connexion Location not exact", Toast.LENGTH_LONG).show();
                System.out.println(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("localisation", loc);
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

    public void popupPointOfSale() {

        TextView txtclose;
        Button cancel_btn, Ok;
        myDialogPointOfSale = new Dialog(this);


        preSelectedIndex = -1;

        myDialogPointOfSale.setContentView(R.layout.popup_check_point_of_sale);

        cancel_btn = myDialogPointOfSale.findViewById(R.id.cancel_btn);
        txtclose =(TextView) myDialogPointOfSale.findViewById(R.id.close);
        Ok = myDialogPointOfSale.findViewById(R.id.addPos);
        Ok.setOnClickListener(this);
        txtclose.setText("X");

        listViewPointOfSale = (ListView) myDialogPointOfSale.findViewById(R.id.simpleListView);


        final CustomPointOfSaleAdapter adapterPoint = new CustomPointOfSaleAdapter(modecourseActivity.this, Pos);
        listViewPointOfSale.setAdapter(adapterPoint);


        listViewPointOfSale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                pos model = Pos.get(i);
                model.setSelected(true);
                Pos.set(i, model);

                if (preSelectedIndex > -1){

                    pos preRecord = Pos.get(preSelectedIndex);
                    preRecord.setSelected(false);

                    Pos.set(preSelectedIndex, preRecord);

                }

                preSelectedIndex = i;
                idPointOfSale = model.getIdP();

                adapterPoint.updateRecords(Pos);
            }
        });

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
    }

    public void getPriceOfProduct(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idListMS);
            jsonBodyObj.put("idPointOfSale", idPointOfSale);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/modeCourse",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;
                        productPointOfSales = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArr = new JSONArray(jsonObject.getString("msg"));

                            for(int i=0; i<jsonArr.length();i++){
                                jsonObj = jsonArr.getJSONObject(i);

                                // String idModelP, String priceP, String quantityP, String unitP
                                productPointOfSales.add(new productPointOfSale(jsonObj.getString("idModel") ,
                                        jsonObj.getString("price"),
                                        jsonObj.getString("quantity"),
                                        jsonObj.getString("unit") ,
                                        (String ) nameProductModeCourse.get(i)
                                ));

                            }

                            finalProducts.addAll(productPointOfSales) ;


                            adapter = new CustomListAdapterListProduct_course_mode(modecourseActivity.this, productPointOfSales);
                            listView = (ListView) findViewById(R.id.listProduct);
                            listView.setAdapter(adapter);

                        } catch(JSONException e){

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(modecourseActivity.this, "Error connexion no price found", Toast.LENGTH_LONG).show();
                System.out.println(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idList", idListMS);
                params.put("idPointOfSale", idPointOfSale);
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

    public void addCart(){

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

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(modecourseActivity.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();

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
        Stringrequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                Toast.makeText(modecourseActivity.this, "Error connexion no price found", Toast.LENGTH_LONG).show();
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
        Stringrequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(Stringrequest);
    }

    @Override
    public void onClick(View v) {
        ProgressDialog pDialog = new ProgressDialog(this);
        List<Product> li = new ArrayList<Product>();

        ListView listView = (ListView) findViewById(R.id.listProduct);
        JSONObject  o =new JSONObject ();
        JSONArray  a =new JSONArray ();
        switch (v.getId()) {
            case R.id.logoCart:
                Intent intentPanier = new Intent(modecourseActivity.this ,
                        basketActivity.class);
                intentPanier.putExtra("idListMS", idListMS);
                intentPanier.putExtra("idPointOfSale", idPointOfSale);
                intentPanier.putExtra("sumPrices", sumPrices);
                intentPanier.putExtra("check",check) ;
                modecourseActivity.this.startActivity(intentPanier);
                finish();
                break;
            case R.id.add:
                EditText name=(EditText)myDialog.findViewById(R.id.nameproduct);
                EditText prix=(EditText)myDialog.findViewById(R.id.prix);
                System.out.print(name+" "+prix);
                MyDBHandler mh=new MyDBHandler(this, null, null, 1);
                int quantity =Integer.parseInt(prix.getText().toString());
                String namee =name.getText().toString();
                //Product product = new Product(namee, quantity);
                if(mh.findProduct(namee).isEmpty()){
                    //mh.addProduct(product);
                    name.setText("");
                    prix.setText("");
                    myDialog.hide();
                }else {
                    Toast.makeText(getBaseContext(), "This product is exist", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.scan:

                scancode();
                break;
            case R.id.addPos:
                myDialogPointOfSale.hide();
                getPriceOfProduct();
                Toast.makeText(getBaseContext(), "Add Point Of Sale", Toast.LENGTH_LONG).show();
                break;
            case R.id.addtolist:
                DBHandlerlist dbHandler = new DBHandlerlist(this, null, null, 1);
                EditText nam=(EditText)myDialog.findViewById(R.id.nameproduct);
                EditText pri=(EditText)myDialog.findViewById(R.id.prix);
                //Spinner Unites=(Spinner) myDialog.findViewById(R.id.unite);
                EditText quantite=(EditText)myDialog.findViewById(R.id.quantite);
                float qt=Float.parseFloat((quantite.getText()).toString());
                //Unites.setOnItemSelectedListener(this);
                Float f= Float.parseFloat(pri.getText().toString());
                int b=(int) Math.round(f);


                Intent it = getIntent();

                List<ListProduct> productList = dbHandler.fetchlistdetail(it.getStringExtra("title"));

                JSONObject js = null;
                JSONArray jsonarray =new JSONArray();

                for (int k=0;k<productList.size();k++){

                    String ch=productList.get(k).getList();
                    System.out.println(ch+"kilou");
                    if (ch != null) {
                        try {
                            js = new JSONObject(ch);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsonarray = js.getJSONArray("list");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        js = new JSONObject();
                        try {
                            jsonarray = js.getJSONArray("list");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }


                jsonarray.put(nam.getText().toString());
                jsonarray.put(b);
                jsonarray.put("kg");

                try {
                    jsonarray.put(qt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    o.put("list", jsonarray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //send variable to database "sqllite"

                System.out.println(o);
                ListProduct l=new ListProduct();
                Intent i = getIntent();
                l.setName(i.getStringExtra("title"));
                l.setList(o.toString());
                dbHandler.editList(l);


                //get date to adapter to show the list
                String nameproduct = null;
                int prixproduct = 0;
                int conteur =0;
                String unite =null;
                Float quant = null;
                while (conteur<jsonarray.length()){

                    try {
                        nameproduct =(valueOf(jsonarray.get(conteur)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        prixproduct=(Integer) jsonarray.get(conteur+1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        unite=(String) jsonarray.get(conteur+2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        quant=Float.parseFloat(jsonarray.getString(conteur+3));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Product pi = new Product() ;
                    //pi.set_productname(nameproduct);
                    pi.setPrix(prixproduct);
                    //pi.setUnite(unite);
                    //pi.setQuantite(quant);
                    li.add(pi);
                    conteur=conteur+4;
                    TextView pr=(TextView)findViewById(R.id.price);
                  //  TextView number=(TextView)findViewById(R.id.number);
                    int nb = adapter.getCount();
                    double S=0;
                    for(int e=0;e<adapter.getCount();e++){
                        Product p= (Product) adapter.getItem(e);
                       // S=S+p.getQuantite()*p.getPrix();
                    }
                    pr.setText(valueOf(S)+" Price");
                   // number.setText(valueOf(nb)+" Product");

                }
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                myDialog.hide();
                break;
                case R.id.buy:
                    if(productsPanier.size() == 0){
                        Toast.makeText(modecourseActivity.this,"No product in the Cart", LENGTH_SHORT).show();
                    }else
                    {
                        check = true ;
                        createCart();
                    }
                    break;

        }
    }

    //pupup of create group
    public void ShowPopupedit(String name,String prix) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.add_product_to_list);
        txtclose =(TextView) myDialog.findViewById(R.id.close);
        txtclose.setText("X");
        //Spinner spinner = (Spinner) myDialog.findViewById(R.id.unite);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);



        Button add=(Button)myDialog.findViewById(R.id.addtolist);
        add.setOnClickListener(this);
        EditText nam=(EditText)myDialog.findViewById(R.id.nameproduct);
        EditText pri=(EditText)myDialog.findViewById(R.id.prix);
        nam.setText(name);
        pri.setText(prix);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MyDBHandler mih=new MyDBHandler(this, null, null, 1);

        String website = (String) lstView.getItemAtPosition(position);
        Product product = mih.findProductdatil(website);
        // String name=product.get_productname();
        String prs= valueOf(product.getPrix());
        // ShowPopupedit(name ,prs); showPopupeditPointOfSale(); for the update idPos is important

    }
}