package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDBHandlerProduct extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "productMicroService.db";
    public static final String TABLE_PRODUCTS = "productsMS";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IDLISTMS = "idList";
    public static final String COLUMN_PRODUCTNAME = "productname";
    public static final String COLUMN_MARK = "MARK";
    public static final String COLUMN_MODEL = "MODEL";
    public static final String COLUMN_QRCODE = "QRcode";
    public static final String COLUMN_UNIT = "UNIT";
    public static final String COLUMN_QUANTITY = "QUANTITY";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IDMODEL = "idModel";
    public static final String COLUMN_IDPRODUCTMS = "idProductMicroService";

    public static final String COLUMN_PRIX = "prix";
    public static final String COLUMN_BARCODE = "code";


    public MyDBHandlerProduct(Context context
                       ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PRODUCTS + "("
                +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IDLISTMS + " TEXT," +
                COLUMN_PRODUCTNAME + " TEXT," +
                COLUMN_MARK + " TEXT," +
                COLUMN_QRCODE + " TEXT," +
                COLUMN_MODEL + " TEXT," +
                COLUMN_UNIT + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_QUANTITY + " TEXT," +
                COLUMN_PRIX + " INTEGER," +
                COLUMN_BARCODE + " TEXT," +
                COLUMN_IDPRODUCTMS + " TEXT," +
                COLUMN_IDMODEL + " TEXT" +
                ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);

    }

    public void addProduct(ProductMicroService product) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_IDLISTMS, product.getIdListMS());
        values.put(COLUMN_PRODUCTNAME, product.getProductNameMS());
        values.put(COLUMN_MARK, product.getMarkMS());
        values.put(COLUMN_QRCODE, product.getQrCodeMS());
        values.put(COLUMN_MODEL, product.getModelMS());
        values.put(COLUMN_UNIT, product.getUnitMS());
        values.put(COLUMN_CATEGORY, product.getCategoryMS());
        values.put(COLUMN_DESCRIPTION, product.getDescriptionMS());
        values.put(COLUMN_QUANTITY, product.getQuantityMS());
        values.put(COLUMN_PRIX, product.getPrixMS());
        values.put(COLUMN_BARCODE, product.getBarCodeMS());
        values.put(COLUMN_IDPRODUCTMS, product.getIdProductMS());
        values.put(COLUMN_IDMODEL, product.getIdModelMS());


        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public ArrayList<ProductMicroService> getAllProducts(String idListMicroService){

        ArrayList<ProductMicroService> allProducts = new ArrayList<ProductMicroService>();
        ProductMicroService product;
        String productName, mark, model, qrCode, unit, quantity, barCode, description, category, idListMS, idModelMs, idProductMS;
        int prix, id;

        String query = "Select * FROM " + TABLE_PRODUCTS +" WHERE " + COLUMN_IDLISTMS + " =  '" + idListMicroService + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            idListMS = cursor.getString(cursor.getColumnIndex(COLUMN_IDLISTMS));
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
            idProductMS = cursor.getString(cursor.getColumnIndex(COLUMN_IDPRODUCTMS));
            idModelMs = cursor.getString(cursor.getColumnIndex(COLUMN_IDMODEL));


            product = new ProductMicroService(id,
                    idListMS,
                    productName,
                    mark,
                    model,
                    qrCode,
                    unit,
                    quantity,
                    barCode,
                    description,
                    category,
                    prix,
                    idModelMs,
                    idProductMS
            );
            allProducts.add(product);
        }
        return allProducts;
    }

    public void deletAll(String idListMS){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_PRODUCTS + " WHERE " + COLUMN_IDLISTMS +"='" + idListMS + "'");
        db.close();

    }

    public void deletProductList(int idProduct, String idListMS){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_PRODUCTS + " WHERE " + COLUMN_IDLISTMS +"='" + idListMS + "' AND " + COLUMN_ID +
                "='" + idProduct + "'");
        db.close();

    }

    public void updateProductLocal(int idProduct, String idListMS, String unit, String quantity){

        SQLiteDatabase db = this.getWritableDatabase();
        // SET Column1 = someValue   COLUMN_MARK COLUMN_MODEL COLUMN_UNIT COLUMN_QUANTITY
        /*
                + COLUMN_MARK + " = '" + mark +
                "' ,"+COLUMN_MODEL+"= '" + model +
                "' ,"
         */
        db.execSQL("UPDATE " + TABLE_PRODUCTS + " SET "
                     +COLUMN_UNIT+"= '" + unit +
                "' ,"+COLUMN_QUANTITY+"= '" + quantity + "' WHERE " + COLUMN_IDLISTMS + " = '" + idListMS + "' AND " + COLUMN_ID +
                " = '" + idProduct + "'");
        db.close();
    }

}