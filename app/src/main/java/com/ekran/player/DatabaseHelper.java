package com.ekran.player;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "store.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "user";
    static final String TABLE_CONTENT = "content";
    static final String TABLE_VERSION = "version";
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASS = "password";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_PANELNAME = "panelName";

    public static final String COLUMN_CONT_ID = "_id";
    public static final String COLUMN_CONT_FN = "file_name";
    public static final String COLUMN_CONT_FS = "file_size";
    public static final String COLUMN_CONT_SY = "sync";
    public static final String COLUMN_CONT_DE = "deleted";
    public static final String COLUMN_CONT_ED = "end_date";
    public static final String COLUMN_CONT_UI = "user_id";
    public static final String COLUMN_CONT_PI = "panel_id";
    public static final String COLUMN_CONT_TC = "type_content";

    public static final String COLUMN_VER = "version";
    public static final String COLUMN_ORI = "orientation";
    public static final String COLUMN_VER_ID = "idVer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COLUMN_ID  + " TEXT," +
                COLUMN_USERNAME  + " TEXT, " +
                COLUMN_PASS  + " TEXT, " +
                COLUMN_TOKEN + " TEXT, " +
                COLUMN_PANELNAME + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_CONTENT + " (" +
                COLUMN_CONT_ID  + " TEXT," +
                COLUMN_CONT_FN  + " TEXT, " +
                COLUMN_CONT_FS  + " TEXT, " +
                COLUMN_CONT_SY + " TEXT, " +
                COLUMN_CONT_DE + " TEXT, " +
                COLUMN_CONT_ED + " TEXT, " +
                COLUMN_CONT_UI + " TEXT, " +
                COLUMN_CONT_PI + " TEXT, " +
                COLUMN_CONT_TC + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_VERSION + " (" + COLUMN_VER_ID  + " TEXT," + COLUMN_VER + " TEXT," + COLUMN_ORI + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION);
        onCreate(db);
    }
}
