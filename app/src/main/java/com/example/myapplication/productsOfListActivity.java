package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class productsOfListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {

    private SearchView searchView;
    private ArrayList<String> listProductsSearch ;
    private Button btnVoice;
    private LinearLayout linearLayoutModeCourse , LinearLayoutAnalyse;
    private Dialog myDialog;
    private ListView listViewSearch , listViewProducts;
    private List<ProductMicroService> listProducts ;
    private TextView textViewTitle, textViewModecourse;
    private MyDBHandlerProduct dbHandlerProduct ;
    private String idProductMS, idMarkMS, idModelMS, unit = "";
    static String idListMS , nameList;
    private ArrayList<String> AllMark = new ArrayList<>();
    static ArrayList<String> AllModel = new ArrayList<>();
    static ArrayList<String> allfirstModel = new ArrayList<>();
    private ProductMicroService ProductToUpdate ;
    static ArrayList<String> AllUnit = new ArrayList<>();
    private JSONArray IdProductMicroService, idMark, nameProductMicroService, categoryProductMicroService;
    static JSONArray nameProductModeCourse;

    static JSONArray idModel;
    private String categoryProduct, nameProductMS;
    private DBHandlerlist dbHandler;
    private CustomListAdapterListProduct CustomListAdapterListProduct ;
    static ArrayAdapter<String> arrayAdapter2;
    private String nameIndexed;
    private int idProductOnLocal;
    private ProgressBar progressBar ;

    private LinearLayout linearLayoutHistoric ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_product);

        // Arraylist of type String
        listProductsSearch = new ArrayList<>() ;


        // Progress Bar
        progressBar = findViewById(R.id.progress_bar) ;
        // Buttons
        btnVoice = findViewById(R.id.btnVoice);

        // linearLayout
        linearLayoutHistoric = findViewById(R.id.lin_historic) ;
        LinearLayoutAnalyse = findViewById(R.id.LinearLayoutAnalyse) ;

        // ListView
        listViewSearch = findViewById(R.id.listsearsh);
        listViewProducts = findViewById(R.id.listProduct);

        // searchView
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");

        // textView
        textViewTitle = findViewById(R.id.title);
        textViewModecourse = findViewById(R.id.modecourse);

        // Dialog
        myDialog = new Dialog(this);

        // LinearLayout
        linearLayoutModeCourse = findViewById(R.id.linearLayoutModeCourse) ;

        // setOnclickListener
        listViewSearch.setOnItemClickListener(this);
        btnVoice.setOnClickListener(this);
        linearLayoutHistoric.setOnClickListener(this);
        linearLayoutModeCourse.setOnClickListener(this);
        textViewModecourse.setOnClickListener(this);
        LinearLayoutAnalyse.setOnClickListener(this);

        // dbHandler of list
        dbHandler = new DBHandlerlist(this, null, null, 1);

        // Intent
        Intent intent = getIntent();
        nameList = intent.getStringExtra("title") ;
        textViewTitle.setText(nameList);
        idListMS = intent.getStringExtra("listIdIntoMsList");

        // MyDBHandlerProduct ( local database products )
        dbHandlerProduct = new MyDBHandlerProduct(productsOfListActivity.this);

        // get list of products from Microservice
        getListMicroService(idListMS);

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listViewProducts),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {

                            }
                        });


        listViewProducts.setOnTouchListener(touchListener);
        listViewProducts.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {


                view.findViewById(R.id.txt_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductMicroService product = (ProductMicroService) listViewProducts.getItemAtPosition(position);

                        // delete Product from local database ( id list , id Product )
                        dbHandlerProduct.deletProductList(product.getIdMS(), product.getIdListMS());

                        // get list of Products after delete
                        listProducts = dbHandlerProduct.getAllProducts(idListMS) ;
                        CustomListAdapterListProduct = new CustomListAdapterListProduct(productsOfListActivity.this, listProducts);

                        listViewProducts.setAdapter(CustomListAdapterListProduct);

                    }
                });

                view.findViewById(R.id.txt_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // get selected Product
                        ProductToUpdate = (ProductMicroService) listViewProducts.getItemAtPosition(position);

                        AllMark.clear();
                        AllModel.clear();
                        AllUnit.clear();

                        // add mark and model to Popup
                        AllMark.add(ProductToUpdate.getMarkMS());
                        AllModel.add(ProductToUpdate.getModelMS());

                        AllUnit.add("Select a Unit");

                        // get IdProduct
                        idProductMS = String.valueOf(ProductToUpdate.getIdProductMS());
                        idProductOnLocal = ProductToUpdate.getIdMS();

                        // get unit
                        unit = ProductToUpdate.getUnitMS();

                        // get units from microservice input ( idModel )
                        getUnitsOfModelFromMicroService(ProductToUpdate.getIdModelMS());
                        listProducts = dbHandlerProduct.getAllProducts(idListMS);

                        CustomListAdapterListProduct = new CustomListAdapterListProduct(productsOfListActivity.this, listProducts);

                        listViewProducts.setAdapter(CustomListAdapterListProduct);
                    }
                });

            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 0) {
                    sendIndex(newText);
                } else {
                    listViewSearch.setAdapter(null);
                }
                return false;
            }
        });


    }


    @Override
    protected void onStop() {
        super.onStop();

        try {
            // when the user exit this activity this function will send products to microservice
            insertProductsOnMicroservice();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getListMicroService(final String idList) {

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idList);
        } catch (JSONException e) { }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/listProduct/getListProductbyid",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj ;
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("msg").equals("No list found!")) {
                                Toast.makeText(productsOfListActivity.this, "No list found!", Toast.LENGTH_LONG).show();
                            } else if(jsonObject.getString("msg").equals("404")) {
                                Toast.makeText(productsOfListActivity.this, "check ip on list", Toast.LENGTH_LONG).show();
                            }else {

                                JSONArray jsonArray = new JSONArray(jsonObject.getString("arraymodel"));

                                // delete list of products from local database
                                dbHandlerProduct.deletAll(idListMS);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    jsonObj = jsonArray.getJSONObject(i);

                                    // add product to local database ( mydbhandlerProduct )
                                    addProductintoList(jsonObj.getString("nameProduct"),
                                            jsonObj.getString("nameCategory"),
                                            jsonObj.getString("nameMark"),
                                            jsonObj.getString("nameModel"),
                                            jsonObj.getString("unit"),
                                            jsonObj.getString("quantity"),
                                            jsonObj.getString("idModel")
                                    );
                                }

                                // get list products from local db by idList Of microservice
                                listProducts = dbHandlerProduct.getAllProducts(idListMS);

                                // custom adapter
                                CustomListAdapterListProduct = new CustomListAdapterListProduct(productsOfListActivity.this, listProducts);
                                // Hide the progress Bar
                                progressBar.setVisibility(View.GONE);
                                // add custom adapter to listView
                                listViewProducts.setAdapter(CustomListAdapterListProduct);
                            }
                        } catch (JSONException e) { }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listProducts = dbHandlerProduct.getAllProducts(idListMS);
                CustomListAdapterListProduct = new CustomListAdapterListProduct(productsOfListActivity.this, listProducts);
                listViewProducts.setAdapter(CustomListAdapterListProduct);
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

    public void sendIndex(final String keyword) {

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("nameProduct", keyword);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/searchingProduct",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        IdProductMicroService = new JSONArray();
                        nameProductMicroService = new JSONArray();
                        categoryProductMicroService = new JSONArray();
                        listProductsSearch.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            nameIndexed = jsonObject.getString("Product");
                            if (!nameIndexed.equals("[]")) {
                                JSONArray jsonArr = new JSONArray(nameIndexed);

                                for (int i = 0; i < jsonArr.length(); i++) {

                                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                                    String nameP = jsonObj.getString("nameProduct") + "\n " + " Category :" + jsonObj.getString("nameCategory");
                                    IdProductMicroService.put(jsonObj.getString("idProduct"));
                                    nameProductMicroService.put(jsonObj.getString("nameProduct"));
                                    categoryProductMicroService.put(jsonObj.getString("nameCategory"));
                                    listProductsSearch.add(nameP);
                                }

                                    ArrayAdapter adapter = new ArrayAdapter(productsOfListActivity.this, android.R.layout.simple_list_item_1, listProductsSearch);
                                    listViewSearch.setAdapter(adapter);
                            } else
                            {
                                ArrayAdapter adapter = new ArrayAdapter(productsOfListActivity.this, android.R.layout.simple_list_item_1, listProductsSearch);
                                listViewSearch.setAdapter(adapter);

                            }
                        } catch (JSONException e) { }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nameProduct", keyword);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchView.setQuery(result.get(0), false);
            }
        }
    }




    // function to show popup to update the poduct
    public void UpdateProduct(final AppCompatActivity appCompatActivity) {

        myDialog.setContentView(R.layout.product_update_popup);

        // TextView
        TextView TextViewclose =  myDialog.findViewById(R.id.close);
        TextView TextViewCategorie = myDialog.findViewById(R.id.category);
        TextView TextViewMark = myDialog.findViewById(R.id.yourMark);
        TextView TextViewModel =  myDialog.findViewById(R.id.yourModel);
        TextView TextViewNameProduct = myDialog.findViewById(R.id.nameproduct);

        // set the name of product to editText
        TextViewNameProduct.setText(ProductToUpdate.getProductNameMS());

        // EditText (final to access to setOnCLickListener)
        final EditText EditTextQuantity = myDialog.findViewById(R.id.quantity);

        // set quantity value to editText
        EditTextQuantity.setText(ProductToUpdate.getQuantityMS());

        // Button
        Button btnCancel = myDialog.findViewById(R.id.cancel_btn);
        Button btnUpdate = myDialog.findViewById(R.id.updateInList);

        // spinner
        Spinner spinnerUnit = myDialog.findViewById(R.id.Unit);

        // ArrayAdapter
        ArrayAdapter<String> arrayAdapterUnits = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                AllUnit);

        // set adapter
        spinnerUnit.setAdapter(arrayAdapterUnits);

        // set default position of unit
        spinnerUnit.setSelection(1);

        // set layout of spinner
        arrayAdapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // setText of ( mark , model , category )
        TextViewMark.setText(ProductToUpdate.getMarkMS());
        TextViewModel.setText(ProductToUpdate.getModelMS());
        TextViewCategorie.setText(ProductToUpdate.getCategoryMS());

        // setOnclickListener
        TextViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        // update product
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner SpinnerUnit = myDialog.findViewById(R.id.Unit);
                // check if the Quantity field is empty
                if (EditTextQuantity.getText().toString().isEmpty()) {
                    EditTextQuantity.setError("Quantity Required");
                    EditTextQuantity.requestFocus();
                    return;
                }
                 if (SpinnerUnit.getSelectedItemPosition() == 0) {
                    Toast.makeText(productsOfListActivity.this, "Select a Unit", Toast.LENGTH_LONG).show();
                } else {

                     // update Product on local database
                    dbHandlerProduct.updateProductLocal(idProductOnLocal,
                            idListMS,
                            SpinnerUnit.getSelectedItem().toString(),
                            EditTextQuantity.getText().toString()
                    );

                    // get all products from local database
                    listProducts = dbHandlerProduct.getAllProducts(idListMS);

                    // update Custom adapter
                    CustomListAdapterListProduct = new CustomListAdapterListProduct(appCompatActivity, listProducts);

                    listViewProducts.setAdapter(CustomListAdapterListProduct);
                    myDialog.dismiss();

                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }
