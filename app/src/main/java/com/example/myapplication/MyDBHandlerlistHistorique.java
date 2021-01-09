package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandlerlistHistorique extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "listhistoriqueDB.db";
    public static final String TABLE_LIST = "listshistorique";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LIST = "list";
    public static final String COLUMN_DATEBUY = "datebuy";
    public static final String COLUMN_PRICE = "price";



    public MyDBHandlerlistHistorique(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_LIST + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME
                + " VARCHAR," + COLUMN_LIST + " INTEGER," + COLUMN_DATEBUY + " VARCHAR,"+ COLUMN_PRICE + " VARCHAR "+  ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        onCreate(db);
    }
    public void addList(ListProductHistorique list) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, list.getName());
        values.put(COLUMN_LIST, list.getDate());
        values.put(COLUMN_PRICE, list.getIdList());
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        values.put(COLUMN_DATEBUY, ts);



        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LIST, null, values);
        db.close();
    }

    public void editList(ListProduct list) {
        System.out.println(list.getName());
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, list.getName());
        values.put(COLUMN_LIST, list.getList());
        String[] name= new String[]{String.valueOf(list.getList())};
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " +TABLE_LIST+" SET "+COLUMN_LIST+" = '"+list.getList()+"'  WHERE " +COLUMN_NAME+" = \""+ list.getName()+"\"" );

        db.close();
    }


    public ListProduct findList(String productname) {
        String query = "Select * FROM " + TABLE_LIST + " WHERE " + COLUMN_NAME + " =  " + productname + "";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ListProduct list = new ListProduct();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            list.setId(Integer.parseInt(cursor.getString(0)));
            list.setName(cursor.getString(1));
            //list.setList(Integer.parseInt(cursor.getString(2)));
            cursor.close();
        } else {
            list = null;
        }
        db.close();
        return list;
    }

    public List<ListProductHistorique> fetchlist() {
        String query = "Select * FROM " + TABLE_LIST+"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<ListProductHistorique> fileName = new ArrayList<ListProductHistorique>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do
                {
                    ListProductHistorique p = new ListProductHistorique();
                    p.setName((cursor.getString(cursor.getColumnIndex(COLUMN_NAME))));
                    p.setDate((cursor.getString(cursor.getColumnIndex(COLUMN_DATEBUY))));
                    p.setName((cursor.getString(cursor.getColumnIndex(COLUMN_PRICE))));

                    fileName.add(p);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            fileName = null;
        }
        db.close();
        return fileName;
    }


    public List<ListProductHistorique> fetchlistdetail(String listname) {
        String query = "Select * FROM " + TABLE_LIST+ " WHERE " + COLUMN_NAME +" =  \"" + listname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<ListProductHistorique> fileName = new ArrayList<ListProductHistorique>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do
                {
                    ListProductHistorique p = new ListProductHistorique();
                    p.setName((cursor.getString(cursor.getColumnIndex(COLUMN_NAME))));
                    p.setDate((cursor.getString(cursor.getColumnIndex(COLUMN_LIST))));
                    fileName.add(p);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            fileName = null;
        }
        db.close();
        return fileName;
    }

    public boolean deleteList(String productname) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_LIST + " WHERE " + COLUMN_NAME + " =  \"" + productname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ListProduct list = new ListProduct();

        if (cursor.moveToFirst()) {
            list.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_LIST, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(list.getId()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

}