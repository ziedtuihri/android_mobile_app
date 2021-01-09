package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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


public class signUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back ;
    private Button btnSignUp ;
    private ProgressBar loading;
    private EditText editTextEmail, editTextFirstName, editTextLastName , editTextPhoneNumber , editTextPassword , zipCodeEditText;
    private TextView textViewDateOfBirth , login;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String email , firstName , lastName , phoneNumber, password , dateOfBirth, governorate, delegation , city, zipCode, gender;
    private ArrayList<String> governorates, delegations , cities , genders ;
    private Spinner governorateSpinner, delegationSpinner ,citySpinner , genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // ImageView
        back = findViewById(R.id.back) ;

        // TextViews
        textViewDateOfBirth=findViewById(R.id.dateOfBirth);
        login = findViewById(R.id.btnLogin); //link to login activity

        //progressBar
        loading=findViewById(R.id.loading);

        // arrayLists
        governorates = new ArrayList<>() ;
        delegations = new ArrayList<>() ;
        cities = new ArrayList<>() ;
        genders = new ArrayList<>() ;

        // spinners
        governorateSpinner = (Spinner) findViewById(R.id.governorate);
        delegationSpinner = (Spinner) findViewById(R.id.delegation);
        citySpinner = (Spinner) findViewById(R.id.city);
        genderSpinner = (Spinner) findViewById(R.id.gender);

        // editTexts
        editTextEmail = findViewById(R.id.email);
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);
        editTextPassword = findViewById(R.id.password);
        zipCodeEditText = findViewById(R.id.zipCode) ;


        // buttons
        btnSignUp = findViewById(R.id.btnSignUp);

        // onClickListner
        login.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        back.setOnClickListener(this);

        // Date
        textViewDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        signUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                textViewDateOfBirth.setText(date);
            }
        };

        // add female and male to gender Spinner
        genders.add("Select Gender");
        genders.add("female");
        genders.add("male");

        genderSpinner.setAdapter(new ArrayAdapter<String>(signUpActivity.this, android.R.layout.simple_spinner_dropdown_item, genders));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genderSpinner.getItemAtPosition(genderSpinner.getSelectedItemPosition()).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // get all governorates
        getAllGov();

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
                            governorates.add("select governorate");
                            for(int i=0;i<jArray.length();i++){
                                JSONObject govJsonObject = jArray.getJSONObject(i);
                                String gov =govJsonObject.getString("governorate");
                                governorates.add(gov);
                            }
                            governorateSpinner.setAdapter(new ArrayAdapter<String>(signUpActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, governorates));
                        } catch (JSONException e) {
                            loading.setVisibility(View.GONE);
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

    // function to get all delegations input ( governorate )
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
                        try {
                            JSONArray jArray = new JSONArray(response);
                            for(int i=0;i<jArray.length();i++){
                                JSONObject jsonObject = jArray.getJSONObject(i);
                                String deleg=jsonObject.getString("delegation");
                                delegations.add(deleg);
                            }
                            delegationSpinner.setAdapter(new ArrayAdapter<String>(signUpActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, delegations));
                        } catch (JSONException e) {
                            loading.setVisibility(View.GONE);
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

    // function to get city input ( governorate , delegation )
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
                        try {
                            JSONArray jArray = new JSONArray(response);
                            for(int i=0;i<jArray.length();i++){
                                JSONObject jsonObject1 = jArray.getJSONObject(i);
                                String ci = jsonObject1.getString("city");
                                cities.add(ci);
                            }
                            citySpinner.setAdapter(new ArrayAdapter<String>(signUpActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, cities));
                        } catch (JSONException e) {
                            loading.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(signUpActivity.this, "login Error" + error.toString(), Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
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
                            loading.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(signUpActivity.this, "login Error" + error.toString(), Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
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

    private void singUp(){

        JSONObject jsonBodyObj = new JSONObject();

        try{
            jsonBodyObj.put("email", email);
            jsonBodyObj.put("firstName", firstName);
            jsonBodyObj.put("lastName", lastName);
            jsonBodyObj.put("phoneNumber", phoneNumber);
            jsonBodyObj.put("dateOfBirth", dateOfBirth);
            jsonBodyObj.put("governorate", governorate);
            jsonBodyObj.put("delegation", delegation);
            jsonBodyObj.put("city", city);
            jsonBodyObj.put("zipCode", zipCode);
            jsonBodyObj.put("password", password);
            jsonBodyObj.put("gender", gender);
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client+"/user/signUp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean check = jsonObject.getBoolean("check");
                            Boolean msgMail = jsonObject.getBoolean("msgMail");
                            if(check)
                            {
                                    if(msgMail){
                                    Intent intent =new Intent(signUpActivity.this, validationCodeActivity.class) ;
                                    intent.putExtra("email",email);
                                    startActivity(intent);
                                    finish();
                                }else{
                                        loading.setVisibility(View.GONE);
                                        // the user can click on button
                                        btnSignUp.setVisibility(View.VISIBLE);
                                        Toast.makeText(signUpActivity.this," email not found "  ,Toast.LENGTH_LONG).show();
                                }

                            }else{
                                loading.setVisibility(View.GONE);
                                btnSignUp.setVisibility(View.VISIBLE);
                                Toast.makeText(signUpActivity.this," email address is already registred "  ,Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(signUpActivity.this, e.toString() ,Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            btnSignUp.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(signUpActivity.this,"ERROR Registed" + error.getMessage()  ,Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.VISIBLE);
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params =new HashMap<>();
                params.put("firstname",firstName);
                params.put("lastName",lastName);
                params.put("email",email);
                params.put("phoneNumber",phoneNumber);
                params.put("dateOfBirth",dateOfBirth);
                params.put("governorate", governorate);
                params.put("city", city);
                params.put("delegation", delegation);
                params.put("zipCode", zipCode);
                params.put("password", password);
                params.put("gender", gender);
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
                    VolleyLog.wtf(" ",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Stringrequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(Stringrequest);

    }

    private boolean checkFields() {

         email = editTextEmail.getText().toString().trim();
         firstName = editTextFirstName.getText().toString().trim();
         phoneNumber = editTextPhoneNumber.getText().toString().trim();
         lastName = editTextLastName.getText().toString().trim();
         dateOfBirth= textViewDateOfBirth.getText().toString().trim();
         password= editTextPassword.getText().toString().trim();
         zipCode = zipCodeEditText.getText().toString().trim() ;


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

        if (email.isEmpty()) {
            editTextEmail.setError("Email Required");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }

        if(dateOfBirth.isEmpty()){
            textViewDateOfBirth.setError("Date of birth required");
            textViewDateOfBirth.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Phone Number Required");
            editTextPhoneNumber.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password should be at least 6 characters");
            editTextPassword.requestFocus();
            return false;
        }

        if(genderSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(signUpActivity.this, "select Gender", Toast.LENGTH_LONG).show();
            return false;
        }

        if(governorateSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(signUpActivity.this, "select governorate", Toast.LENGTH_LONG).show();
            return false;
        }

        if(delegationSpinner.getSelectedItemPosition() ==0){

            Toast.makeText(signUpActivity.this, "select delegation", Toast.LENGTH_LONG).show();
            return false;
        }

        if(citySpinner.getSelectedItemPosition() ==0){

            Toast.makeText(signUpActivity.this, "select city", Toast.LENGTH_LONG).show();
            return false;
        }

        if (zipCode.isEmpty())
        {
            Toast.makeText(signUpActivity.this, "select city", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLogin:
                Intent intentMain = new Intent(signUpActivity.this, loginActivity.class);
                signUpActivity.this.startActivity(intentMain);
                break;

            case R.id.btnSignUp:
                if (checkFields()) {
                    // user can't click multiple times
                    btnSignUp.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    singUp();
                }
                break;
            case R.id.back:
                onBackPressed();
                break;

            default:

        }
    }

}
