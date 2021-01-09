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

public class validateForgetPasswordActivity extends AppCompatActivity {
    private Button btnConfirm ;
    private EditText editTextCode ;
    private String code , email  ;
    private  Intent intent ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.validation_code);

        // hide App Title Bar
        getSupportActionBar().hide();

        // get email
        intent = getIntent() ;
        email = intent.getStringExtra("email") ;
        btnConfirm = findViewById(R.id.btnSend) ;
        editTextCode = findViewById(R.id.code) ;

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = editTextCode.getText().toString().trim() ;
                if (code.isEmpty()) {
                    editTextCode.setError("code is required");
                    editTextCode.requestFocus();
                    return;
                }
                btnConfirm.setEnabled(false);
                send() ;

            }
        });
    }

    public void send()
    {

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("email", email);
            jsonBodyObj.put("validateCode", code);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client+"/user/validateCodeForget",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean msg = jsonObject.getBoolean("msg");
                            if (msg)
                            {
                                Intent intent =new Intent(validateForgetPasswordActivity.this, updateForgetPasswordActivity.class) ;
                                intent.putExtra("email",email) ;
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                editTextCode.setError("wrong code");
                                editTextCode.requestFocus ();
                                btnConfirm.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(validateForgetPasswordActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(validateForgetPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("validateCode", code);
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
