package com.projekt.mirage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class SettingsDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLENAME = "CONFIG";
    public static final String COLUMN_PROFILE = "PROFILE";
    public static final String COLUMN_SELECTION = "SELECTION";

    public SettingsDatabaseHelper(Context context) {
        super(context, "Settings.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLENAME + " ("
                + COLUMN_PROFILE + " TEXT, "
                + COLUMN_SELECTION + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean updateSettings(String profile, Uri imgSrc) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLENAME + " SET "
                + COLUMN_SELECTION + "='" + imgSrc.toString()
                + "' WHERE " + COLUMN_PROFILE + " = '" + profile + "'");
        db.close();
        return true;
    }

    public List<Uri> getImgSrc() {

        List<Uri> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TABLENAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                returnList.add(Uri.parse(cursor.getString(1)));
            } while (cursor.moveToNext());

        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_PROFILE, "user");
            cv.put(COLUMN_SELECTION, Uri.EMPTY.toString());
            db.insert(TABLENAME, null, cv);

            cv.clear();

            cv.put(COLUMN_PROFILE, "bot");
            cv.put(COLUMN_SELECTION, Uri.EMPTY.toString());
            db.insert(TABLENAME, null, cv);

            return returnList;
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public Uri getUri(String profile) {
        String queryString = "SELECT * FROM " + TABLENAME + " WHERE " + COLUMN_PROFILE + " = '" + profile + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            return Uri.parse(cursor.getString(1));
        } else {
            return Uri.EMPTY;
        }
    }
}
