package com.example.budgetsnap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "budget.db";
    private static final int DB_VERSION = 17;

    // User
    public static final String TABLE_USER = "USER";
    public static final String PK_UNUM = "UNum";
    public static final String UNAME = "UName";
    public static final String UPASS = "UPass";
    public static final String UBDAY = "UBday";
    public static final String UEMAIL = "UEmail";
    public static final String UIMAGE = "UImage";
    public static final String UINCOME = "UIncome";
    public static final String UEXPENSE = "UExpense";

    public static final String TABLE_FRIENDS = "FRIENDS";
    public static final String FK_FNUM = "FNum";
    public static final String FK_FUNUM = "UNum";

    // Savings
    public static final String TABLE_SAVINGS = "SAVINGS";
    public static final String PK_SNUM = "SNum";
    public static final String SNAME = "SName";
    public static final String SCURRENTAMOUNT = "SCurrentAmount";
    public static final String SGOALAMOUNT = "SGoalAmount";
    public static final String SFREQUENCY = "SFrequency";
    public static final String SDATE = "SDate";
    public static final String SSTATUS = "SStatus";
    public static final String FK_SUNUM = "UNum";


    // Transactions
    public static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    public static final String PK_TNUM = "TNum";
    public static final String TNAME = "TName";
    public static final String TDATE = "TDate";
    public static final String TTIME = "TTime";
    public static final String TAMOUNT = "TAmount";
    public static final String TIMAGE = "TImage";
    public static final String TSTATUS = "TStatus";
    public static final String FK_TCNUM = "CNum";
    public static final String FK_TUNUM = "UNum";

    // Categories
    public static final String TABLE_CATEGORIES = "CATEGORIES";
    public static final String PK_CNUM = "CNum";
    public static final String CNAME = "CName";

    // Budget Tables
    public static final String TABLE_BUDGET = "BUDGET";
    public static final String PK_BNUM = "BNum";
    public static final String FK_BUNUM = "UNum";

    public static final String TABLE_BUDGET_CATEGORY = "BUDGET_CATEGORY";
    public static final String PK_BCNUM = "BCNum";
    public static final String BCBUDGET = "BCBudget";
    public static final String FK_BCBNUM = "BNum";
    public static final String FK_BCCNUM = "CNum";

    public static final String TABLE_BUDGET_ADD = "BUDGET_ADD";
    public static final String PK_BANUM = "BANum";
    public static final String BANAME = "BAName";
    public static final String BAEXPENSE = "BAExpense";
    public static final String FK_BABNUM = "BNum";
    public static final String FK_BACNUM = "CNum";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //USER
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + PK_UNUM + " TEXT PRIMARY KEY,"
                + UNAME + " TEXT,"
                + UPASS + " TEXT,"
                + UBDAY + " TEXT,"
                + UEMAIL + " TEXT,"
                + UIMAGE + " BLOB,"
                + UINCOME + " DOUBLE,"
                + UEXPENSE + " DOUBLE"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        //FRIENDS
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + "("
                + FK_FNUM + " TEXT PRIMARY KEY,"
                + FK_FUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_FUNUM + ") REFERENCES " + TABLE_USER + "(" + FK_FUNUM + ")"
                + ")";
        db.execSQL(CREATE_FRIENDS_TABLE);
        //   db.execSQL("" +
        //                "('F0001', ), " +


        //SAVINGS
        String CREATE_SAVINGS_TABLE = "CREATE TABLE " + TABLE_SAVINGS + "("
                + PK_SNUM + " TEXT PRIMARY KEY,"
                + SNAME + " TEXT,"
                + SCURRENTAMOUNT + " DOUBLE,"
                + SGOALAMOUNT + " DOUBLE,"
                + SFREQUENCY + " TEXT,"
                + SDATE + " TEXT,"
                + SSTATUS + " BOOLEAN,"
                + FK_SUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_SUNUM + ") REFERENCES " + TABLE_USER + "(" + PK_UNUM + ")"
                + ")";
        db.execSQL(CREATE_SAVINGS_TABLE);

        //TRANSACTIONS
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + PK_TNUM + " TEXT PRIMARY KEY,"
                + TNAME + " TEXT,"
                + TDATE + " TEXT,"
                + TTIME + " TEXT,"
                + TAMOUNT + " DOUBLE,"
                + TIMAGE + " BLOB,"
                + TSTATUS + " BOOLEAN,"
                + FK_TCNUM + " TEXT,"
                + FK_TUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_TCNUM + ") REFERENCES " + TABLE_CATEGORIES + "(" + PK_CNUM + "),"
                + "FOREIGN KEY(" + FK_TUNUM + ") REFERENCES " + TABLE_USER + "(" + PK_UNUM + ")"
                + ")";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    //   db.execSQL("" +
        //                "('T0001', ), " +

        // CATEGORIES
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + PK_CNUM + " TEXT PRIMARY KEY,"
                + CNAME + " TEXT"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        db.execSQL("INSERT INTO CATEGORIES(CNUM, CNAME) VALUES " +
                "('C0001', 'Home'), " +
                "('C0002', 'Food'), " +
                "('C0003', 'Bills'), " +
                "('C0004', 'Health'), " +
                "('C0005', 'Leisure'), " +
                "('C0006', 'Education'), " +
                "('C0007', 'Transportation'), " +
                "('C0008', 'Savings'), " +
                "('C0009', 'Others')");

        // BUDGET
        String CREATE_BUDGET_TABLE = "CREATE TABLE " + TABLE_BUDGET + "("
                + PK_BNUM + " TEXT PRIMARY KEY,"
                + FK_BUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_BUNUM + ") REFERENCES " + TABLE_USER + "(" + PK_UNUM + ")"
                + ")";
        db.execSQL(CREATE_BUDGET_TABLE);
        //   db.execSQL("" +
        //                "('B0001', ), " +

        // BUDGET_CATEGORY
        String CREATE_BUDGET_CATEGORY_TABLE = "CREATE TABLE " + TABLE_BUDGET_CATEGORY + "("
                + PK_BCNUM + " TEXT PRIMARY KEY,"
                + BCBUDGET + " DOUBLE,"
                + FK_BCBNUM + " TEXT,"
                + FK_BCCNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_BCBNUM + ") REFERENCES " + TABLE_BUDGET + "(" + PK_BNUM + "),"
                + "FOREIGN KEY(" + FK_BCCNUM + ") REFERENCES " + TABLE_CATEGORIES + "(" + PK_CNUM + ")"
                + ")";
        db.execSQL(CREATE_BUDGET_CATEGORY_TABLE);
        //   db.execSQL("" +
        //                "('BC0001', ), " +

        // BUDGET_ADD
        String CREATE_BUDGET_ADD_TABLE = "CREATE TABLE " + TABLE_BUDGET_ADD + "("
                + PK_BANUM + " TEXT PRIMARY KEY,"
                + BANAME + " TEXT,"
                + BAEXPENSE + " DOUBLE,"
                + FK_BABNUM + " TEXT,"
                + FK_BACNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_BABNUM + ") REFERENCES " + TABLE_BUDGET + "(" + PK_BNUM + "),"
                + "FOREIGN KEY(" + FK_BACNUM + ") REFERENCES " + TABLE_CATEGORIES + "(" + PK_CNUM + ")"
                + ")";
        db.execSQL(CREATE_BUDGET_ADD_TABLE);
        //   db.execSQL("" +
        //                "('BA0001', ), " +
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET_ADD);
        onCreate(db);
    }

    public void updateUserName(String UNum, String newUserName) {
        String query = "UPDATE " + TABLE_USER +
                " SET " + UNAME + " = ? " +
                " WHERE " + PK_UNUM + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query, new Object[]{newUserName, UNum});
        db.close();
    }

}
