package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;

public class Transaction1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageBG, imageLogo, imageBadge, imageBell, searchBar, Search_Button;
    TextView textTransactions, dateText, textRestaurant, textCategory, textPrice, viewImage, Search_Text;
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
        textRestaurant = findViewById(R.id.textRestaurant);
        textCategory = findViewById(R.id.textCategory);
        textPrice = findViewById(R.id.textPrice);
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

    // Method triggered when "plus" button is pressed
    public void imageClicked(View view) {
        // Call the method to show the dialog
        showTransactionDialog();
    }

    // Show the "Money In" or "Money Out" prompt
    private void showTransactionDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        // Create the dialog without the default "Cancel" button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);  // cancel button

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking on "Money In" and "Money Out"
        dialogView.findViewById(R.id.moneyInOption).setOnClickListener(v -> {
            // Start Money In Activity
            Intent i = new Intent(Transaction1.this, transaction_moneyin.class);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            // Start Money Out Activity
            Intent i = new Intent(Transaction1.this, transactions_moneyout.class);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        // Handle clicking on the custom "Cancel" button inside your layout
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog on "Cancel"
        });
    }

    // Hardcoded data for transactions
    private void loadTransactionData() {
        transactionList = new ArrayList<>();
        transactionList.add(new Transaction("Dunkin' Donut Coffee", "Food", "- Php 120.00", false));
        transactionList.add(new Transaction("Academic Commission", "Commission", "+ Php 350.00", true));
        transactionList.add(new Transaction("Kuya Mel's Chicken", "Food", "- Php 120.00", false));
    }

    private void showTransactionsInConstraintLayout() {
        LinearLayout transactionContainer = findViewById(R.id.transactionContainer);

        boolean isDateAdded = false; // Date displayed once

        // Loop through the transactions and dynamically add views
        for (Transaction transaction : transactionList) {
            if (!isDateAdded) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText("Today"); // Hardcoded data
                dateTextView.setTextSize(16);
                dateTextView.setTypeface(null, Typeface.BOLD);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                transactionContainer.addView(dateTextView);
                isDateAdded = true;
            }

            TextView transactionNameView = new TextView(this);
            transactionNameView.setText(transaction.getName());
            transactionNameView.setTextSize(18);
            transactionNameView.setTypeface(null, Typeface.BOLD);
            transactionNameView.setTextColor(getResources().getColor(R.color.verydarkcyan));
            transactionContainer.addView(transactionNameView);

            TextView transactionCategoryView = new TextView(this);
            transactionCategoryView.setText(transaction.getCategory());
            transactionCategoryView.setTextSize(14);
            transactionCategoryView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            transactionContainer.addView(transactionCategoryView);

            TextView transactionAmountView = new TextView(this);
            transactionAmountView.setText(transaction.getAmount());
            transactionAmountView.setTextSize(18);
            transactionAmountView.setTextColor(transaction.isPositive() ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.holo_red_light));
            transactionContainer.addView(transactionAmountView);

            View spacer = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 16);
            spacer.setLayoutParams(params);
            transactionContainer.addView(spacer);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle Spinner selection
        switch (position) {
            case 1:
                // Handle "Low-High" sorting
                break;
            case 2:
                // Handle "High-Low" sorting
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Optional: Handle case where nothing is selected
    }
}
