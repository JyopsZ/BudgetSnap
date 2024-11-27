package com.example.budgetsnap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class categories_select extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Categories> catList;
    private TextView categoryView;
    private String PK_Unum;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_select);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        PK_Unum = getIntent().getStringExtra("PK_UNUM");

        recyclerView = findViewById(R.id.recycler_view);
        categoryView = findViewById(R.id.Category);
        String titleCategory = getIntent().getStringExtra("Title_Category");
        categoryView.setText(titleCategory);

        if ("Home".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Home"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();


    } else if ("Food".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Food"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Bills".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Bills"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Health".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Health"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Education".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Education"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Leisure".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Leisure"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Transportation".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Transportation"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Savings".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Savings"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        } else if ("Others".equals(titleCategory)) {
            catList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT T.TName, T.TDate, T.TAmount " +
                    "FROM TRANSACTIONS T " +
                    "INNER JOIN CATEGORIES C ON T.CNum = C.CNum " +
                    "WHERE T.UNum = ? AND C.CName = ? " +
                    "ORDER BY T.TDate DESC";

            Cursor cursor = db.rawQuery(query, new String[]{PK_Unum, "Others"});
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));

                String amountString = String.valueOf(amount);

                catList.add(new Categories(name, date, amountString));

                Log.d("CategoryQuery", "Transaction Found - Name: " + name + ", Date: " + date + ", Amount: " + amount);
            }
            cursor.close();

            db.close();
        }

        Log.d("CategoryQuery", "Category List Size before setting adapter: " + catList.size());

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CategoriesAdapter(catList);
        recyclerView.setAdapter(adapter);
    }

    public void back(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewCategoryList(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goHome(View v) {
        Intent i = new Intent(this, Home.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goTransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goCategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goAccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }
}
