package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


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
    private EditText profileNameEditText;

    FrameLayout frameLayout;

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
        // Reference to the EditText where the user will modify their profile name
        profileNameEditText = findViewById(R.id.profileName);

        recyclerView = findViewById(R.id.recycler_view);

        transactionList = new ArrayList<>();
        transactionList.add(new Transaction("The Barn", "Today", "Php 210", false, "Food", null));
        transactionList.add(new Transaction("Electricity", "Yesterday", "Php 290", false, "Bills", null));
        transactionList.add(new Transaction("Angkong", "October 15, 2024", "Php 150", false, "Food", null));
        transactionList.add(new Transaction("PITX", "October 15, 2024", "Php 20", false, "Transportation", null));
        transactionList.add(new Transaction("Water", "October 14, 2024", "Php 1000", false, "Bills", null));
        transactionList.add(new Transaction("Sisig", "October 13, 2024", "Php 150", false, "Food", null));

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecentTransactionsAdapter(transactionList);
        recyclerView.setAdapter(adapter);
    }

    // This method will capture the updated profile name and save it (or use it as needed)
    public void saveProfileName(View v) {
        // Get the edited name from the EditText
        String updatedName = profileNameEditText.getText().toString().trim();

        if (!updatedName.isEmpty()) {
            // Optionally, you can save this updated name in shared preferences, database, or pass it to another activity
            Toast.makeText(this, "Profile name saved: " + updatedName, Toast.LENGTH_SHORT).show();
            // Here, you might update the UI or take additional actions as needed
        } else {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Navigation methods
    public void goHome(View v) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goTransactions(View v) {
        Intent intent = new Intent(this, Transaction1.class);
        startActivity(intent);
    }

    public void goCategories(View v) {
        Intent intent = new Intent(this, categories_main.class);
        startActivity(intent);
    }

    public void goAccount(View v) {

        Intent intent = new Intent(this, account.class);
        startActivity(intent);

    }

    public void goSavings(View v) {
        Intent intent = new Intent(this, SavingsActivity.class);
        startActivity(intent);
    }

    public void goEdit(View v) {
        Intent intent = new Intent(this, accountEditProfile.class);
        startActivity(intent);
    }
}
