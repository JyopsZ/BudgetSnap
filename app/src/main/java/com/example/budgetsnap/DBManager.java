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

    // ========================== USERS ==========================
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

    // ========================== SAVINGS ==========================
    public Cursor fetchSavings(String unum) {

        String[] columns = new String[] { DatabaseHelper.PK_SNUM, DatabaseHelper.SNAME, DatabaseHelper.SCURRENTAMOUNT, DatabaseHelper.SGOALAMOUNT, DatabaseHelper.SFREQUENCY, DatabaseHelper.SDATE, DatabaseHelper.SSTATUS, DatabaseHelper.FK_SUNUM };
        Cursor cursor = database.query(DatabaseHelper.TABLE_SAVINGS, columns, DatabaseHelper.FK_SUNUM + "=?", new String[] {unum}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public String getSavingsMax() {

        String maxSNum = "S0000";

        Cursor cursor = database.rawQuery("SELECT MAX(" + DatabaseHelper.PK_SNUM + ") FROM " + DatabaseHelper.TABLE_SAVINGS, null);

        cursor.moveToFirst();
        maxSNum = cursor.getString(0);

        cursor.close();
        return maxSNum;
    }

    public void insertSavings (SavingsClass savings) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PK_SNUM, savings.getSNum());
        values.put(DatabaseHelper.SNAME, savings.getName());
        values.put(DatabaseHelper.SCURRENTAMOUNT, savings.getCurrentAmount());
        values.put(DatabaseHelper.SGOALAMOUNT, savings.getGoalAmount());
        values.put(DatabaseHelper.SFREQUENCY, savings.getFrequency());
        values.put(DatabaseHelper.SDATE, savings.getDateFinish());
        values.put(DatabaseHelper.SSTATUS, savings.getStatus());
        values.put(DatabaseHelper.FK_SUNUM, savings.getUNum());

        database.insert(DatabaseHelper.TABLE_SAVINGS, null, values);
    }

    public void updateSavingsStatus(String snum, boolean status) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SSTATUS, status);

        database.update(DatabaseHelper.TABLE_SAVINGS, values, DatabaseHelper.PK_SNUM + "=?", new String[] {snum});
    }

    public void incrementCurSavings(String snum, double amount) {

        ContentValues values = new ContentValues();
        // get currentAmount of current savings selected
        Cursor cursor = database.rawQuery("SELECT " + DatabaseHelper.SCURRENTAMOUNT + " FROM " + DatabaseHelper.TABLE_SAVINGS + " WHERE " + DatabaseHelper.PK_SNUM + " = ?", new String[] {snum});

        if (cursor.moveToFirst()) {
            double currentAmount = cursor.getDouble(0);
            double newAmount = currentAmount += amount;
            values.put(DatabaseHelper.SCURRENTAMOUNT, newAmount);
            database.update(DatabaseHelper.TABLE_SAVINGS, values, DatabaseHelper.PK_SNUM + "=?", new String[] {snum});
        }
    }

    public SavingsClass getSavingsForEdit(String snum) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SAVINGS + " WHERE " + DatabaseHelper.PK_SNUM + "=?", new String[]{snum});
        SavingsClass savings = null;

        if (cursor.moveToFirst()) {
            savings = new SavingsClass(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PK_SNUM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SNAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.SCURRENTAMOUNT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.SGOALAMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SFREQUENCY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SDATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SSTATUS)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FK_SUNUM))
            );
        }
        cursor.close();
        return savings;
    }

    public void editSavings(String snum, String name, double goalAmount, String frequency, String dateFinish) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SNAME, name);
        values.put(DatabaseHelper.SGOALAMOUNT, goalAmount);
        values.put(DatabaseHelper.SFREQUENCY, frequency);
        values.put(DatabaseHelper.SDATE, dateFinish);

        database.update(DatabaseHelper.TABLE_SAVINGS, values, DatabaseHelper.PK_SNUM + "=?", new String[] {snum});
    }

    public void deleteSavingsGoal(String snum) {

        database.delete(DatabaseHelper.TABLE_SAVINGS, DatabaseHelper.PK_SNUM + "=?", new String[] {snum});
    }


    // ========================== OTHERS ==========================
    public void insertBudgetCategory (String BCNum, double BCBudget, String BNum, String CNum) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PK_BCNUM, BCNum);
        values.put(DatabaseHelper.BCBUDGET, BCBudget);
        values.put(DatabaseHelper.FK_BCBNUM, BNum);
        values.put(DatabaseHelper.FK_BCCNUM, CNum);

        database.insert(DatabaseHelper.TABLE_BUDGET_CATEGORY, null, values);
    }

    public void insertBudget (String BNum, String UNum) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PK_BNUM, BNum);
        values.put(DatabaseHelper.FK_BUNUM, UNum);

        database.insert(DatabaseHelper.TABLE_BUDGET, null, values);
    }

    public void insertBudgetAdd (String BANum, String BAName, double BAExpense, String BNum, String CNum) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PK_BANUM, BANum);
        values.put(DatabaseHelper.BANAME, BAName);
        values.put(DatabaseHelper.BAEXPENSE, BAExpense);
        values.put(DatabaseHelper.FK_BABNUM, BNum);
        values.put(DatabaseHelper.FK_BACNUM, CNum);

        database.insert(DatabaseHelper.TABLE_BUDGET_ADD, null, values);
    }

    // TODO: Remove comments for TImage Part 1 of 1
    public void insertTransaction (String TNum, String TName, String TDate, String TTime, double TAmount, String TImage, String TStatus, String CNum, String UNum) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PK_TNUM, TNum);
        values.put(DatabaseHelper.TNAME, TName);
        values.put(DatabaseHelper.TDATE, TDate);
        values.put(DatabaseHelper.TTIME, TTime);
        values.put(DatabaseHelper.TAMOUNT, TAmount);
        values.put(DatabaseHelper.TIMAGE, TImage);
        values.put(DatabaseHelper.TSTATUS, TStatus);
        values.put(DatabaseHelper.FK_TCNUM, CNum);
        values.put(DatabaseHelper.FK_TUNUM, UNum);

        database.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
    }

    public void deleteSQLInitial () {

        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_USER + ";");
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_SAVINGS + ";");
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_TRANSACTIONS + ";");
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_BUDGET + ";");
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_BUDGET_CATEGORY + ";");
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_BUDGET_ADD + ";");
    }


}
