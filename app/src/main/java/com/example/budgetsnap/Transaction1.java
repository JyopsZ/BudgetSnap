package com.example.budgetsnap;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Transaction1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageBG, imageLogo, imageBadge, imageBell;
    SearchView searchBar;
    TextView textTransactions, dateText, textRestaurant, textCategory, textPrice, viewImage;
    Spinner dropdown_menu;
    FrameLayout frameLayout;

    private static final String[] paths = {"Sort By Amount", "Low-High", "High-Low"};
    private List<Transaction> transactionList; // List to store transaction data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction1);

        // Initialize views properly
        imageBG = findViewById(R.id.imageBG);
        imageLogo = findViewById(R.id.imageLogo);
        imageBadge = findViewById(R.id.imageBadge);
        imageBell = findViewById(R.id.imageBell);
        searchBar = findViewById(R.id.searchBar);
        textTransactions = findViewById(R.id.textTransactions);
        frameLayout = findViewById(R.id.frameLayout);
        dateText = findViewById(R.id.dateText);
        textRestaurant = findViewById(R.id.textRestaurant);
        textCategory = findViewById(R.id.textCategory);
        textPrice = findViewById(R.id.textPrice);
        //viewImage = findViewById(R.id.viewImage);
        dropdown_menu = findViewById(R.id.dropdown_menu);

        // Setup Window Insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_menu.setAdapter(adapter);
        dropdown_menu.setOnItemSelectedListener(this); // Set listener for item selection

        // Load hardcoded data
        loadTransactionData();

        // Show the transactions
        showTransactionsInConstraintLayout();
    }

    // hardcoded data
    private void loadTransactionData() {
        transactionList = new ArrayList<>();

        // Hardcoded data
        transactionList.add(new Transaction("Dunkin' Donut Coffee", "Food", "- Php 120.00", false));
        transactionList.add(new Transaction("Academic Commission", "Commission", "+ Php 350.00", true));
        transactionList.add(new Transaction("Kuya Mel's Chicken", "Food", "- Php 120.00", false));
    }

    private void showTransactionsInConstraintLayout() {
        LinearLayout transactionContainer = findViewById(R.id.transactionContainer);

        // assume all transaction is Today for hardcoded data
        boolean isDateAdded = false; // date displyed once

        // Loop through the transactions and dynamically add views
        for (Transaction transaction : transactionList) {
            // Add "Today" text only once at the top
            if (!isDateAdded) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText("Today");  // change when DB is deployed
                dateTextView.setTextSize(16);
                dateTextView.setTypeface(null, Typeface.BOLD);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                transactionContainer.addView(dateTextView);
                isDateAdded = true;  // Set flag to true so that "Today" is added only once
            }

            //transaction name
            TextView transactionNameView = new TextView(this);
            transactionNameView.setText(transaction.getName());
            transactionNameView.setTextSize(18);
            transactionNameView.setTypeface(null, Typeface.BOLD);
            transactionNameView.setTextColor(getResources().getColor(R.color.verydarkcyan));
            transactionContainer.addView(transactionNameView);

            // transaction category
            TextView transactionCategoryView = new TextView(this);
            transactionCategoryView.setText(transaction.getCategory());
            transactionCategoryView.setTextSize(14);
            transactionCategoryView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            transactionContainer.addView(transactionCategoryView);

            // transaction amount
            TextView transactionAmountView = new TextView(this);
            transactionAmountView.setText(transaction.getAmount());
            transactionAmountView.setTextSize(18);
            transactionAmountView.setTextColor(transaction.isPositive() ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.holo_red_light));
            transactionContainer.addView(transactionAmountView);

            // Add a space between each transaction
            View spacer = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 16); // 16px height for space
            spacer.setLayoutParams(params);
            transactionContainer.addView(spacer);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            // Do nothing for "Sort By Amount"
            return;
        }

        switch (position) {
            case 1:
                // Handle "Low-High" selection
                break;
            case 2:
                // Handle "High-Low" selection
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Optional: Code when no item is selected
    }
}
