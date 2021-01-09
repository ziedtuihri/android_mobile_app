package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class mindLogoActivity extends AppCompatActivity {

    private String imei ;
    private static String insertDevice=HostName_Interface.client+"/addDevice";
    DeviceSession deviceSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mind_logo);

        // get Device session to check if this is the first time the user open the app or no
        // why because we want to insert code imei of the user in the database , but only on the first time its useless to send a request
        // every time the user opens the app
        deviceSession  = new DeviceSession(getApplicationContext()) ;
        if (!deviceSession.checkInsertedDevice()) {
            // check the permission
            checkPermission();
        }
        else {
            Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mindLogoActivity.this, smartLogoActivity.class));
                    finish();
                }
            },1500);
        }

    }

    public void insertToDevice()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("codeImei", imei);
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, insertDevice,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                             deviceSession.createDeviceSession(imei,"0");
                        }
                        catch (Exception e){

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params =new HashMap<>();
                params.put("codeImei",imei);
                return super.getParams();
            }
            public Map getHeaders() throws AuthFailureError
            {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "eiWee8ep9due4deeshoa8Peichai8Eih");
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
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(Stringrequest);

    }

    public static String getUniqueIMEIId(Context context) {
        try {

            // Provides access to information about the telephony services on the device
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // get imei Number
            // Returns the unique device ID, for example,
            // the IMEI for GSM and the MEID or ESN for CDMA phones.
            // Return null if device ID is not available.
            // Note we can use getImei() but our minimum api level is 23
            String imei = telephonyManager.getDeviceId();

            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                // else will work only if there is no sim card so we can't get code imei
                // Gets the hardware serial number, if available.
                return android.os.Build.SERIAL;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "not_found";
    }

    public void checkPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        } else
        {
            imei = getUniqueIMEIId(this) ;
            //insert imei number to database
            insertToDevice();
            Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mindLogoActivity.this, smartLogoActivity.class));
                    finish();
                }
            },1500);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 2) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                imei = getUniqueIMEIId(this) ;
                //insert imei number to database
                insertToDevice();
                Handler handler =new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(mindLogoActivity.this, smartLogoActivity.class));
                        finish();
                    }
                },1500);

            } else {
                checkPermission();
            }
        }

    }
}

