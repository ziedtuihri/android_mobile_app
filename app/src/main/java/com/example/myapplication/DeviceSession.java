package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceSession {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "device";
    private static final String device = "device";
    public static final String imei = "imei";
    public static final String userId = "userId";

    public DeviceSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createDeviceSession(String imei,String userId) {
        editor.putBoolean(this.device,true) ;
        editor.putString(this.imei, imei);
        editor.putString(this.userId, userId);
        editor.apply();
    }

    public boolean checkInsertedDevice(){
        return sharedPreferences.getBoolean(device, false);
    }

    public void updateDevice(String userId)
    {
        editor.putString(this.userId,userId) ;
        editor.apply();
    }




}
