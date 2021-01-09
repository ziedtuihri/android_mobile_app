package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class updateForgetPasswordActivity extends AppCompatActivity {
    private Button btnConfirm ;
    private EditText newPassword , confirmPassword ;
    private String newPassText , confirmPassText , email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateforgetpassword);

        // hide App Title Bar
        getSupportActionBar().hide();

        btnConfirm = findViewById(R.id.btnConfirm) ;
        newPassword = findViewById(R.id.newPassword) ;
        confirmPassword = findViewById(R.id.confirmPassword) ;

        Intent intent = getIntent() ;
        email = intent.getStringExtra("email") ;

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPassText = newPassword.getText().toString();
                confirmPassText = confirmPassword.getText().toString();

                if(!newPassText.equals(confirmPassText)){
                    confirmPassword.setError("Check new  Password");
                    confirmPassword.requestFocus();
                    return; }

                if (newPassText.length() < 6) {
                    newPassword.setError("Password should be at least 6 character long  ");
                    newPassword.requestFocus();
                    return;
                }
                if (confirmPassText.length() < 6) {
                    confirmPassword.setError("Password should be at least 6 character long  ");
                    confirmPassword.requestFocus();
                    return;
                }
                send();
            }

        });
    }

    public void send()
    {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("email", email);
            jsonBodyObj.put("newPassword", newPassText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.PUT, HostName_Interface.client+"/user/updateForgetPassword",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.print(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.print(response);
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("password updated"))
                            {
                                Intent intent = new Intent(updateForgetPasswordActivity.this, loginActivity.class) ;
                                startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(codeActivity.this, "login Error" + error.getStackTrace().toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("newPassword", newPassText);
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
