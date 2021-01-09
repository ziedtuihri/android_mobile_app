package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class updatePasswordActivity extends AppCompatActivity {


    private EditText editTextOldPassword, editTextnewPassword, editTextconfirmPassword;
    private Button updatePassword;
    private SessionManager sessionManager;
    private HashMap<String, String> user ;
    private ProgressDialog progressDialog ;
    private String URL_REGIST = HostName_Interface.client+"/user/updatePassword";
    private String oldPassword , newPassword , confirmPassword ;
    private ImageView imageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatepassword);

        // hide App Title Bar
        getSupportActionBar().hide();

        // ProgressDialog
        progressDialog = new ProgressDialog(this) ;

        imageView = findViewById(R.id.back_profile) ;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        // Session Manager
        sessionManager = new SessionManager(updatePasswordActivity.this);

        // Button
        updatePassword = findViewById(R.id.btn_updatePassword);

        // EditText
        editTextOldPassword = findViewById(R.id.update_odlPassword);
        editTextnewPassword = findViewById(R.id.update_newPassword1);
        editTextconfirmPassword = findViewById(R.id.update_newPassword2);

        user= sessionManager.getUserDetail();


        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkFields())
                {
                    progressDialog.setMessage("Loading ...");
                    progressDialog.show();
                    updatePassword();
                }

            }
        });
    }



    public void updatePassword(){

            JSONObject jsonBodyObj = new JSONObject();
            try {
                jsonBodyObj.put("oldPassword", oldPassword);
                jsonBodyObj.put("newPassword", newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBodyObj.toString();

            StringRequest stringrequest = new StringRequest(Request.Method.PUT, URL_REGIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg =  jsonObject.getString("msg");
                                if (msg.equals("password updated!")){

                                    Intent i = new Intent(updatePasswordActivity.this, profileActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(updatePasswordActivity.this,"wrong Password !",Toast.LENGTH_LONG).show();
                                }
                                progressDialog.hide();

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
                    Map<String,String>params =new HashMap<>();
                    params.put("oldPassword",oldPassword);
                    params.put("newPassword",newPassword);

                    return super.getParams();
                }
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("x-api-Key", "eiWee8ep9due4deeshoa8Peichai8Eih");
                    headers.put("x-access-token", user.get(sessionManager.TOKEN));
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
            requestQueue.add(stringrequest);

        }

    public boolean checkFields()
    {
        oldPassword = editTextOldPassword.getText().toString();
        newPassword = editTextnewPassword.getText().toString();
        confirmPassword = editTextconfirmPassword.getText().toString();

        if (oldPassword.isEmpty()) {
            editTextOldPassword.setError("Empty Password");
            editTextOldPassword.requestFocus();
            return false;
        }

        if (oldPassword.length() < 6) {
            editTextOldPassword.setError("Password should be at least 6 character long ");
            editTextOldPassword.requestFocus();
            return false;
        }

        if (newPassword.isEmpty()) {
            editTextnewPassword.setError("Empty Password");
            editTextnewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            editTextnewPassword.setError("Password should be at least 6 character long  ");
            editTextnewPassword.requestFocus();
            return false ;
        }

        if (confirmPassword.isEmpty()) {
            editTextconfirmPassword.setError("Empty Password");
            editTextconfirmPassword.requestFocus();
            return false ;
        }

        if (confirmPassword.length() < 6) {
            editTextconfirmPassword.setError("Password should be at least 6 character long  ");
            editTextconfirmPassword.requestFocus();
            return false;
        }

        if(!newPassword.equals(confirmPassword)){
            editTextconfirmPassword.setError("Check new  Password");
            editTextconfirmPassword.requestFocus();
            return false ;
        }

        progressDialog.hide();
        return true ;
    }

}
