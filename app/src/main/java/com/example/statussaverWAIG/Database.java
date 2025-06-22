package com.example.statussaverWAIG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class Database extends SQLiteOpenHelper {
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Database(Context context, String name, int version, SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    private static final String DatabaseName = "shrilink.db";
    private static SQLiteDatabase DB = null;
    private long result;
    private static ContentValues contantValues = null;

    private static final String table1 = "instagram";
    private static final String c = "column_id";
    private static final String c1 = "id";
    private static final String c2 = "post_url";
    private static final String c3 = "is_video";
    private static final String c4 = "thumbnail";
    private static final String c5 = "link";
    private static final String create_table1 = "CREATE TABLE "+table1+"( "+ c +" INTEGER PRIMARY KEY  AUTOINCREMENT,"+c1+ " TEXT UNIQUE NOT NULL,"+
            c2+" TEXT NOT NULL,"+c3+" INTEGER NOT NULL,"+c4+" TEXT," + c5 +" TEXT)";
    private static final String drop_table1 = "DROP TABLE IF EXISTS " + table1;
    private static final String getTable1 = "SELECT * FROM " +table1+ " ORDER BY "+c+" DESC";


    private static final String table2 = "gallery";
    private static final String w1 = "status_id";
    private static final String w2 = "uri";
    private static final String create_table2 = "CREATE TABLE "+table2+"("+ w1+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
            w2+" TEXT)";
    private static final String drop_table2 = "DROP TABLE IF EXISTS " + table2;
    private static final String getTable2 = "SELECT * FROM " +table2+ " ORDER BY "+w1+" DESC";

    private static final String table3 = "data_table";
    private static final String i = "Iid";
    private static final String i1 = "path";
    private static final String i2 = "uname";
    private static final String i3 = "post_name";
    private static final String i4 = "is_private";
    private static final String i5 = "is_verfied";
    private static final String i6 = "is_hashtag";
    private static final String create_table3 = "CREATE TABLE "+table3+"("+ i + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
            i1 +" TEXT," + i2 +" TEXT UNIQUE," + i3 +" TEXT," + i4 + " INTEGER DEFAULT 0," + i5 + " INTEGER DEFAULT 0,"+ i6 +" INTEGER DEFAULT 0)";
    private static final String drop_table3 = "DROP TABLE IF EXISTS " + table3;


    protected Database(Context context) {
        super(context, DatabaseName, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_table1);
        sqLiteDatabase.execSQL(create_table2);
        sqLiteDatabase.execSQL(create_table3);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(drop_table1);
        sqLiteDatabase.execSQL(drop_table2);
        sqLiteDatabase.execSQL(drop_table3);
        onCreate(sqLiteDatabase);
    }
    protected void clearData(){
        DB = this.getWritableDatabase();
        DB.execSQL(drop_table1);
        DB.execSQL(drop_table2);
        DB.execSQL(drop_table3);
        onCreate(DB);
    }
    protected void deteleinsta(){
        DB = this.getWritableDatabase();
        DB.execSQL(drop_table1);
        DB.execSQL(create_table1);
    }
    protected void detelegallery(){
        DB = this.getWritableDatabase();
        DB.execSQL(drop_table2);
        DB.execSQL(create_table2);
    }
    private static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    protected void inert_insta_post(final String url, final int isvideo, final String img, final String link){
        DB = this.getWritableDatabase();
        contantValues = new ContentValues();
        contantValues.put(c1,getSaltString());
        contantValues.put(c2,url);
        contantValues.put(c3,isvideo);
        contantValues.put(c4,img);
        contantValues.put(c5,link);
        DB.insert(table1, null, contantValues);
    }
    protected long delete_insta_post(final int pid){
        DB = this.getWritableDatabase();
        return (long) DB.delete(table1,c + " = " + pid ,null);
    }
    protected Cursor is_exists_insta_post(final String link){
        DB = this.getWritableDatabase();
        return DB.query(table1,null, c5+ " = ?", new String[] { link }, null, null, null);
    }
    protected Cursor get_insta_post(){
        DB = this.getWritableDatabase();
        return DB.rawQuery(getTable1,null);
    }
    protected long inert_gallery(final String uri){
        DB = this.getWritableDatabase();
        contantValues = new ContentValues();
        contantValues.put(w2,uri);
        return (long)DB.insert(table2,null,contantValues);
    }
    protected long delete_gallery(final String sid){
        DB = this.getWritableDatabase();
        return (long) DB.delete(table2,w1 + " = ?",new String[]{sid});
    }
    protected Cursor get_gallery(){
        DB = this.getWritableDatabase();
        return DB.rawQuery(getTable2,null);
    }
    
}