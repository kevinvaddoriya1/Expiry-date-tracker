package com.test.expirydatetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBhelper extends SQLiteOpenHelper {

    Handler handler;
    Runnable runnable;

    public static String strdate="";
    private static final String DB_NAME="expire.db";
    private static final int DB_VERSION=1;
    public static final String TABLE="product";
    public static final String TABLE2="reg";
    public static final String ID_USR = "id";
    public static final String  GMAIL_COL= "Gmail";
    public static final String USR_COL = "user";
    public static final String PASS_COL = "pass";

    public static final String ID_COL="id";
    public static final String NAME_COL="name";
    public static final String FUTURE_COL="future";

    public DBhelper(Context context) {
        super(context,DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+TABLE+" ("+
                ID_COL+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                NAME_COL+" TEXT,"+
                FUTURE_COL+" TEXT)";
        db.execSQL(query);
        String query2 = "CREATE TABLE "+TABLE2+" ("+
                ID_USR+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                GMAIL_COL+"TEXT"+
                USR_COL+" TEXT,"+
                PASS_COL+" TEXT)";
        db.execSQL(query2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addNew(String name,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL,name);
        values.put(FUTURE_COL,date);
        db.insert(TABLE,null,values);
        db.close();

    }

    public void addUser(String gmail,String usr,String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GMAIL_COL,gmail);
        values.put(USR_COL,usr);
        values.put(PASS_COL,pass);
        db.insert(TABLE2,null,values);
        db.close();

    }

    public void updateNew(Context context,String newValue1,String newValue2,String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {id};
        ContentValues values = new ContentValues();
        values.put(NAME_COL, newValue1);
        values.put(FUTURE_COL, newValue2);

        int rowsAffected = db.update(TABLE, values, whereClause, whereArgs);
        if (rowsAffected > 0) {
            Toast.makeText(context,"Product has been update..",Toast.LENGTH_SHORT).show();
            db.close(); // Close the database after updating


        } else {
            Toast.makeText(context,"update failed..",Toast.LENGTH_SHORT).show();
            db.close(); // Close the database after updating
        }
    }

    public ArrayList<ProductModel> fetchdata(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE,null);

        ArrayList<ProductModel> products = new ArrayList<>();

        while (cursor.moveToNext()){
            ProductModel productModel = new ProductModel();
            productModel.setId(cursor.getInt(0));
            productModel.setName(cursor.getString(1));
            productModel.setDate(cursor.getString(2));
            products.add(productModel);
        }

        return products;
    }
}
