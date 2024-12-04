package com.example.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.note.bean.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "noteSQLite.db";
    public static final String TABLE_NAME_NOTE = "note";

    // Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_WEATHER = "weather";
    public static final String COLUMN_IMAGE_DATA = "image_data";

    // SQL statement to create the table
    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME_NOTE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_CONTENT + " TEXT, " +
            COLUMN_CREATE_TIME + " TEXT, " +
            COLUMN_WEATHER + " INT, " +
            COLUMN_IMAGE_DATA + " BLOB)";


    public NoteDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




    public long insertData(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_CREATE_TIME, note.getCreatedTime());
        values.put(COLUMN_WEATHER, note.getWeather());
        values.put(COLUMN_IMAGE_DATA, note.getPicture()); // 假设getImageData()返回图片的byte[]数据
        return db.insert(TABLE_NAME_NOTE, null, values);
    }

    public int deleteFromDbById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_NOTE, COLUMN_ID + " = ?", new String[]{id});
    }

    public int updateData(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        values.put(COLUMN_CREATE_TIME, note.getCreatedTime());
        values.put(COLUMN_WEATHER, note.getWeather());
        values.put(COLUMN_IMAGE_DATA, note.getPicture()); // 更新图片数据
        return db.update(TABLE_NAME_NOTE, values, COLUMN_ID + " = ?", new String[]{note.getId()});
    }

    public List<Note> queryAllFromDb() {
        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME_NOTE, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                int createTimeIndex = cursor.getColumnIndex(COLUMN_CREATE_TIME);
                int weatherIndex = cursor.getColumnIndex(COLUMN_WEATHER);
                int imageDataIndex = cursor.getColumnIndex(COLUMN_IMAGE_DATA);

                // Check if any index is -1, which means the column does not exist
                if (idIndex == -1 || titleIndex == -1 || contentIndex == -1 ||
                        createTimeIndex == -1 || weatherIndex == -1 || imageDataIndex == -1) {
                    throw new IllegalStateException("One or more columns do not exist in the table");
                }

                String id = cursor.getString(idIndex);
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String createTime = cursor.getString(createTimeIndex);
                int weather = cursor.getInt(weatherIndex);
                byte[] imageData = cursor.getBlob(imageDataIndex); // 从数据库中获取图片数据

                Note note = new Note();
                note.setId(id);
                note.setTitle(title);
                note.setContent(content);
                note.setCreatedTime(createTime);
                note.setWeather(weather);
                note.setPicture(imageData); // 设置图片数据到Note对象
                noteList.add(note);
            }
            cursor.close();
        }
        return noteList;
    }

    public List<Note> queryFromDbByTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return queryAllFromDb(); // 如果标题为空，则查询所有记录
        }

        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        // 使用SQL注入安全的方法构建查询
        Cursor cursor = db.query(TABLE_NAME_NOTE, null, COLUMN_TITLE + " LIKE ?", new String[]{"%" + title + "%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                String title2 = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
                String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_TIME));
                int weather = cursor.getInt(cursor.getColumnIndex(COLUMN_WEATHER));
                byte[] imageData = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_DATA)); // 获取图片数据

                // 创建并设置Note对象
                Note note = new Note();
                note.setId(id);
                note.setTitle(title2);
                note.setContent(content);
                note.setCreatedTime(createTime);
                note.setWeather(weather);
                note.setPicture(imageData); // 设置图片数据

                noteList.add(note);
            }
            cursor.close();
        }
        return noteList;
    }

    public List<Note> queryFromDbByDate(String date) {
        SQLiteDatabase db = getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        // 使用SQL注入安全的方法构建查询
        Cursor cursor = db.query(TABLE_NAME_NOTE, null, COLUMN_CREATE_TIME + " LIKE ?", new String[]{date + "%"}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                int createTimeIndex = cursor.getColumnIndex(COLUMN_CREATE_TIME);
                int weatherIndex = cursor.getColumnIndex(COLUMN_WEATHER);
                int imageDataIndex = cursor.getColumnIndex(COLUMN_IMAGE_DATA);

                // Check if any index is -1, which means the column does not exist
                if (idIndex == -1 || titleIndex == -1 || contentIndex == -1 ||
                        createTimeIndex == -1 || weatherIndex == -1 || imageDataIndex == -1) {
                    throw new IllegalStateException("One or more columns do not exist in the table");
                }

                String id = cursor.getString(idIndex);
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String createTime = cursor.getString(createTimeIndex);
                int weather = cursor.getInt(weatherIndex);
                byte[] imageData = cursor.getBlob(imageDataIndex); // 获取图片数据

                // 创建并设置Note对象
                Note note = new Note();
                note.setId(id);
                note.setTitle(title);
                note.setContent(content);
                note.setCreatedTime(createTime);
                note.setWeather(weather);
                note.setPicture(imageData); // 设置图片数据

                noteList.add(note);
            }
            cursor.close();
        }
        return noteList;
    }



}
