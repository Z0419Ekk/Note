package com.example.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        return database.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public boolean loginUser(String username, String password) {
        String[] columns = {
                DatabaseHelper.COLUMN_ID
        };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?" + " AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}

