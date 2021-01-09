package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class myDbHandlerPOS  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "dBPOS";
    private static final String TABLE_NAME = "POS";
    private static final String COLUMN_ID = "idPos";
    private static final String COLUMN_NAME = "namePos";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_ADRESS = "adress";
    private static final String COLUMN_DELEGATION = "delegation";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_CODEZIP = "codeZip";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phoneNumber";

    public myDbHandlerPOS(@Nullable Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+TABLE_NAME+
                " ( "+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT" +
                " , "+COLUMN_NAME+" TEXT "+
                " , "+COLUMN_LOCATION+" TEXT "+
                " , "+COLUMN_ADRESS+" TEXT "+
                " , "+COLUMN_DELEGATION+" TEXT "+
                " , "+COLUMN_CITY+" TEXT "+
                " , "+COLUMN_CODEZIP+" TEXT "+
                " , "+COLUMN_EMAIL+" TEXT  "+
                " , "+COLUMN_PHONE+" TEXT ); " ;

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addPointOfSale(pointOfSale pointOfSale)
    {
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(COLUMN_NAME,pointOfSale.getNamePos());
        contentValues.put(COLUMN_LOCATION,pointOfSale.getLocation());
        contentValues.put(COLUMN_ADRESS,pointOfSale.getAddress());
        contentValues.put(COLUMN_DELEGATION,pointOfSale.getDelegation());
        contentValues.put(COLUMN_CITY,pointOfSale.getCity());
        contentValues.put(COLUMN_CODEZIP,pointOfSale.getCodeZip());
        contentValues.put(COLUMN_EMAIL,pointOfSale.getEmail());
        contentValues.put(COLUMN_PHONE,pointOfSale.getPhoneNumber());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public List<pointOfSale> getPointOfSales()
    {
        String namePos , location , adress , email , phoneNumber , delegation , codeZip , city ;
        int idPOS ;
        pointOfSale pointOfSale  ;
        List<pointOfSale> list = new ArrayList<pointOfSale>();
        String query = "Select * FROM " + TABLE_NAME +" ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext())
        {
            idPOS = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)) ;
            namePos = cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) ;
            location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)) ;
            adress = cursor.getString(cursor.getColumnIndex(COLUMN_ADRESS)) ;
            delegation = cursor.getString(cursor.getColumnIndex(COLUMN_DELEGATION)) ;
            codeZip = cursor.getString(cursor.getColumnIndex(COLUMN_CODEZIP));
            city = cursor.getString(cursor.getColumnIndex(COLUMN_CITY)) ;
            email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)) ;
            phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)) ;
            pointOfSale = new pointOfSale(idPOS,namePos,location,adress,email,phoneNumber,delegation,codeZip,city) ;
            list.add(pointOfSale) ;
        }
        cursor.close();
        db.close();
        return list ;
    }


}
