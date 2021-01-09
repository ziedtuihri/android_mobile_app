package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewsignUp;
    private EditText editTextEmail, editTextPassword;
    private CallbackManager callbackManager;
    private RequestQueue mQueue ;
    public SessionManager sessionManager;
    public DeviceSession deviceSession;
    private Button forgetPassword , btnLogin;
    private LoginButton btnFb;
    private ProgressDialog pDialog;
    private String email, password;
    private SignInButton btnGmail;
    private GoogleSignInClient mGoogleSignInClient;
    private CheckBox remember ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // progressBar
        pDialog = new ProgressDialog(this);

        // checkBox
        remember = findViewById(R.id.remember) ;

        // RequestQueue
        mQueue = Volley.newRequestQueue(this);

        // Sessions
        deviceSession = new DeviceSession(this);
        sessionManager = new SessionManager(this);

        // Buttons
        btnLogin = findViewById(R.id.btnLogin);
        forgetPassword = findViewById(R.id.forgetPassword);

        // TextView
        textViewsignUp = findViewById(R.id.signUp);

        // EditText
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        // LoginButton
        btnFb = (LoginButton) findViewById(R.id.btnfb);

        // ImageView
        ImageView back = findViewById(R.id.back);

        // setOnClickListener
        textViewsignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        back.setOnClickListener(this);


        // Remeber me
        SharedPreferences prefs = getSharedPreferences("address", MODE_PRIVATE);
        String oldEmail = prefs.getString("email", null);
        String oldPassword = prefs.getString("password", null);
        if (oldEmail != null) {
            editTextEmail.setText(prefs.getString("email", oldEmail));
            editTextPassword.setText(prefs.getString("password", oldPassword));
        }

        // get Permission to get ( email )
        btnFb.setPermissions("email");
        callbackManager = CallbackManager.Factory.create();

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            // logout
            LoginManager.getInstance().logOut();
        }

        // Callback registration
        btnFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                pDialog.show();
                getUserProfile(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }

        });

        // login with gmail
        btnGmail = findViewById(R.id.btnGmail);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        btnGmail.setSize(SignInButton.SIZE_STANDARD);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // methode for intent to check your email.
        btnGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGmail();
            }
        });
        signOutGmail();

        // end of create
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        switch (requestCode) {
            case 101:
                try {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    onLoggedInGmail(account);
                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                }
                break;
        }
    }

    private void signInGmail() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }


    private void signOutGmail() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void onLoggedInGmail(GoogleSignInAccount googleSignInAccount) {


        String firstName = googleSignInAccount.getGivenName();
        String email = googleSignInAccount.getEmail();
        String lastName = googleSignInAccount.getFamilyName();
        pDialog.setMessage("Loading ...");
        pDialog.show();
        //register profil Gmail into database
        insertUser(firstName, lastName, email);

    }

    // function to connet with graphQL and load user Profile
    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            insertUser(first_name, last_name, email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void insertUser(final String firstName , final String lastName , final String email) {

        // get imei
        final String codeImei = deviceSession.sharedPreferences.getString("imei","0");
        final JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("email", email);
            jsonBodyObj.put("firstName", firstName);
            jsonBodyObj.put("lastName", lastName);
            jsonBodyObj.put("codeImei", codeImei);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBodyObj.toString();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/user/socialMedia",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response) ;
                            String token = jsonObject.getString("token") ;
                            String idUser = jsonObject.getString("idUser") ;
                            String phoneNumber = jsonObject.getString("phoneNumber") ;
                            String dateOfBirth = jsonObject.getString("dateOfBirth") ;
                            String governorate = jsonObject.getString("governorate") ;
                            String city = jsonObject.getString("city") ;
                            String delegation = jsonObject.getString("delegation");
                            String zipCode = jsonObject.getString("zipCode");
                            String imageProfile = jsonObject.getString("imageProfile");
                            String fName = jsonObject.getString("firstName") ;
                            String lName = jsonObject.getString("lastName") ;
                            // insert user information to local database
                            sessionManager.createSession(fName, email, lName, token, phoneNumber, dateOfBirth, governorate, city, delegation, zipCode, imageProfile);
                            deviceSession.updateDevice(idUser) ;
                            pDialog.hide();
                            Intent intent = new Intent(loginActivity.this, mainActivity.class);
                            startActivity(intent);
                            finish();


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
                params.put("email", email);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
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
        //end of function
    }

    private void login() {

        email = this.editTextEmail.getText().toString().trim();
        password = this.editTextPassword.getText().toString().trim();
        final String codeimei = deviceSession.sharedPreferences.getString("imei", "0");
        if (remember.isChecked()) {
            SharedPreferences.Editor editor = getSharedPreferences("address", MODE_PRIVATE).edit();
            editor.clear();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }

        if (userLogin()) {
            pDialog.setMessage("Loading...");
            pDialog.show();


            JSONObject jsonBodyObj = new JSONObject();
            try {
                jsonBodyObj.put("email", email);
                jsonBodyObj.put("password", password);
                jsonBodyObj.put("codeImei", codeimei);
                jsonBodyObj.put("app", "mobile");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBodyObj.toString();

            StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/user/login",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.print(response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String isLogged = jsonObject.getString("isLogged");
                                if (isLogged.equals("true")) {
                                    String id = jsonObject.getString("idUser");
                                    String email = jsonObject.getString("email");
                                    String firstname = jsonObject.getString("firstName");
                                    String lastname = jsonObject.getString("lastName");
                                    String tel = jsonObject.getString("phoneNumber");
                                    String token = jsonObject.getString("token");
                                    String dateOfBirth = jsonObject.getString("dateOfBirth");
                                    String governorate = jsonObject.getString("governorate");
                                    String city = jsonObject.getString("city");
                                    String delegation = jsonObject.getString("delegation");
                                    String zipcode = jsonObject.getString("zipCode");
                                    String image_profile = jsonObject.getString("imageProfile");

                                    sessionManager.createSession(firstname, email, lastname, token, tel, dateOfBirth, governorate, city, delegation, zipcode, image_profile);
                                    deviceSession.updateDevice(id);
                                    pDialog.hide();
                                    Intent i = new Intent(loginActivity.this, mainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    String check = jsonObject.getString("msg");
                                    if (check.equals("verify")) {
                                        Intent intent = new Intent(loginActivity.this, validationCodeActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                    } else if(check.equals("User not registred or deleted!")) {
                                        Toast.makeText(loginActivity.this, "User not registred or deleted!", Toast.LENGTH_LONG).show();
                                    } else if (check.equals("Password incorrect !"))
                                    {
                                        Toast.makeText(loginActivity.this, "Password incorrect !", Toast.LENGTH_LONG).show();
                                    }

                                    pDialog.hide();
                                    textViewsignUp.setVisibility(View.VISIBLE);

                                }

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
                    params.put("email", email);
                    params.put("password", password);
                    params.put("codeImei", codeimei);
                    params.put("app", "mobile");
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


    private boolean userLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false ;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return false ;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return false ;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password should be atleast 6 character long");
            editTextPassword.requestFocus();
            return false ;
        }
        return true;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signUp:
                Intent intentMain = new Intent(loginActivity.this , signUpActivity.class);
                loginActivity.this.startActivity(intentMain);
                break;

            case R.id.btnLogin:
                login();
                break;

            case R.id.forgetPassword :
                Intent intent = new Intent(loginActivity.this, forgetPasswordActivity.class);
                startActivity(intent);
                break ;

            case R.id.back:
                onBackPressed();
                break;

            default:

        }
    }
}