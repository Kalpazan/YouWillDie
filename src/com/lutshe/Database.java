package com.lutshe;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "survival_guide.sqlite3";
    private static final String TABLE_NAME = "messages";
    private static final int DB_VERSION = 1;
    private static final String KEY_DAY = "day";
    private static final String KEY_LINK = "link";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_ICON = "icon";

    private static Database instance;
    
    private Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    public synchronized static Database getDb(Context context) {
    	
    	if (instance == null) {
    		instance = new Database(context);
    	}
    	
    	return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_DAY + " TEXT NOT NULL, "
                + KEY_LINK + " TEXT NOT NULL, " + KEY_TITLE + " TEXT NOT NULL, " + KEY_TEXT + " TEXT NOT NULL, " +
                KEY_ICON + " TEXT NOT NULL);";
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(int day, String link, String title, String text, int icon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY, day);
        values.put(KEY_LINK, link);
        values.put(KEY_TITLE, title);
        values.put(KEY_TEXT, text);
        values.put(KEY_ICON, icon);

        db.insert(TABLE_NAME, null, values);
    }

    public synchronized NotificationTemplate getItem(int day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,    //откуда
                new String[]{KEY_LINK, KEY_TITLE, KEY_TEXT, KEY_ICON},//что
                KEY_DAY + "=?", // условие
                new String[]{String.valueOf(day)}, // что подставить в условие
                null, null,
                KEY_DAY); // отсортировать по этой штуке
       
        if (cursor != null) {
            cursor.moveToFirst();

			NotificationTemplate notificationTemplate = new NotificationTemplate(cursor.getString(0), cursor.getString(1), cursor.getString(2), Integer.valueOf(cursor.getString(3)));
			cursor.close();
			return notificationTemplate;
        }
        
        return null;
    }

    public synchronized NotificationTemplate[] getAllTemplates() {
        NotificationTemplate[] notifications = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
        	notifications = new NotificationTemplate[cursor.getCount()];
        	int i = 0;
        	
            do {
                NotificationTemplate nt = new NotificationTemplate(
                		cursor.getString(cursor.getColumnIndex(KEY_LINK)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TEXT)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ICON))
                        );

                // Adding contact to list
                notifications[i++] = nt;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return notifications;
    }

    public synchronized int getTemplatesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
		return count;
    }

}
