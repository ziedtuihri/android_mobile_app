package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class validationCodeActivity extends AppCompatActivity {
    private Button btnSend ;
    private EditText editTextCode ;
    private String code , email , codeImei ;
    private  Intent intent ;
    private DeviceSession deviceSession;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.validation_code);
        sessionManager = new SessionManager(this);
        deviceSession = new DeviceSession(this);
        codeImei = deviceSession.sharedPreferences.getString("imei", "0");
        // get email
        intent = getIntent() ;
        email = intent.getStringExtra("email") ;
        btnSend = findViewById(R.id.btnSend) ;
        editTextCode = findViewById(R.id.code) ;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = editTextCode.getText().toString().trim() ;
                if (code.isEmpty()) {
                    editTextCode.setError("code is required");
                    editTextCode.requestFocus();
                    return;
                }
                btnSend.setEnabled(false);
                send() ;

            }
        });
    }

    public void send()
    {

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("email", email);
            jsonBodyObj.put("validationCode", code);
            jsonBodyObj.put("codeImei", codeImei);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client+"/user/validationCode",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean msg = jsonObject.getBoolean("msg");
                            if (msg)
                            {
                                String email = jsonObject.getString("email");
                                String img = jsonObject.getString("imageProfile");
                                String zipcode = jsonObject.getString("zipCode");
                                String delegation = jsonObject.getString("delegation");
                                String city = jsonObject.getString("city");
                                String governorate = jsonObject.getString("governorate");
                                String dateofbirth = jsonObject.getString("dateOfBirth");
                                String tel = jsonObject.getString("phoneNumber");
                                String lastname = jsonObject.getString("lastName");
                                String firstname = jsonObject.getString("firstName");
                                String token = jsonObject.getString("token");

                                sessionManager.createSession(firstname, email,  lastname , token , tel , dateofbirth,  governorate ,  city ,  delegation ,  zipcode, img );
                                Intent intent =new Intent(validationCodeActivity.this, mainActivity.class) ;
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                editTextCode.setError("wrong code");
                                editTextCode.requestFocus ();
                                btnSend.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(validationCodeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(validationCodeActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("validationCode", code);
                params.put("codeImei", codeImei);
                return super.getParams();
            }
            public Map getHeaders() throws AuthFailureError {
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(Stringrequest);

    }
}
