package com.projekt.mirage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projekt.mirage.models.ChatsModel;

import java.util.ArrayList;

public class ChatsDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLENAME = "CHAT_RECORDS_TABLE";
    public static final String COLUMN_ID_NUM = "ID_NUM";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    public static final String COLUMN_SENDER = "SENDER";

    public ChatsDatabaseHelper(Context context) {
        super(context, "ChatRecords.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + " ("
                + COLUMN_ID_NUM + " INT, "
                + COLUMN_TASK_NAME + " TEXT, "
                + COLUMN_SENDER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addChatRecord(ChatsModel chatsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_NUM, chatsModel.getID());
        cv.put(COLUMN_TASK_NAME, chatsModel.getMessage());
        cv.put(COLUMN_SENDER, chatsModel.getSender());

        long insert = db.insert(TABLENAME, null, cv);
        db.close();
        return insert != -1;
    }

    public ArrayList<ChatsModel> getAllRecords() {
        ArrayList<ChatsModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TABLENAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                ChatsModel newModel = new ChatsModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                returnList.add(newModel);
            } while (cursor.moveToNext());
        } else {
            return returnList;
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public void clearChatRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + TABLENAME;
        db.execSQL(queryString);
    }
}
