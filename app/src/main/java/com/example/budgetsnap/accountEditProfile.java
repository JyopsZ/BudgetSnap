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

public class accountEditProfile extends AppCompatActivity {
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
        setContentView(R.layout.activity_account_edit_profile);

        frameLayout = findViewById(R.id.frameLayout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view);
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
            // You can show an error message to the user or navigate back
            return;
        }


        // Query the database
        getUserDetails(UNum);

        transactionList = new ArrayList<>();


        /*transactionList.add(new Transaction("The Barn", "Today", "Php 210", false, "Food", null));
        transactionList.add(new Transaction("Electricity", "Yesterday", "Php 290", false, "Bills", null));
        transactionList.add(new Transaction("Angkong", "October 15, 2024", "Php 150", false, "Food", null));
        transactionList.add(new Transaction("PITX", "October 15, 2024", "Php 20", false, "Transportation", null));
        transactionList.add(new Transaction("Water", "October 14, 2024", "Php 1000", false, "Bills", null));
        transactionList.add(new Transaction("Sisig", "October 13, 2024", "Php 150", false, "Food", null));*/

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
