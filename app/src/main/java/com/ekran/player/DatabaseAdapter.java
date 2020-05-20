package com.ekran.player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.ekran.player.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_TOKEN, DatabaseHelper.COLUMN_PANELNAME};
        return  database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = getAllEntries();

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
                String token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
                String panelName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PANELNAME));
                users.add(new User(id, username, token, panelName));
            }
            while (cursor.moveToNext());

        }
        cursor.close();
        return  users;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public User getUser(long id) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            String token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
            String panelName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PANELNAME));
            user  = new User(id, username, token, panelName);
        }
        cursor.close();
        return  user;
    }

    public long insert(User user) {

        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_ID, user.getId());
        cv.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        cv.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        cv.put(DatabaseHelper.COLUMN_TOKEN, user.getToken());
        cv.put(DatabaseHelper.COLUMN_PANELNAME, user.getPanelName());

        return  database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long userId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(userId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(User user){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + String.valueOf(user.getId());
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_ID, user.getId());
        cv.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        cv.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        cv.put(DatabaseHelper.COLUMN_TOKEN, user.getToken());
        cv.put(DatabaseHelper.COLUMN_PANELNAME, user.getPanelName());

        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}
