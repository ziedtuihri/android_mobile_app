package com.example.myapplication;



import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class mainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener  {


    private LinearLayout linearLayoutProfile , linearLayoutLists , linearLayoutGroupes , linearLayoutHistorique ;
    private SessionManager sessionManager;
    private Dialog myDialog;
    private DeviceSession deviceSession;
    private int userId, userDevice;
    private ArrayList<listt> lists = new ArrayList<listt>();
    private MyDbHandlerList myDbHandlerList;
    private GridView gridView;
    private CustomListofListAsapter adapter;
    private Toolbar toolbar ;
    private String  codeimei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        // LinearLayout
        linearLayoutProfile = findViewById(R.id.ic_profile);
        linearLayoutLists = findViewById(R.id.listes);
        linearLayoutGroupes = findViewById(R.id.groupes);
        linearLayoutHistorique = findViewById(R.id.histori);

        // setOnclickListener
        linearLayoutProfile.setOnClickListener(this);
        linearLayoutLists.setOnClickListener(this);
        linearLayoutGroupes.setOnClickListener(this);
        linearLayoutHistorique.setOnClickListener(this);

        // SessionManager
        sessionManager =new SessionManager(this);

        // device session to get id user and id device
        deviceSession = new DeviceSession(this);
        userId = 1; //Integer.parseInt(deviceSession.sharedPreferences.getString("userId","0"));
        userDevice = 1; // deviceSession.sharedPreferences.getInt("deviceId", 0);
        codeimei = "1"; // deviceSession.sharedPreferences.getString("imei", "0");

        myDbHandlerList = new MyDbHandlerList(this);
        toolbar  = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ScrollView mainScrollView = findViewById(R.id.scroll);

        gridView = findViewById(R.id.listoflist);
        adapter = new CustomListofListAsapter(this, lists);
        gridView.setAdapter(adapter);

        // get all lists from Microservice
        getLists();

        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                listt pro=(listt) arg0.getItemAtPosition(pos);
                ShowPopupDelete (pro.getlistMS());
                return true;
            }
        });

        myDialog = new Dialog(this);
        //mainScrollView.fullScroll(ScrollView.FOCUS_UP);

        ImageView menu = findViewById(R.id.menu);
        ImageView notification = findViewById(R.id.notification);
        menu.setOnClickListener(this);
        notification.setOnClickListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // end of onCreate ................................
    }

    // function to use on Delete and Create list
    public void getListsFromLocalDb()
    {
        lists.clear();
        listt list = new listt();
        list.setName("ADD NEW LIST");
        list.setListId(999);
        lists.add(list);
        getListsFromLocalDatabase() ;

    }

    // on setOnItemLongClickListener
    public void ShowPopupDelete(final String id) {

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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DELETE LOCAL
                myDbHandlerList.deleteListMS(id);
                deleteList(id);
                myDialog.dismiss();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    // this is function to add list
    public void ShowPopupAddList() {

        myDialog.setContentView(R.layout.activity_create__list);

        TextView textViewClose = myDialog.findViewById(R.id.close);
        Button btnCreateList = myDialog.findViewById(R.id.button_create);

        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextNameList = myDialog.findViewById(R.id.List_name);
                String nameList =editTextNameList.getText().toString();
                if (nameList.isEmpty())
                {
                    editTextNameList.setError("name of the list must not be empty");
                    editTextNameList.requestFocus() ;
                    return;
                }
                createList_Microservice(nameList);
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }



    public void createList_Microservice(final String nameList){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("nameList", nameList);
            jsonBodyObj.put("codeImei", "1");
            jsonBodyObj.put("idUser", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/addList",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String IdListMS = jsonObject.getString("idList");
                            myDbHandlerList.addList(new listt(nameList,userDevice,userId,IdListMS,codeimei));
                            getListsFromLocalDb() ;
                            myDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();

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

                params.put("nameList", nameList);
                params.put("codeImei", "1");
                params.put("idUser", "1");
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
    public void deleteList(final String id){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/deletelist",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        getListsFromLocalDb() ;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idList", id);
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // button click slide bar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_historique) {
            Intent intentMain = new Intent(mainActivity.this , historiquelistActivity.class);
            mainActivity.this.startActivity(intentMain);

        } else if (id == R.id.nav_profile) {

            if(sessionManager.isLoggin()) {
                Intent intentMain = new Intent(mainActivity.this, profileActivity.class);
                mainActivity.this.startActivity(intentMain);
            }
            else{
                Intent intentMain = new Intent(mainActivity.this, loginActivity.class);
                mainActivity.this.startActivity(intentMain);
            }

        }  else if (id == R.id.nav_Groups) {

            if(sessionManager.isLoggin()) {
                Intent intentMain = new Intent(mainActivity.this , GroupsActivity.class);
                mainActivity.this.startActivity(intentMain);
            }
            else{
                Intent intentMain = new Intent(mainActivity.this, loginActivity.class);
                mainActivity.this.startActivity(intentMain);
                Toast.makeText(mainActivity.this,"To aceess to the groups you have to login "  ,Toast.LENGTH_LONG).show();

            }

        } else if (id == R.id.nav_pointOfSale)
        {
            Intent intent = new Intent(this , addPosActivity.class) ;
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getListsFromLocalDatabase(){

        ArrayList<listt> list = myDbHandlerList.getList(userId);
        for(int i = 0;i < list.size();i++) {
            lists.add(list.get(i));
        }
        adapter.notifyDataSetChanged();
    }


    // first function called
    public void getLists(){

        lists.clear();
        listt lis = new listt();
        lis.setName("ADD NEW LIST");
        lis.setListId(999);
        lists.add(lis);
        adapter.notifyDataSetChanged();
        // check if the user is connected
        if(userId != 0){
             getListByImeiForUpdate();
        }else {
            getListByImei();
        }

    }

    public void getListByUser(){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idUser", String.valueOf(userId));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/list/getListByUser",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("found")){

                                    JSONArray jArray = new JSONArray(jsonObject.getString("list"));
                                    myDbHandlerList.delete();

                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject jsonObj = jArray.getJSONObject(i);
                                        String IMEIMS = jsonObj.getString("idDevice");
                                        String IDLIST = jsonObj.getString("idList");
                                        String NAMELIST = jsonObj.getString("nameList");

                                        listt list = new listt(NAMELIST, userDevice, userId, IDLIST, IMEIMS) ;
                                        myDbHandlerList.addList(list);
                                        lists.add(list);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                }catch(Exception e){}
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getListsFromLocalDatabase();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idUser", String.valueOf(userId));
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



    //{
    //  "found": false,
    //  "list": [
    //    {
    //      "idDevice": "aaaa",
    //      "idList": "49e9864f1d6165e95090994ca932a5c5bacc507e2e47794a00723176532016e3",
    //      "idUser": "zzzz",
    //      "nameList": "aaa"
    //    }
    //  ]
    //}
    public void getListByImeiForUpdate(){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("codeImei", codeimei);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/list/getListByImei",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("found")){
                                   JSONArray jArray = new JSONArray(jsonObject.getString("list")) ;
                                    myDbHandlerList.delete();
                                    for(int i=0;i<jArray.length();i++){
                                        JSONObject jsonObj = jArray.getJSONObject(i);
                                        if(jsonObj.getString("idUser").equals("0")) {
                                            String IDLIST = jsonObj.getString("idList");
                                            String NAMELIST = jsonObj.getString("nameList");
                                            updateListMS(IDLIST, NAMELIST);
                                        }
                                    }
                                    getListByUser();
                                }
                            }catch(JSONException e){ }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getListsFromLocalDatabase();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("codeImei", codeimei);
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

    public void updateListMS(final String idList, final String nameList){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("idList", idList);
            jsonBodyObj.put("nameList", nameList);
            jsonBodyObj.put("idDevice", codeimei);
            jsonBodyObj.put("idUser", String.valueOf(userId));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.PUT, HostName_Interface.list + "/list/update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mainActivity.this, "update list error ", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idList", idList);
                params.put("nameList", nameList);
                params.put("idDevice", codeimei);
                params.put("idUser", String.valueOf(userId));
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

    public void getListByImei(){

        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("codeImei", codeimei);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.list + "/list/getListByImei",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("found")){
                                JSONArray jArray = new JSONArray(jsonObject.getString("list")) ;
                                myDbHandlerList.delete();
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject jsonObj = jArray.getJSONObject(i);
                                    int IDUSER = Integer.parseInt(jsonObj.getString("idUser"));
                                    if(IDUSER == 0) {
                                        String IDLIST = jsonObj.getString("idList");
                                        String NAMELIST = jsonObj.getString("nameList");
                                        listt newList = new listt(NAMELIST, userDevice, userId, IDLIST, codeimei) ;
                                        myDbHandlerList.addList(newList);
                                        lists.add(newList);
                                    } } } }
                            catch (JSONException e) { }

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
                params.put("codeImei", codeimei);
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

            case R.id.ic_profile:
                if(sessionManager.isLoggin()) {
                    Intent intentMain = new Intent(mainActivity.this, profileActivity.class);
                    mainActivity.this.startActivity(intentMain);
                }
                else{
                    Intent intentMain = new Intent(mainActivity.this, loginActivity.class);
                    mainActivity.this.startActivity(intentMain);
                }

                break;

            case R.id.histori:
                Intent i = new Intent(mainActivity.this , historiquelistActivity.class);
                mainActivity.this.startActivity(i);

                break;

            case R.id.groupes:
                if(sessionManager.isLoggin()) {
                    Intent intentMain = new Intent(mainActivity.this , GroupsActivity.class);
                    mainActivity.this.startActivity(intentMain);
                }
                else{
                    Intent intentMain = new Intent(mainActivity.this, loginActivity.class);
                    mainActivity.this.startActivity(intentMain);
                    Toast.makeText(mainActivity.this,"To aceess to your groups you have to login"  ,Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.listes:
                Intent it = new Intent(mainActivity.this , mainActivity.class);
                mainActivity.this.startActivity(it);
                break;

            case R.id.menu:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.notification:
                Intent nt = new Intent(mainActivity.this , notificationActivity.class);
                mainActivity.this.startActivity(nt);
                break;

            default:
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listt llist  = (listt) gridView.getItemAtPosition(position);
        if(llist.getListId() == 999){
            ShowPopupAddList();
        }else {

            Intent intent = new Intent(mainActivity.this, productsOfListActivity.class);
            intent.putExtra("title", llist.getName());
            intent.putExtra("listIdIntoMsList", llist.getlistMS());
            startActivity(intent);
        }
    }



}
