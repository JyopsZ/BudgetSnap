package com.example.budgetsnap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class account extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecentTransactionsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Transaction> transactionList;
    FrameLayout frameLayout;

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userImageTextView;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private String UNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize UI elements
        frameLayout = findViewById(R.id.frameLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        // Fetch transactions
        transactionList = fetchTransactionsFromDatabase();
        adapter = new RecentTransactionsAdapter(transactionList);
        recyclerView.setAdapter(adapter);
    }

    private List<Transaction> fetchTransactionsFromDatabase() {
        List<Transaction> transactions = new ArrayList<>();

        String[] columns = {
                "TName",
                "TDate",
                "TAmount",
                "TStatus",
                "CNum",
                "TImage"
        };

        try (Cursor cursor = database.query(
                "TRANSACTIONS",
                columns,
                null,  // Where clause
                null,  // Where args
                null,  // Group by
                null,  // Having
                "TDate DESC" // Order by
        )) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("TAmount"));
                boolean isPositive = cursor.getInt(cursor.getColumnIndexOrThrow("TStatus")) == 1;
                String category = cursor.getString(cursor.getColumnIndexOrThrow("CNum"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("TImage"));

                transactions.add(new Transaction(name, date, String.valueOf(amount), isPositive, category, image));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching transactions: ", e);
        }

        return transactions;
    }


    private void getUserDetails(String UNum) {
        // Define the query
        String query = "SELECT UName, UEmail, UImage FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.PK_UNUM + " = ?";
        Log.d(TAG, "Executing query: " + query + " with UNum: " + UNum);

        Cursor cursor = database.rawQuery(query, new String[]{UNum});
        if (cursor != null && cursor.moveToFirst()) {
            String userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNAME));
            String userEmail = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UEMAIL));
            //String userImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UIMAGE)); // Assuming UImage is a String

            // Log retrieved values
            Log.d(TAG, "User details - UName: " + userName + ", UEmail: " + userEmail);
            //+ ", UImage: " + userImage
            // Display retrieved values
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
            //userImageTextView.setText(userImage);

            cursor.close();
        } else {
            Log.w(TAG, "No user found with UNum: " + UNum);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }


    // Navigation methods
    public void goHome(View v) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }

    public void goTransactions(View v) {
        Intent intent = new Intent(this, Transaction1.class);
        intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }

    public void goCategories(View v) {
        Intent intent = new Intent(this, categories_main.class);
        intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }

    public void goAccount(View v) {
            Intent intent = new Intent(this, account.class);
            intent.putExtra("PK_UNUM", UNum);
            startActivity(intent);
    }

    public void goSavings(View v) {
        Intent intent = new Intent(this, SavingsActivity.class);
        intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }

   public void goEdit(View v) {
        Intent intent = new Intent(this, accountEditProfile.class);
       intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }
}
