package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class account extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageBG, imageLogo, imageBadge, imageBell, searchBar, Search_Button;
    TextView textTransactions, dateText, textRestaurant, textCategory, textPrice, viewImage, Search_Text;
    Spinner dropdown_menu;
    FrameLayout frameLayout;

    private static final String[] paths = {"Sort By Amount", "Low-High", "High-Low"};
    private List<Transaction> transactionList; // List to store transaction data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize views properly
        frameLayout = findViewById(R.id.frameLayout);
        textRestaurant = findViewById(R.id.textRestaurant);
        textCategory = findViewById(R.id.textCategory);
        textPrice = findViewById(R.id.textPrice);

        // Setup Window Insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            Intent i = new Intent(account.this, transaction_moneyin.class);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            // Start Money Out Activity
            Intent i = new Intent(account.this, transactions_moneyout.class);
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
        transactionList.add(new Transaction("Dunkin' Donut Coffee", "Food", "- Php 150.00", false));
        transactionList.add(new Transaction("Kuya Mel's Chicken", "Food", "- Php 120.00", false));
    }

    private void showTransactionsInConstraintLayout() {
        LinearLayout transactionContainer = findViewById(R.id.transactionContainer);
        transactionContainer.removeAllViews(); // Clear previous views to avoid duplication

        boolean isDateAdded = false; // Flag to ensure the date is displayed only once

        // Loop through the transactions and dynamically add views
        for (Transaction transaction : transactionList) {
            // Adding "Today" text once
            if (!isDateAdded) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText("Today");
                dateTextView.setTextSize(16);
                dateTextView.setTypeface(null, Typeface.BOLD);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                transactionContainer.addView(dateTextView);
                isDateAdded = true;
            }

            // Create a new container for each transaction (to mimic the boxed structure in the image)
            LinearLayout transactionLayout = new LinearLayout(this);
            transactionLayout.setOrientation(LinearLayout.HORIZONTAL);
            transactionLayout.setPadding(0, 16, 0, 16);

            // Create a vertical layout for name and category
            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Create and add the transaction name (bold and dark color)
            TextView transactionNameView = new TextView(this);
            transactionNameView.setText(transaction.getName());
            transactionNameView.setTextSize(18);
            transactionNameView.setTypeface(null, Typeface.BOLD);
            transactionNameView.setTextColor(getResources().getColor(R.color.verydarkcyan)); // Assuming you have this color defined
            textLayout.addView(transactionNameView);

            // Create and add the category (smaller and gray color)
            TextView transactionCategoryView = new TextView(this);
            transactionCategoryView.setText("Category: " + transaction.getCategory());
            transactionCategoryView.setTextSize(14);
            transactionCategoryView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            textLayout.addView(transactionCategoryView);

            // Add the textLayout (for name and category) to the main horizontal container
            transactionLayout.addView(textLayout);

            // Create a vertical layout for the amount and "view image" link
            LinearLayout amountLayout = new LinearLayout(this);
            amountLayout.setOrientation(LinearLayout.VERTICAL);
            amountLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            // Create and add the transaction amount (right-aligned, green for positive, red for negative)
            TextView transactionAmountView = new TextView(this);
            transactionAmountView.setText(transaction.getAmount());
            transactionAmountView.setTextSize(18);
            transactionAmountView.setTextColor(transaction.isPositive() ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.holo_red_light));
            amountLayout.addView(transactionAmountView);

            // Create and add the "view image" link below the amount
            TextView viewImage = new TextView(this);
            viewImage.setText("> view image");
            viewImage.setTextSize(12);
            viewImage.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Assuming "view image" is gray
            amountLayout.addView(viewImage);

            // Add the amountLayout (for amount and "view image") to the main horizontal container
            transactionLayout.addView(amountLayout);

            // Add the transaction layout to the main container
            transactionContainer.addView(transactionLayout);

            // Add a small spacer between transactions
            View spacer = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16);
            spacer.setLayoutParams(params);
            transactionContainer.addView(spacer);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1: // Low-High sorting
                sortTransactionsByAmount(false); // Ascending order
                break;
            case 2: // High-Low sorting
                sortTransactionsByAmount(true); // Descending order
                break;
            default:
                break;
        }
        showTransactionsInConstraintLayout(); // Refresh the UI with the sorted list
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Optional: Handle case where nothing is selected
    }

    private void sortTransactionsByAmount(boolean descending) {
        // Comparator to sort by the numeric value of the transaction amount
        Collections.sort(transactionList, (account, transaction2) -> {
            // Extract the amounts from both transactions, remove any non-numeric characters
            String amount1 = account.getAmount().replaceAll("[^\\d.-]", "");
            String amount2 = transaction2.getAmount().replaceAll("[^\\d.-]", "");

            // Convert to double for comparison
            double value1 = Double.parseDouble(amount1);
            double value2 = Double.parseDouble(amount2);

            // Compare amounts (ascending or descending based on the 'descending' flag)
            return descending ? Double.compare(value2, value1) : Double.compare(value1, value2);
        });
    }

    public void gohome(View v) {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        startActivity(i);
    }
}
