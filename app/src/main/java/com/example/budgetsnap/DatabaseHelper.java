package com.example.budgetsnap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "budget.db";
    private static final int DB_VERSION = 11;

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
    private static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    private static final String PK_TNUM = "TNum";
    private static final String TNAME = "TName";
    private static final String TDATE = "TDate";
    private static final String TTIME = "TTime";
    private static final String TAMOUNT = "TAmount";
    private static final String TIMAGE = "TImage";
    private static final String TSTATUS = "TStatus";
    private static final String FK_TCNUM = "CNum";
    private static final String FK_TUNUM = "UNum";

    // Categories
    private static final String TABLE_CATEGORIES = "CATEGORIES";
    private static final String PK_CNUM = "CNum";
    private static final String CNAME = "CName";

    // Budget Tables
    private static final String TABLE_BUDGET = "BUDGET";
    private static final String PK_BNUM = "BNum";
    private static final String FK_BUNUM = "UNum";

    private static final String TABLE_BUDGET_CATEGORY = "BUDGET_CATEGORY";
    private static final String PK_BCNUM = "BCNum";
    private static final String BCBUDGET = "BCBudget";
    private static final String FK_BCBNUM = "BNum";
    private static final String FK_BCCNUM = "CNum";

    private static final String TABLE_BUDGET_ADD = "BUDGET_ADD";
    private static final String PK_BANUM = "BANum";
    private static final String BANAME = "BAName";
    private static final String BAEXPENSE = "BAExpense";
    private static final String FK_BABNUM = "BNum";
    private static final String FK_BACNUM = "CNum";

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
                + UIMAGE + " TEXT,"
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
           db.execSQL("INSERT INTO SAVINGS(SNum, SName, SCurrentAmount, SGoalAmount, SFrequency, SDate, SStatus, UNum) VALUES " +
                        "('S0001', 'Concert', 0.0, 5000, 'Daily', '10/17/2024', true, 'U0002' ), " +
                        "('S0002', 'Tuition', 9878.0, 100000, 'Monthly', '01/03/2025', false, 'U0002' )");



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
}
