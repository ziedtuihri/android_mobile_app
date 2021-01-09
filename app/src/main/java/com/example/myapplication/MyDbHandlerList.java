package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDbHandlerList extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbList";
    private static final String TABLE_NAME = "LIST";
    private static final String COLUMN_ID = "listId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DEVICEID = "deviceId";
    private static final String COLUMN_USERID = "userId";
    private static final String COLUMN_LISTMS = "LISTMS";
    private static final String COLUMN_IMEI = "codeimei";


    public MyDbHandlerList(@Nullable Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+TABLE_NAME+
                " ( "+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT" +
                " , "+COLUMN_LISTMS+" TEXT "+
                " , "+COLUMN_IMEI+" TEXT "+
                " , "+COLUMN_NAME+" TEXT "+
                " , "+COLUMN_DEVICEID+" INTEGER "+
                " , "+COLUMN_USERID+" INTEGER );";

        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addList(listt list){

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LISTMS,list.getlistMS());
        contentValues.put(COLUMN_IMEI,list.getCodeimei());
        contentValues.put(COLUMN_NAME,list.getName());
        contentValues.put(COLUMN_LISTMS, list.getlistMS());
        contentValues.put(COLUMN_DEVICEID,list.getDeviceId());
        contentValues.put(COLUMN_USERID,list.getUserId());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public ArrayList<listt> getList() {

        ArrayList<listt> l = new ArrayList<listt>();
        listt list;
        int listId,deviceId,userId;
        String name, listMS, codeImei;
        String query = "Select * FROM " + TABLE_NAME +" ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext())
        {
            listId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            deviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_DEVICEID));
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USERID));
            listMS = cursor.getString(cursor.getColumnIndex(COLUMN_LISTMS));
            codeImei = cursor.getString(cursor.getColumnIndex(COLUMN_IMEI));

           list = new listt(listId,name,deviceId,userId,listMS,codeImei);
           l.add(list);
        }

        return l ;

    }

    public ArrayList<listt> getList(int id){

        ArrayList<listt> l = new ArrayList<listt>();
        listt list;
        int listId,deviceId,userId;
        String name, listMS, codeImei;
        String query = "Select * FROM " + TABLE_NAME +" where "+ COLUMN_USERID + " = 0 or  " + COLUMN_USERID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext())
        {
            listId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            deviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_DEVICEID));
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USERID));
            listMS = cursor.getString(cursor.getColumnIndex(COLUMN_LISTMS));
            codeImei = cursor.getString(cursor.getColumnIndex(COLUMN_IMEI));

            list = new listt(listId,name,deviceId,userId,listMS,codeImei);
            l.add(list);
        }

        return l ;
    }

    public void deleteList(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_ID+"='"+id+"'");
        db.close();
    }

    public void deleteListMS(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_LISTMS+"='"+id+"'");
        db.close();
    }

    public void deleteAll(int userId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_USERID+"='"+userId+"'");
        //db.delete(TABLE_NAME, null,null);
        db.close();
    }

    public void delete() {

        SQLiteDatabase db = this.getWritableDatabase();
        //delete all table
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }


}
