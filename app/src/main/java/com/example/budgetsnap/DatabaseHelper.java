package com.example.budgetsnap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "budget.db";
    private static final int DB_VERSION = 1;

    // User
    public static final String TABLE_USER = "USER";
    public static final String PK_UNUM = "UNum";
    public static final String UNAME = "UName";
    public static final String UPASS = "UPass";
    public static final String UBDAY = "UBday";
    public static final String UEMAIL = "UEmail";
    public static final String UINCOME = "UIncome";
    public static final String UEXPENSE = "UExpense";

    public static final String TABLE_FRIENDS = "FRIENDS";
    public static final String FK_FNUM = "FNum";
    public static final String FK_FUNUM = "UNum";

    // Savings
    private static final String TABLE_SAVINGS = "SAVINGS";
    private static final String PK_SNUM = "SNum";
    private static final String SNAME = "SName";
    private static final String SCURRENTAMOUNT = "SCurrentAmount";
    private static final String SGOALAMOUNT = "SGoalAmount";
    private static final String SFREQUENCY = "SFrequency";
    private static final String SDATE = "SDate";
    private static final String SSTATUS = "SStatus";
    private static final String FK_SUNUM = "UNum";

    // Budget
    private static final String TABLE_BUDGET = "BUDGET";
    private static final String PK_BNUM = "BNum";
    private static final String BNAME = "BName";
    private static final String BDATE = "BDate";
    private static final String BBUDGET = "BBudget";
    private static final String BEXPENSE = "BExpense";
    private static final String FK_BUNUM = "UNum";

    // Budget Categories
    private static final String TABLE_BUDGET_CATEGORIES = "BUDGET_CATEGORIES";
    private static final String PK_BCNUM = "BCNum";
    private static final String BCNAME = "BCName";
    private static final String BCDATE = "BCDate";
    private static final String BCEXPENSE = "BExpense";
    private static final String FK_BCNUM = "CNum";
    private static final String FK_BBNUM = "BNum";

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
    private static final String FK_CUNUM = "UNum";

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

        //BUDGET
        String CREATE_BUDGET_TABLE = "CREATE TABLE " + TABLE_BUDGET + "("
                + PK_BNUM + " TEXT PRIMARY KEY,"
                + BNAME + " TEXT,"
                + BDATE + " TEXT,"
                + BBUDGET + " DOUBLE,"
                + BEXPENSE + " DOUBLE,"
                + FK_BUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_BUNUM + ") REFERENCES " + TABLE_USER + "(" + PK_UNUM + ")"
                + ")";
        db.execSQL(CREATE_BUDGET_TABLE);

        //BUDGET CATEGORIES
        String CREATE_BUDGET_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_BUDGET_CATEGORIES + "("
                + PK_BCNUM + " TEXT PRIMARY KEY,"
                + BCNAME + " TEXT,"
                + BCDATE + " TEXT,"
                + BCEXPENSE + " DOUBLE,"
                + FK_BCNUM + " TEXT,"
                + FK_BBNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_BCNUM + ") REFERENCES " + TABLE_CATEGORIES + "(" + PK_CNUM + "),"
                + "FOREIGN KEY(" + FK_BBNUM + ") REFERENCES " + TABLE_BUDGET + "(" + PK_BNUM + ")"
                + ")";
        db.execSQL(CREATE_BUDGET_CATEGORIES_TABLE);

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

        //CATEGORIES
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + PK_CNUM + " TEXT PRIMARY KEY,"
                + CNAME + " TEXT,"
                + FK_CUNUM + " TEXT,"
                + "FOREIGN KEY(" + FK_CUNUM + ") REFERENCES " + TABLE_USER + "(" + PK_UNUM + ")"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

        onCreate(db);
    }
}
