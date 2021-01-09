package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class updateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private HashMap<String, String> user ;
    private ImageView back  ;
    private ProgressDialog progressDialog ;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button update, update_password;
    private SessionManager sessionManager ;
    private EditText  editTextFirstName, editTextLastName, editTexttel ,
                zipCodeEditText;
    private TextView editDateOfBirth ;
    private String  governorate, delegation , city , firstName , lastName , phoneNumber , dateOfBirth;
    private ArrayList<String> governorates, delegations , cities ;
    private Spinner governorateSpinner, delegationSpinner ,citySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateprofile);

        // hide App Title Bar
        getSupportActionBar().hide();

        // Progress Dialog
        progressDialog = new ProgressDialog(this) ;

        // Image View
        back = (ImageView) findViewById(R.id.back_profile);

        // EditTexts
        editTextFirstName = findViewById(R.id.update_firstname);
        editTextLastName = findViewById(R.id.update_lastname);
        editTexttel = findViewById(R.id.update_tel);
        editDateOfBirth = (TextView)findViewById(R.id.update_dateOfBirth);
        zipCodeEditText = findViewById(R.id.zipCode) ;

        // spinners
        governorateSpinner = (Spinner) findViewById(R.id.governorate);
        delegationSpinner = (Spinner) findViewById(R.id.delegation);
        citySpinner = (Spinner) findViewById(R.id.city);

        // Buttons
        update = findViewById(R.id.btn_update);
        update_password = findViewById(R.id.password_update);

        //setOnClickListener
        back.setOnClickListener(this);
        update.setOnClickListener(this);
        update_password.setOnClickListener(this);
        editDateOfBirth.setOnClickListener(this);

        //Date Of birth
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                editDateOfBirth.setText(date);
            }
        };

        // function to all values from session
        getData();

        // get all governorate
        getAllGov();


        // Spinners setOnItemSelected
        governorateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                governorate = governorateSpinner.getItemAtPosition(governorateSpinner.getSelectedItemPosition()).toString();
                if(governorateSpinner.getSelectedItemPosition() != 0)
                    getDelegations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        delegationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                delegation = delegationSpinner.getItemAtPosition(delegationSpinner.getSelectedItemPosition()).toString();
                if(delegationSpinner.getSelectedItemPosition() != 0)
                    getCity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = citySpinner.getItemAtPosition(citySpinner.getSelectedItemPosition()).toString();
                if(citySpinner.getSelectedItemPosition() != 0)
                    getZipCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        // end of onCreate .
    }

    // check fields
    public Boolean checkFields(){

         firstName = editTextFirstName.getText().toString().trim() ;
         lastName = editTextLastName.getText().toString().trim() ;
         phoneNumber = editTexttel.getText().toString().trim() ;
         dateOfBirth = editDateOfBirth.getText().toString().trim() ;

        if (firstName.isEmpty()) {
            editTextFirstName.setError("First Name Required");
            editTextFirstName.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            editTextLastName.setError("Last Name Required");
            editTextLastName.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            editTexttel.setError("phone Number Required");
            editTexttel.requestFocus();
            return false;
        }

        if (dateOfBirth.isEmpty()) {
            editDateOfBirth.setError("date of birth Required");
            editDateOfBirth.requestFocus();
            return false;
        }

        if (phoneNumber.length() != 8) {
            editTexttel.setError("phone Number should be 8 numbers");
            editTexttel.requestFocus();
            return false;
        }

        if(governorateSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(updateProfileActivity.this, "select governorate", Toast.LENGTH_LONG).show();
            return false;
        }

        if(delegationSpinner.getSelectedItemPosition() ==0){

            Toast.makeText(updateProfileActivity.this, "select delegation", Toast.LENGTH_LONG).show();
            return false;
        }

        if(citySpinner.getSelectedItemPosition() ==0){

            Toast.makeText(updateProfileActivity.this, "select city", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void getData(){
        sessionManager = new SessionManager(updateProfileActivity.this) ;
        user= sessionManager.getUserDetail();
        editTextFirstName.setText(user.get(sessionManager.FIRSTNAME));
        editTextLastName.setText(user.get(sessionManager.LASTNAME));
        editTexttel.setText(user.get(sessionManager.TEL));
        editDateOfBirth.setText(user.get(sessionManager.DATEOFBIRTH));
        zipCodeEditText.setText(user.get(sessionManager.ZIPCODE));
    }

    // function to send Updated value into Micro Service
    public void update()
    {
        final String email = user.get(sessionManager.EMAIL);
        final String zipcode = zipCodeEditText.getText().toString().trim();

        JSONObject jsonBodyObj = new JSONObject();
        try{

            jsonBodyObj.put("firstName", firstName);
            jsonBodyObj.put("lastName", lastName);
            jsonBodyObj.put("phoneNumber", phoneNumber);
            jsonBodyObj.put("dateOfBirth", dateOfBirth);
            jsonBodyObj.put("governorate", governorate);
            jsonBodyObj.put("city", city);
            jsonBodyObj.put("delegation", delegation);
            jsonBodyObj.put("zipCode", zipcode);
        }catch (JSONException e){
            e.printStackTrace();
        }

        final String requestBody = jsonBodyObj.toString();

        StringRequest stringrequest = new StringRequest(Request.Method.PUT, HostName_Interface.client + "/user/update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.print(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String isUpdated =  jsonObject.getString("isUpdated");
                            if (isUpdated.equals("true")){
                                String token = user.get(sessionManager.TOKEN);
                                String image_profile = user.get(sessionManager.IMG);
                                sessionManager.editor.clear();
                                sessionManager.createSession( firstName,email , lastName,token,phoneNumber, dateOfBirth, governorate , city , delegation , zipcode, image_profile);
                                sessionManager.editor.commit();
                                progressDialog.hide();
                                Intent i = new Intent(updateProfileActivity.this, profileActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(updateProfileActivity.this,"Update failed !",Toast.LENGTH_LONG).show();
                            }

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
                Map<String,String>params =new HashMap<>();

                params.put("firstName",firstName);
                params.put("lastName",lastName);
                params.put("phoneNumber",phoneNumber);
                params.put("dateOfBirth",dateOfBirth);
                params.put("governorate", governorate);
                params.put("city", city);
                params.put("delegation", delegation);
                params.put("zipCode", zipcode);

                return super.getParams();
            }
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json; charset=UTF-8");
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



    // functions to get all governorates
    public void getAllGov(){

        JSONObject jsonBodyObj = new JSONObject();
        final String requestBody = jsonBodyObj.toString();
        StringRequest Stringrequest = new StringRequest(Request.Method.GET, HostName_Interface.client + "/address/Gov",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jArray = new JSONArray(response);
                            governorates = new ArrayList<>();
                            governorates.add("select governorate");
                            int index = 0 ;
                            for(int i=0;i<jArray.length();i++){
                                JSONObject govJsonObject = jArray.getJSONObject(i);
                                String gov =govJsonObject.getString("governorate");
                                if (gov.equals(user.get(sessionManager.GOVERNORATE)))
                                    index = i+1 ;
                                governorates.add(gov);
                            }
                            governorateSpinner.setAdapter(new ArrayAdapter<String>(updateProfileActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, governorates));
                            // check there is there is already a governorate
                            if (index != 0)
                            {
                                governorateSpinner.setSelection(index);
                                getDelegations();
                            }


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

    // function to get all delegations with one params in the body governorate
    public void getDelegations(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("governorate", governorate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/address/deleg",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.print(response);
                        delegations = new ArrayList<>();
                        delegations.add("select Delegation");
                        int index = 0 ;
                        try {
                            JSONArray jArray = new JSONArray(response);
                            for(int i=0;i<jArray.length();i++){
                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String deleg=jsonObject.getString("delegation");
                                if (deleg.equals(user.get(sessionManager.DELEGATION)))
                                    index = i +1 ;
                                delegations.add(deleg);
                            }
                            delegationSpinner.setAdapter(new ArrayAdapter<String>(updateProfileActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, delegations));
                            if (index !=0)
                            {
                                delegationSpinner.setSelection(index);
                                getCity();
                            }

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("governorate", governorate);
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

    // function to get city with tow params in the body (governorate , delegation )
    public void getCity(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("delegation", delegation);
            jsonBodyObj.put("governorate", governorate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/address/city",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cities = new ArrayList<>();
                        cities.add("select city");
                        int index = 0 ;
                        try {
                            JSONArray jArray = new JSONArray(response);
                            for(int i=0;i<jArray.length();i++){
                                JSONObject jsonObject1 = jArray.getJSONObject(i);
                                String ci = jsonObject1.getString("city");
                                if (ci.equals(user.get(sessionManager.CITY)))
                                    index = i + 1 ;
                                cities.add(ci);
                            }
                            citySpinner.setAdapter(new ArrayAdapter<String>(updateProfileActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, cities));
                            if(index != 0)
                            {
                                citySpinner.setSelection(index);
                                getZipCode();
                            }
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
                params.put("delegation", delegation);
                params.put("governorate", governorate);
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

    // function to get zipCode input ( governorate , delegation , city )
    public void getZipCode(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("governorate", governorate);
            jsonBodyObj.put("delegation", delegation);
            jsonBodyObj.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/address/zipCode",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jArray = new JSONArray(response);
                            String zip = jArray.getJSONObject(0).getString("zipCode");
                            zipCodeEditText.setText(zip);
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
                params.put("governorate",governorate);
                params.put("delegation", delegation);
                params.put("city", city);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_profile :
                onBackPressed();
                break ;
            case R.id.btn_update :
                if(checkFields()){
                    progressDialog.setMessage("Loading ...");
                    progressDialog.show();
                    update();
                }
                break ;
            case R.id.password_update :
                Intent i = new Intent(updateProfileActivity.this, updatePasswordActivity.class);
                updateProfileActivity.this.startActivity(i);
                break ;
            case R.id.update_dateOfBirth :
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        updateProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break ;
        }
    }
}



