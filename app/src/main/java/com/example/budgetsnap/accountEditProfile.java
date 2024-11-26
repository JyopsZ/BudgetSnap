package com.example.budgetsnap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class accountEditProfile extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<AccountList> AccountList;
    private FrameLayout frameLayout;

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private ImageView userImageTextView;

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

        recyclerView = findViewById(R.id.recyclerView);
        userNameTextView = findViewById(R.id.profileName);
        userEmailTextView = findViewById(R.id.email);
        userImageTextView = findViewById(R.id.profilePicture);


        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        // Retrieve UNum from the Intent
        Intent intent = getIntent();
        UNum = intent.getStringExtra("PK_UNUM");

        // Log the received UNum
        Log.d(TAG, "Received UNum: " + UNum);

        if (UNum == null || UNum.isEmpty()) {
            Log.e(TAG, "UNum is null or empty. Cannot query database.");
            return;
        }

        // Query user details
        getUserDetails(UNum);

        loadTransactions(UNum);

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
            String userImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UIMAGE)); // Assuming UImage is a String

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


    private void loadTransactions(String UNum) {
        // Initialize the transaction list
        AccountList = new ArrayList<>();

        // SQL query to fetch transaction details
        String query = "SELECT T.TName, T.TDate, T.TAmount, T.TStatus, T.CNum, C.CName, T.TImage " +
                "FROM Transactions T " +
                "JOIN Categories C ON T.CNum = C.CNum " +
                "WHERE T.UNum = ? " +
                "ORDER BY T.TDate DESC";

        Cursor cursor = null;
        try {
            // Execute query and retrieve data
            cursor = database.rawQuery(query, new String[]{UNum});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Fetch transaction details from the cursor
                    String tName = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                    String tDate = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                    String tAmount = cursor.getString(cursor.getColumnIndexOrThrow("TAmount"));
                    boolean tStatus = cursor.getInt(cursor.getColumnIndexOrThrow("TStatus")) > 0;
                    String cName = cursor.getString(cursor.getColumnIndexOrThrow("CName"));


                    // Add transaction to the list
                    AccountList.add(new AccountList(tName, tDate, tAmount, cName));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure the cursor is closed to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }
        }

        // Set up RecyclerView and adapter after loading the transactions
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AccountAdapter(AccountList);
        recyclerView.setAdapter(adapter);
    }

    // Method to return a default image as byte array
    private byte[] getDefaultImageBlob() {
        // Example: Convert a drawable resource to a byte array
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_boy1);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void saveProfile(View view) {
        // Get the updated name from the TextView
        String updatedName = userNameTextView.getText().toString().trim();

        if (updatedName.isEmpty()) {
            Log.e(TAG, "Name is empty, cannot save.");
            return;
        }

        // Update the database
        String updateQuery = "UPDATE " + DatabaseHelper.TABLE_USER +
                " SET " + DatabaseHelper.UNAME + " = ? WHERE " + DatabaseHelper.PK_UNUM + " = ?";
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        try {
            writableDatabase.execSQL(updateQuery, new String[]{updatedName, UNum});
            Log.d(TAG, "User name updated successfully to: " + updatedName);
        } catch (Exception e) {
            Log.e(TAG, "Error updating user name: ", e);
        } finally {
            writableDatabase.close();
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