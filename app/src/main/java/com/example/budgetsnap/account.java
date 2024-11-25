package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
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
