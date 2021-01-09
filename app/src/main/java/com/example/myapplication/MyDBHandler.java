package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "productDB.db";
    public static final String TABLE_PRODUCTS = "products";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCTNAME = "productname";
    public static final String COLUMN_MARK = "MARK";
    public static final String COLUMN_MODEL = "MODEL";
    public static final String COLUMN_QRCODE = "QRcode";
    public static final String COLUMN_UNIT = "UNIT";
    public static final String COLUMN_QUANTITY = "QUANTITY";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_PRIX = "prix";
    public static final String COLUMN_BARCODE = "code";


    public MyDBHandler(Context context, String name,
                       CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PRODUCTS + "("
                +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUCTNAME + " TEXT," +
                COLUMN_MARK + " TEXT," +
                COLUMN_QRCODE + " TEXT," +
                COLUMN_MODEL + " TEXT," +
                COLUMN_UNIT + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_QUANTITY + " TEXT," +
                COLUMN_PRIX + " INTEGER," +
                COLUMN_BARCODE + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);

    }

    public ArrayList<String> findProduct (String productname) {
        String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME +" like '%"+productname+"%'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> fileName = new ArrayList<String>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do
                {
                   Product p = new Product();

                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME));
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));

                    fileName.add(name + ".\n  Category: "+ category);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            fileName = null;
        }
        db.close();
        return fileName;
    }

    public void addProduct(Product product) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, product.getproductName());
        values.put(COLUMN_MARK, product.getMark());
        values.put(COLUMN_QRCODE, product.getQrCode());
        values.put(COLUMN_MODEL, product.getModel());
        values.put(COLUMN_UNIT, product.getUnit());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_PRIX, product.getPrix());
        values.put(COLUMN_BARCODE, product.getBarCode());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public Product findProductdatil(String productname) {
        String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME +" like '%"+productname+"%'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Product fileName = new Product();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do
                {
                    fileName.setProductName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME)));
                    fileName.setMark(cursor.getString(cursor.getColumnIndex(COLUMN_MARK)));
                    fileName.setQrCode(cursor.getString(cursor.getColumnIndex(COLUMN_QRCODE)));
                    fileName.setModel(cursor.getString(cursor.getColumnIndex(COLUMN_MODEL)));
                    fileName.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
                    fileName.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                    fileName.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    fileName.setQuantity(cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY)));
                    fileName.setBarCode(cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE)));
                    fileName.setPrix(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRIX))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            fileName = null;
        }
        db.close();
        return fileName;
    }

    public ArrayList<Product> getAllProducts(){

        ArrayList<Product> allProducts = new ArrayList<Product>();
        Product product;
        String productName, mark, model, qrCode, unit, quantity, barCode, description, category;
        int prix, id;

        String query = "Select * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            productName = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTNAME));
            mark = cursor.getString(cursor.getColumnIndex(COLUMN_MARK));
            model = cursor.getString(cursor.getColumnIndex(COLUMN_MODEL));
            qrCode = cursor.getString(cursor.getColumnIndex(COLUMN_QRCODE));
            unit = cursor.getString(cursor.getColumnIndex(COLUMN_UNIT));
            quantity = cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY));
            category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
            description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            prix = cursor.getInt(cursor.getColumnIndex(COLUMN_PRIX));
            barCode = cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE));

            product = new Product(id,
                    productName,
                    mark,
                    model,
                    qrCode,
                    unit,
                    quantity,
                    barCode,
                    description,
                    category,
                    prix
                    );
            allProducts.add(product);
        }
        return allProducts;
    }



}