//

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    public void insertProductsOnMicroservice() throws JSONException {

        // get list of products
        listProducts = dbHandlerProduct.getAllProducts(idListMS);

        nameProductModeCourse = new JSONArray();

        final JSONArray jsonList = new JSONArray();

        for (int i = 0; i < listProducts.size(); i++) {
            JSONObject jsonListProduct = new JSONObject();
            jsonListProduct.put("idModel", listProducts.get(i).getIdModelMS());
            jsonListProduct.put("quantity", listProducts.get(i).getQuantityMS());
            jsonListProduct.put("unit", listProducts.get(i).getUnitMS());

            // insert ( idModel , quantity , unit ) to json
            jsonList.put(jsonListProduct);
            nameProductModeCourse.put(listProducts.get(i).getProductNameMS());
        }


        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idListMS);
            jsonBodyObj.put("listProduct", jsonList); // jsonArray

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.PUT, HostName_Interface.list + "/updatelistProduct",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idList", idListMS);
                params.put("listProduct", String.valueOf(jsonList));
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


    /*
     * this method to add Product into list on local on MyDBHandlerProduct.java
     * @params Field into popap add_product_to_list
     */
    public void addProductintoList(final String nameProduct, final String category, final String mark,
                                   final String model, final String unit, final String quantity, final String idModelMicroS) {
        try {

            ProductMicroService product = new ProductMicroService(0,
                    idListMS,
                    nameProduct,
                    mark,
                    model,
                    "null",
                    unit,
                    quantity,
                    "null",
                    "description",
                    category,
                    0,
                    idModelMicroS,
                    "0"

            );
            dbHandlerProduct.addProduct(product);

        } catch (Exception e) {
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.add:
                EditText name = (EditText) myDialog.findViewById(R.id.nameproduct);
                EditText prix = (EditText) myDialog.findViewById(R.id.prix);
                System.out.print(name + " " + prix);
                MyDBHandler mh = new MyDBHandler(this, null, null, 1);
                String namee = name.getText().toString();
                if (mh.findProduct(namee).isEmpty()) {
                    name.setText("");
                    prix.setText("");
                    myDialog.hide();
                }
                break;

            case R.id.addtolist:
                MyDBHandlerProduct dbHandlerProduct = new MyDBHandlerProduct(this);

                System.out.println("-------- products => " + nameProductMicroService);

                TextView nameProduct = (TextView) myDialog.findViewById(R.id.nameproduct);
                TextView categoryPopup = (TextView) myDialog.findViewById(R.id.category);
                Spinner markProduct = (Spinner) myDialog.findViewById(R.id.Mark);
                Spinner modelProduct = (Spinner) myDialog.findViewById(R.id.Model);
                Spinner unitProduct = (Spinner) myDialog.findViewById(R.id.Unit);
                EditText quantityP = (EditText) myDialog.findViewById(R.id.quantity);

                if (quantityP.getText().toString().isEmpty()) {
                    quantityP.setError("Quantity Required");
                    quantityP.requestFocus();
                } else if (markProduct.getSelectedItemPosition() == 0) {
                    Toast.makeText(productsOfListActivity.this, "Select a Mark", Toast.LENGTH_LONG).show();


                } else if (modelProduct.getSelectedItemPosition() == 0) {
                    Toast.makeText(productsOfListActivity.this, "Select a Model", Toast.LENGTH_LONG).show();

                } else if (unitProduct.getSelectedItemPosition() == 0) {
                    Toast.makeText(productsOfListActivity.this, "Select a Unit", Toast.LENGTH_LONG).show();

                } else {

                    addProductintoList(nameProduct.getText().toString(),
                            categoryPopup.getText().toString(),
                            markProduct.getSelectedItem().toString(),
                            modelProduct.getSelectedItem().toString(),
                            unitProduct.getSelectedItem().toString(),
                            quantityP.getText().toString(),
                            idModelMS
                    );


                    ArrayAdapter adapterSearch = null;
                    final ArrayList<String> product = new ArrayList<String>();
                    adapterSearch = new ArrayAdapter(productsOfListActivity.this, android.R.layout.simple_list_item_1, product);
                    listViewSearch.setAdapter(adapterSearch);

                    List<ProductMicroService> productIntoList = dbHandlerProduct.getAllProducts(idListMS);
                    CustomListAdapterListProduct adapter = new CustomListAdapterListProduct(this, productIntoList);

                    listViewProducts.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    myDialog.hide();
                }
                break;

            case R.id.linearLayoutModeCourse:
                Intent intent = new Intent(productsOfListActivity.this, modecourseActivity.class);
                startActivity(intent);
                finish();

                break;
            case R.id.modecourse:
                Intent intentModecourse = new Intent(productsOfListActivity.this, modecourseActivity.class);
                startActivity(intentModecourse);
                finish();
                break;
            case R.id.btnVoice:
                Intent intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH);
                try {
                    startActivityForResult(intentVoice, 200);
                } catch (ActivityNotFoundException ac) {
                    Toast.makeText(getApplicationContext(), "Intent problem", Toast.LENGTH_SHORT).show();
                }
                break ;
            case R.id.lin_historic :
                Intent intentHistoric = new Intent(getApplicationContext(), historicSingleListActivity.class) ;
                intentHistoric.putExtra("idList",idListMS) ;
                intentHistoric.putExtra("nameList",nameList) ;
                startActivity(intentHistoric);
                break ;
            case R.id.LinearLayoutAnalyse:
                Intent intentAnalyse = new Intent(getApplicationContext(), analyseActivity.class) ;
                intentAnalyse.putExtra("idList",idListMS) ;
                intentAnalyse.putExtra("nameList",nameList) ;
                startActivity(intentAnalyse);
                break ;
    }
}


    public void getMarkbyModel(){
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idModel", idModelMS);
            jsonBodyObj.put("idProduct", idProductMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/getMarkByModel",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        idMark = new JSONArray();
                        AllMark.clear();
                        AllMark.add("Select a Mark");


                        try {
                            JSONObject jsonObj = null;
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArr = new JSONArray(jsonObject.getString("Mark"));

                            for (int i = 0; i < jsonArr.length(); i++) {

                                jsonObj = jsonArr.getJSONObject(i);
                                idMark.put(jsonObj.getString("markId"));
                                AllMark.add(jsonObj.getString("markName"));

                            }

                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("idModel", idModelMS);
                params.put("idProduct", idProductMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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

    public void getModelNamebyMark(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idMark", idMarkMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/searchingMarkIdV2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        idModel = new JSONArray();
                        AllModel.clear();
                        AllModel.add("Select a Model");
                        try {
                            JSONObject jsonObj = null;
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArr = new JSONArray(jsonObject.getString("Model"));

                            for (int i = 0; i < jsonArr.length(); i++) {

                                jsonObj = jsonArr.getJSONObject(i);
                                idModel.put(jsonObj.getString("idModel"));
                                AllModel.add(jsonObj.getString("nameModel"));

                            }
                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idMark", idMarkMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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

    public void getModelName(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idProduct", idProductMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/getModelById",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        idModel = new JSONArray();
                        AllModel.clear();
                        AllModel.add("Select a Model");
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                                JSONArray jsonArr = new JSONArray(jsonObject.getString("Model"));

                                for (int i = 0; i < jsonArr.length(); i++) {

                                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                                    idModel.put(jsonObj.getString("idModel"));
                                    AllModel.add(jsonObj.getString("nameModel"));
                            }
                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("idMark", idMark);
                params.put("idProduct", idProductMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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
    public void getUnitsOfModelFromMicroService(final String idModel){
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idModel", idModel);
             jsonBodyObj.put("idProduct", idProductMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/getUnitModel",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = null;
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArr = new JSONArray(jsonObject.getString("Unit"));
                            JSONArray jsonUnit = new JSONArray();

                            AllUnit.add(unit);

                            for (int i = 0; i < jsonArr.length(); i++) {
                                jsonObj = jsonArr.getJSONObject(i);
                                jsonUnit = new JSONArray(jsonObj.getString("unit"));

                                for (int j = 0; j < jsonUnit.length(); j++) {
                                    if(!unit.equals(jsonUnit.get(j).toString())){
                                        AllUnit.add(jsonUnit.get(j).toString());
                                    }
                                }
                            }

                            UpdateProduct(productsOfListActivity.this);
                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idModel", idModel);
                params.put("idProduct", idProductMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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

    public void getUnitName(final String idModelSelect){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idModel", idModelSelect);
            jsonBodyObj.put("idProduct", idProductMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/getUnitModel",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //idModel = new JSONArray();
                        try {
                            JSONObject jsonObj = null;
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArr = new JSONArray(jsonObject.getString("Unit"));
                            JSONArray jsonUnit = new JSONArray();

                            AllUnit.clear();
                            AllUnit.add("Select a Unit");
                            for (int i = 0; i < jsonArr.length(); i++) {

                                jsonObj = jsonArr.getJSONObject(i);
                                jsonUnit = new JSONArray(jsonObj.getString("unit"));

                                for (int j = 0; j < jsonUnit.length(); j++) {
                                    AllUnit.add(jsonUnit.get(j).toString());
                                }
                            }
                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idModel", idModelSelect);
                params.put("idProduct", idProductMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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

    public void ShowPopupAdd(String name) {

        TextView txtclose, categorie;
        Button cancel_btn;
        myDialog.setContentView(R.layout.add_product_to_list);
        txtclose =(TextView) myDialog.findViewById(R.id.close);
        categorie = myDialog.findViewById(R.id.category);


        categorie.setText(categoryProduct);
        cancel_btn = myDialog.findViewById(R.id.cancel_btn);
        final Spinner spinnerMark = myDialog.findViewById(R.id.Mark);

        final Spinner spinnerModel =  myDialog.findViewById(R.id.Model);

        final Spinner spinnerUnit =  myDialog.findViewById(R.id.Unit);


        spinnerMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(arg2 != 0) {
                        idMarkMS = (String) idMark.get(arg2 - 1);

                            spinnerModel.post(new Runnable() {
                                @Override
                                public void run() {
                                    // spinnerModel.setSelection(0);
                                    getModelNamebyMark();

                                }
                            });

                    }
                    if(arg2 == 0) {

                        getModelName();
                        spinnerModel.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerModel.setSelection(0);
                                // getModelNamebyMark();

                            }
                        });
                        spinnerUnit.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerUnit.setSelection(0);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(arg2 != 0) {
                        idModelMS = (String) idModel.get(arg2 - 1);
                        getUnitName((String) idModel.get(arg2 - 1));
                        if(spinnerMark.getSelectedItemPosition() == 0){
                            getMarkbyModel();

                            spinnerMark.post(new Runnable() {
                                @Override
                                public void run() {
                                    spinnerMark.setSelection(1);
                                }
                            });
                        }

                        spinnerUnit.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerUnit.setSelection(0);
                            }
                        });
                    }
                    if(arg2 == 0) {

                        AllUnit.clear();
                        AllUnit.add("Select a Unit");
                        spinnerUnit.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerUnit.setSelection(0);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AllMark);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMark.setAdapter(arrayAdapter1);

        arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AllModel);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModel.setAdapter(arrayAdapter2);

        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AllUnit);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(arrayAdapter3);

        final TextView nam=(TextView)myDialog.findViewById(R.id.nameproduct);
        Button add=(Button)myDialog.findViewById(R.id.addtolist);


        add.setOnClickListener(this);
        nam.setText(name);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }




    public void getFieldOfProduct(){


        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idProduct", idProductMS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();


        allfirstModel = new ArrayList<>();


        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.Product + "/searchingMarkV2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        idMark = new JSONArray();
                        try {
                            JSONObject jsonObj ;
                            JSONObject jsonObject = new JSONObject(response);

                                JSONArray jsonArr = new JSONArray(jsonObject.getString("Mark"));


                                for (int i = 0; i < jsonArr.length(); i++) {

                                    jsonObj = jsonArr.getJSONObject(i);
                                    if (!AllMark.contains(jsonObj.getString("markName")))
                                    {
                                        AllMark.add(jsonObj.getString("markName"));
                                        idMark.put(jsonObj.getString("markId"));
                                    }



                                }
                            ShowPopupAdd(nameProductMS);

                            getModelName();
                        } catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idProduct", idProductMS);
                return super.getParams();
            }

            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "hjm3SE9rhH6I8VB9jx3Roz6uP9r6tghn");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        idProductMS = "";
        nameProductMS = "";
        try {
            idProductMS = (String) IdProductMicroService.get(position);
            nameProductMS = (String) nameProductMicroService.get(position);
            categoryProduct = (String) categoryProductMicroService.get(position);
        }catch (JSONException e){

        }
            AllMark.clear(); AllModel.clear();AllUnit.clear();
            AllMark.add("Select a Mark");
            AllModel.add("Select a Model");
            AllUnit.add("Select a Unit");
            getFieldOfProduct();

    }
}