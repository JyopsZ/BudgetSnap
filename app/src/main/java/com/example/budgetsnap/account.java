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
    private FrameLayout frameLayout;

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

        frameLayout = findViewById(R.id.frameLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        userNameTextView = findViewById(R.id.profileName);
        userEmailTextView = findViewById(R.id.email);
        //userImageTextView = findViewById(R.id.userImageTextView);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        // Retrieve UNum from the Intent
        Intent intent = getIntent();
        UNum = intent.getStringExtra("PK_UNUM");

        // Log the received UNum
        Log.d(TAG, "Received UNum: " + UNum);

        // Check for null or empty UNum
        if (UNum == null || UNum.isEmpty()) {
            Log.e(TAG, "UNum is null or empty. Cannot query database.");
            return;
        }

        // Query the database
        getUserDetails(UNum);

        // Initialize the transaction list
        transactionList = new ArrayList<>();

        // SQL query to get transaction details
        String query = "SELECT T.TName, T.TDate, T.TAmount, T.TStatus, T.TImage, T.CNum, C.CName " +
                "FROM Transactions T " +
                "JOIN Categories C ON T.CNum = C.CNum " +
                "WHERE T.UNum = ? " +  // Filtering by UNum
                "ORDER BY T.TDate DESC";

        Cursor cursor = database.rawQuery(query, new String[]{UNum});

        if (cursor.moveToFirst()) {
            do {
                String tName = cursor.getString(cursor.getColumnIndex("TName"));
                String tDate = cursor.getString(cursor.getColumnIndex("TDate"));
                String tAmount = cursor.getString(cursor.getColumnIndex("TAmount"));

                // Retrieve TStatus as a string and convert to boolean
                String tStatusString = cursor.getString(cursor.getColumnIndex("TStatus"));
                boolean tStatus = Boolean.parseBoolean(tStatusString);

                // Retrieve TImage as a byte array (Blob)
                byte[] tImage = cursor.getBlob(cursor.getColumnIndex("TImage"));

                int cNum = cursor.getInt(cursor.getColumnIndex("CNum"));
                String cName = cursor.getString(cursor.getColumnIndex("CName"));

                // Add the result to the transactionList
                transactionList.add(new Transaction(tName, tDate, tAmount, tStatus, cName, tImage));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize adapter with transaction list
        adapter = new RecentTransactionsAdapter(transactionList);
        recyclerView.setAdapter(adapter);
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