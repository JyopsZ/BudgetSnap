package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {

        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertUser(UserClass user) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PK_UNUM, user.getUNum());
        values.put(DatabaseHelper.UNAME, user.getName());
        values.put(DatabaseHelper.UPASS, user.getPassword());
        values.put(DatabaseHelper.UBDAY, user.getBirthday());
        values.put(DatabaseHelper.UEMAIL, user.getEmail());
        values.put(DatabaseHelper.UIMAGE, user.getImage());
        values.put(DatabaseHelper.UINCOME, user.getIncome());
        values.put(DatabaseHelper.UEXPENSE, user.getExpense());

        database.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    public Cursor fetchUsers() {
        String[] columns = new String[] { DatabaseHelper.PK_UNUM, DatabaseHelper.UNAME, DatabaseHelper.UPASS, DatabaseHelper.UBDAY, DatabaseHelper.UEMAIL, DatabaseHelper.UIMAGE, DatabaseHelper.UINCOME, DatabaseHelper.UEXPENSE };
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // For Assigning new UNUMs. Get latest in table, then add 1
    public String getUserMax() {

        String maxUNum = "U0000";

        Cursor cursor = database.rawQuery("SELECT MAX(" + DatabaseHelper.PK_UNUM + ") FROM " + DatabaseHelper.TABLE_USER, null);

        cursor.moveToFirst();
        maxUNum = cursor.getString(0);

        cursor.close();
        return maxUNum;
    }


}
