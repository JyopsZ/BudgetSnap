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

public class account extends AppCompatActivity {
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
        userImageTextView = findViewById(R.id.profilePicture);


        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        Intent intent = getIntent();
        UNum = intent.getStringExtra("PK_UNUM");

        if (UNum == null || UNum.isEmpty()) {
            Log.e(TAG, "UNum is null or empty. Cannot query database.");
            return;
        }

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
            byte[] userImageBlob = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.UIMAGE)); // Get the BLOB

            // Log retrieved values
            Log.d(TAG, "User details - UName: " + userName + ", UEmail: " + userEmail);

            // Set text for name and email
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);

            // Convert BLOB to Bitmap and display in ImageView
            if (userImageBlob != null) {
                Bitmap userImageBitmap = BitmapFactory.decodeByteArray(userImageBlob, 0, userImageBlob.length);
                userImageTextView.setImageBitmap(userImageBitmap);
            } else {
                // Optionally set a default image if no image is found
                userImageTextView.setImageResource(R.drawable.profile_boy1);
            }

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
            cursor = database.rawQuery(query, new String[]{UNum});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String tName = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                    String tDate = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                    String tAmount = cursor.getString(cursor.getColumnIndexOrThrow("TAmount"));
                    String cName = cursor.getString(cursor.getColumnIndexOrThrow("CName"));

                    AccountList.add(new AccountList(tName, tDate, tAmount, cName));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AccountAdapter(AccountList);
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }


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

    public void goEdit(View v) {
        Intent intent = new Intent(this, accountEditProfile.class);
        intent.putExtra("PK_UNUM", UNum);
        startActivity(intent);
    }
}