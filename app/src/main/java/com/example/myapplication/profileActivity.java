package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class profileActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SessionManager sessionManager;
    private HashMap<String, String> user ;
    private ProgressDialog pDialog;
    private SwipeRefreshLayout swipeLayout;
    private Button btnUpdateProfile , btnLogout;
    private ImageView imageViewProfile, imageViewUpdateImage , imageViewBack;
    private LinearLayout linearLayoutGroup , linearLayoutShowlist ;
    private ImageRequest imageRequest;
    private MyDbHandlerList myDbHandlerList;
    private final int REQUEST_CODE_GALLERY = 999;
    private TextView textViewFullName, textViewEmail , textViewPhoneNumber;
    private Dialog myDialog;
    private int userId ;
    private String firstName , lastName , email , phoneNumber , token;
    public DeviceSession deviceSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);


        // Buttons
        btnUpdateProfile = findViewById(R.id.update_profile);
        btnLogout = findViewById(R.id.logout);

        // TextView
        textViewFullName = findViewById(R.id.name);
        textViewEmail = findViewById(R.id.mail);
        textViewPhoneNumber = findViewById(R.id.phone);

        // ImageView
        imageViewProfile = findViewById(R.id.profile);
        imageViewUpdateImage = findViewById(R.id.updateImage);
        imageViewBack = findViewById(R.id.back);

        // Linearlayouts
        linearLayoutGroup = findViewById(R.id.creategroup);
        linearLayoutShowlist = findViewById(R.id.showlist) ;

        // Dialog
        myDialog = new Dialog(this);

        // Progress Dialog
        pDialog = new ProgressDialog(this) ;

        // DeviceSession
        deviceSession = new DeviceSession(this);

        // SessionManager
        sessionManager = new SessionManager(this);

        // MyDbHandlerList ( local Database of lists )
        myDbHandlerList = new MyDbHandlerList(this);

        // get Values from Device Session
        userId = Integer.parseInt(deviceSession.sharedPreferences.getString("userId", "0"));

        // setOnclickListner
        btnLogout.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);
        linearLayoutGroup.setOnClickListener(this);
        imageViewUpdateImage.setOnClickListener(this);
        linearLayoutShowlist.setOnClickListener(this);

        // check if the user is logged if not the user will be redirected to login page
        sessionManager.checkLogin() ;
        // get User from Session Manager
        user= sessionManager.getUserDetail();
        firstName = user.get(sessionManager.FIRSTNAME);
        lastName = user.get(sessionManager.LASTNAME);
        email = user.get(sessionManager.EMAIL);
        phoneNumber = user.get(sessionManager.TEL);
        token = user.get(sessionManager.TOKEN);

        // set values from local database to textViews
        textViewFullName.setText(firstName + " " + lastName);
        textViewEmail.setText(email);
        textViewPhoneNumber.setText(phoneNumber);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getImg();

        // End Of onCreate
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), mainActivity.class);
        startActivity(i);
    }

    // function to select an image from your gallery.
    public void uploadImage(){
        Toast.makeText(profileActivity.this, "select image ",Toast.LENGTH_LONG).show();

        ActivityCompat.requestPermissions(
                profileActivity.this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }


    // get all permission to image gallery.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // compress your image on Bitmap.
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //load your image into your xml profile and send to server.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && data != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewProfile.setImageBitmap(bitmap);
                // function to insert image
                uploadBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadBitmap(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, HostName_Interface.client + "/user/image",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message") , Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                 headers.put("x-api-Key", "eiWee8ep9due4deeshoa8Peichai8Eih");
                 headers.put("x-access-token", token);

                return headers;
                //return (mHeaders != null) ? mHeaders : super.getHeaders();
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }

    public void ShowPopupLogout() {

        myDialog.setContentView(R.layout.delete_all_lists);
        // TextView to close the Dialog
        TextView TextViewclose =myDialog.findViewById(R.id.close);

        // Button to delete Lists from local database
        Button bntDelete = myDialog.findViewById(R.id.delete);

        // Button to logout without deleting anything
        Button btnLogoutOutWithoutDelete = (Button) myDialog.findViewById(R.id.logOutWithoutDelete);

        TextViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        bntDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDbHandlerList.deleteAll(userId);
                Toast.makeText(profileActivity.this, "Your Lists are Deleted", Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
                logout();

            }
        });

        btnLogoutOutWithoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                logout();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void logout() {

        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, HostName_Interface.client + "/user/logOut",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // hide Progress Dialog after receiving the response
                        pDialog.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String isCreated = jsonObject.getString("isLogged");
                            if (isCreated.equals("false")) {
                                deviceSession.updateDevice("0");
                                sessionManager.logout();
                                Intent i = new Intent(getApplicationContext(), mainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "logout Error", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "log out Error" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(profileActivity.this, "volley Error" + error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-Key", "eiWee8ep9due4deeshoa8Peichai8Eih");
                headers.put("x-access-token", token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(Stringrequest);
    }


    // get Image from MicroService
    public void getImg() {
        imageRequest = new ImageRequest(HostName_Interface.client + "/user/getImage", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap response) {
                imageViewProfile.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(profileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("x-api-Key", "eiWee8ep9due4deeshoa8Peichai8Eih");
                headers.put("x-access-token", user.get(sessionManager.TOKEN));
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(profileActivity.this);
        requestQueue.add(imageRequest);
        requestQueue.getCache().clear();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                ShowPopupLogout();
                break;
            case R.id.creategroup:
                Intent intentGroup = new Intent(getApplicationContext(), GroupsActivity.class);
                startActivity(intentGroup);
                break;
            case R.id.update_profile :
                Intent intentUpdateProfile = new Intent(getApplicationContext(), updateProfileActivity.class);
                startActivity(intentUpdateProfile);
                break ;
            case R.id.back:
            case R.id.showlist:
                Intent intent = new Intent(getApplicationContext(), mainActivity.class) ;
                startActivity(intent);
                break ;
            case R.id.updateImage :
                uploadImage();
                break ;
            default:
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

}