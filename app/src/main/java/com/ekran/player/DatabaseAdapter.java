package com.ekran.player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ekran.player.model.Content;
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
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASS,  DatabaseHelper.COLUMN_TOKEN, DatabaseHelper.COLUMN_PANELNAME};
        return  database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    private Cursor getAllContent() {
        String[] columns = new String[] {DatabaseHelper.COLUMN_CONT_ID, DatabaseHelper.COLUMN_CONT_FN, DatabaseHelper.COLUMN_CONT_FS,
                DatabaseHelper.COLUMN_CONT_SY, DatabaseHelper.COLUMN_CONT_DE, DatabaseHelper.COLUMN_CONT_ED,
                DatabaseHelper.COLUMN_CONT_UI, DatabaseHelper.COLUMN_CONT_PI, DatabaseHelper.COLUMN_CONT_TC};
        return  database.query(DatabaseHelper.TABLE_CONTENT, columns, null, null, null, null, null);
    }

    public void PrintContent() {
        List<Content> contents = getContent();
        for (Content c : contents) {
            Log.e("content: ", c.toString());
        }
    }

    public List<Content> getContent() {
        ArrayList<Content> contents = new ArrayList<>();
        Cursor cursor = getAllContent();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_ID));
                String file_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_FN));
                String file_size = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_FS));
                int sync = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_SY));
                int deleted = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_DE));
                String end_date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_ED));
                int user_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_UI));
                int panel_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_PI));
                int type_content = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONT_TC));
                contents.add(new Content(id, file_name, file_size, sync, deleted, end_date, user_id, panel_id, type_content));
            }  while (cursor.moveToNext());
        }
        return contents;
    }

    public List<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
                String pass = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASS));
                String token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
                String panelName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PANELNAME));
                users.add(new User(id, username, pass, token, panelName));
            }  while (cursor.moveToNext());
        }
        cursor.close();
        return  users;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public long getCountContent() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_CONTENT);
    }

    public User getUser(int id) {
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            String pass = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASS));
            String token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
            String panelName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PANELNAME));
            user  = new User(id, username, pass,  token, panelName);
        }
        cursor.close();
        return  user;
    }

    public long insert(User user) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_ID, user.getId());
        cv.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        cv.put(DatabaseHelper.COLUMN_PASS, user.getPassword());
        cv.put(DatabaseHelper.COLUMN_TOKEN, user.getToken());
        cv.put(DatabaseHelper.COLUMN_PANELNAME, user.getPanelName());

        return  database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long insertContent(Content content) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_CONT_ID, content.getId());
        cv.put(DatabaseHelper.COLUMN_CONT_FN, content.getFile_name());
        cv.put(DatabaseHelper.COLUMN_CONT_FS, content.getFile_size());
        cv.put(DatabaseHelper.COLUMN_CONT_SY, content.getSync());
        cv.put(DatabaseHelper.COLUMN_CONT_DE, content.getDeleted());
        cv.put(DatabaseHelper.COLUMN_CONT_ED, content.getEnd_date());
        cv.put(DatabaseHelper.COLUMN_CONT_UI, content.getUser_id());
        cv.put(DatabaseHelper.COLUMN_CONT_PI, content.getPanel_id());
        cv.put(DatabaseHelper.COLUMN_CONT_TC, content.getType_content());
        return  database.insert(DatabaseHelper.TABLE_CONTENT, null, cv);
    }

    public long delContent(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return database.delete(DatabaseHelper.TABLE_CONTENT, whereClause, whereArgs);
    }

    public void delAllContent() {
        database.delete(DatabaseHelper.TABLE_CONTENT, null, null);
    }

    public void delAllUser() {
        database.delete(DatabaseHelper.TABLE, null, null);
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
        cv.put(DatabaseHelper.COLUMN_PASS, user.getPassword());
        cv.put(DatabaseHelper.COLUMN_TOKEN, user.getToken());
        cv.put(DatabaseHelper.COLUMN_PANELNAME, user.getPanelName());

        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}
