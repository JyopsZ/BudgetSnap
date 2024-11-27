package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageBG, imageLogo;
    TextView textTransactions;
    Spinner dropdown_menu;
    FrameLayout frameLayout;
    String PK_Unum;

    private static final String[] paths = {"Sort By Amount", "Low - High", "High - Low"};
    private List<Transaction> transactionList; // List to store transaction data
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction1);

        // Initialize views properly
        imageBG = findViewById(R.id.GreenBG);
        imageLogo = findViewById(R.id.Logo);
        textTransactions = findViewById(R.id.textTransactions);
        frameLayout = findViewById(R.id.frameLayout);
        dropdown_menu = findViewById(R.id.dropdown_menu);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

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

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + PK_Unum, Toast.LENGTH_SHORT).show();

        // Load transaction data from the database
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
            i.putExtra("PK_UNUM", PK_Unum);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            // Start Money Out Activity
            Intent i = new Intent(Transaction1.this, transactions_moneyout.class);
            i.putExtra("PK_UNUM", PK_Unum);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        // Handle clicking on the custom "Cancel" button inside your layout
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog on "Cancel"
        });
    }

    private void loadTransactionData() {
        transactionList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Modify the query to filter by the current user's ID (PK_Unum)
        String query = "SELECT TRANSACTIONS.TNum, TName, TDate, TAmount, TStatus, CName, TImage " +
                "FROM TRANSACTIONS " +
                "INNER JOIN CATEGORIES ON TRANSACTIONS.CNum = CATEGORIES.CNum " +
                "WHERE TRANSACTIONS.UNum = ?"; // Filter for the logged-in user

        Cursor cursor = db.rawQuery(query, new String[]{PK_Unum}); // Pass PK_Unum as a parameter

        if (cursor.moveToFirst()) {
            do {
                String tNum = cursor.getString(cursor.getColumnIndexOrThrow("TNum"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("TDate"));
                String amount = cursor.getString(cursor.getColumnIndexOrThrow("TAmount"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("TStatus"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("CName"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("TImage")); // Retrieve BLOB data

                boolean isPositive;

                // Use if-else to check the value of TStatus and set isPositive
                if ("1".equals(status) || "true".equalsIgnoreCase(status)) {
                    isPositive = true; // Income
                } else if ("0".equals(status) || "false".equalsIgnoreCase(status)) {
                    isPositive = false; // Expense
                } else {
                    isPositive = false; // Default to false if status is unexpected
                }

                // TODO: REMOVE
                Toast.makeText(this, "Status: " + isPositive, Toast.LENGTH_SHORT).show();

                // Add the transaction to the list
                transactionList.add(new Transaction(tNum, name, date, amount, isPositive, category, image));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private void showTransactionsInConstraintLayout() {
        LinearLayout transactionContainer = findViewById(R.id.transactionContainer);
        transactionContainer.removeAllViews(); // Clear previous views to avoid duplication

        if (transactionList.isEmpty()) {
            // Display a message if there are no transactions
            TextView noDataTextView = new TextView(this);
            noDataTextView.setText("No transactions available.");
            noDataTextView.setTextSize(16);
            noDataTextView.setTypeface(null, Typeface.BOLD);
            noDataTextView.setTextColor(getResources().getColor(android.R.color.black));
            transactionContainer.addView(noDataTextView);
            return;
        }

        String lastAddedDate = ""; // To track the last added date

        for (Transaction transaction : transactionList) {
            // Get the transaction's date
            String transactionDate = transaction.getDate();

            // Add a date label if it's different from the last one
            if (!transactionDate.equals(lastAddedDate)) {
                TextView dateTextView = new TextView(this);
                dateTextView.setText(transactionDate); // Display the transaction date
                dateTextView.setTextSize(18);
                dateTextView.setTypeface(null, Typeface.BOLD);
                dateTextView.setTextColor(getResources().getColor(android.R.color.black));
                transactionContainer.addView(dateTextView);
                lastAddedDate = transactionDate;
            }

            // Create a layout for the transaction
            LinearLayout transactionLayout = new LinearLayout(this);
            transactionLayout.setOrientation(LinearLayout.HORIZONTAL);
            transactionLayout.setPadding(0, 16, 0, 16);

            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView transactionNameView = new TextView(this);
            transactionNameView.setText(transaction.getName());
            transactionNameView.setTextSize(16);
            transactionNameView.setTypeface(null, Typeface.BOLD);
            transactionNameView.setTextColor(getResources().getColor(R.color.verydarkcyan));
            textLayout.addView(transactionNameView);

            TextView transactionCategoryView = new TextView(this);
            transactionCategoryView.setText("Category: " + transaction.getCategory());
            transactionCategoryView.setTextSize(14);
            transactionCategoryView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            textLayout.addView(transactionCategoryView);

            transactionLayout.addView(textLayout);

            LinearLayout amountLayout = new LinearLayout(this);
            amountLayout.setOrientation(LinearLayout.VERTICAL);
            amountLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView transactionAmountView = new TextView(this);

            // Set the amount text with proper formatting based on transaction type (positive or negative)
            String amountText = (transaction.isPositive() ? "+ Php " : "- Php ") + transaction.getAmount();
            transactionAmountView.setText(amountText);
            // Set text size, bold style, and color
            transactionAmountView.setTextSize(16); // Increased text size for better visibility
            transactionAmountView.setTypeface(null, Typeface.BOLD);
            // Use color based on the transaction type (green for income, red for expense)
            int textColor = transaction.isPositive()
                    ? getResources().getColor(android.R.color.holo_green_light)
                    : getResources().getColor(android.R.color.holo_red_light);
            transactionAmountView.setTextColor(textColor);

            // Add the TextView to your layout (amountLayout)
            amountLayout.addView(transactionAmountView);

            // Create and add the "view image" link below the amount
            TextView viewImageTextView = new TextView(this);
            viewImageTextView.setText("> view image");
            viewImageTextView.setTextSize(12);
            viewImageTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            viewImageTextView.setOnClickListener(v -> {
                byte[] imageData = transaction.getImage(); // Get the image data from the transaction
                if (imageData != null && imageData.length > 0) {
                    showImageDialog(imageData); // Show the dialog with the image
                } else {
                    Toast.makeText(this, "No image available for this transaction.", Toast.LENGTH_SHORT).show();
                }
            });
            amountLayout.addView(viewImageTextView);

            transactionLayout.addView(amountLayout);

            transactionContainer.addView(transactionLayout);

            // Add a spacer between transactions
            View spacer = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16);
            spacer.setLayoutParams(params);
            transactionContainer.addView(spacer);
        }
    }

    private void showImageDialog(byte[] imageData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);

        if (imageData != null && imageData.length > 0) {
            try {
                // Convert byte[] to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageView.setImageBitmap(bitmap); // Set Bitmap to ImageView
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.placeholder_image); // Placeholder for errors
            }
        } else {
            imageView.setImageResource(R.drawable.placeholder_image); // Placeholder if no image
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Utility method to check if two dates are the same day
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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
        Collections.sort(transactionList, (transaction1, transaction2) -> {
            // Check whether the transactions are "money-in" or "money-out"
            boolean isMoneyIn1 = transaction1.isPositive(); // true for "money-in"
            boolean isMoneyIn2 = transaction2.isPositive(); // true for "money-in"

            // Prioritize "money-out" over "money-in"
            if (!isMoneyIn1 && isMoneyIn2) {
                return descending ? 1 : -1; // Money-out first if descending, otherwise money-in first
            } else if (isMoneyIn1 && !isMoneyIn2) {
                return descending ? -1 : 1; // Money-in first if descending, otherwise money-out first
            } else {
                // If both are the same type, sort by numerical amount
                double value1 = Double.parseDouble(transaction1.getAmount().replaceAll("[^\\d.-]", ""));
                double value2 = Double.parseDouble(transaction2.getAmount().replaceAll("[^\\d.-]", ""));

                // Sort by amount
                return descending ? Double.compare(value2, value1) : Double.compare(value1, value2);
            }
        });
        // Refresh the UI after sorting
        showTransactionsInConstraintLayout();
    }

    private void showDeleteTransactionPrompt() {
        if (transactionList.isEmpty()) {
            Toast.makeText(this, "No transactions to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a dialog to show a list of transactions
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Transaction to Delete");

        // Prepare an array of transaction names for the dialog
        String[] transactionNames = new String[transactionList.size()];
        for (int i = 0; i < transactionList.size(); i++) {
            transactionNames[i] = transactionList.get(i).getName() + " - Php " + transactionList.get(i).getAmount();
        }

        // Show transaction names in a single-choice list
        builder.setItems(transactionNames, (dialog, which) -> {
            // Confirm deletion
            confirmDeleteTransaction(which);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void confirmDeleteTransaction(int index) {
        Transaction selectedTransaction = transactionList.get(index);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("Confirm Delete");
        confirmBuilder.setMessage("Are you sure you want to delete " + selectedTransaction.getName() + "?");

        confirmBuilder.setPositiveButton("Yes", (dialog, which) -> {
            deleteTransactionFromDatabase(selectedTransaction);
            transactionList.remove(index);
            showTransactionsInConstraintLayout(); // Update the UI
            Toast.makeText(this, "Transaction deleted.", Toast.LENGTH_SHORT).show();
        });

        confirmBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        confirmBuilder.create().show();
    }


    private void deleteTransactionFromDatabase(Transaction transaction) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        updateIncome(transaction.getName(), transaction.getAmount(), PK_Unum);
        // Delete from SQLite
        String whereClause = "TName = ? AND TAmount = ?";
        String[] whereArgs = {transaction.getName(), transaction.getAmount()};
        db.delete("TRANSACTIONS", whereClause, whereArgs);
        db.close();

        // Delete from Firebase Firestore
        FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
        dbFirebase.collection("TRANSACTIONS")
                .document(transaction.getTNum()) // Assuming TNum is the unique transaction ID
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Transaction deleted from Firebase"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error deleting transaction from Firebase", e));
    }

    private void updateIncome(String tName, String tAmount, String uNum) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT USER.UIncome, TRANSACTIONS.TStatus " +
                        "FROM USER " +
                        "INNER JOIN TRANSACTIONS ON USER.UNum = TRANSACTIONS.UNum " +
                        "WHERE TRANSACTIONS.TName = ? AND TRANSACTIONS.TAmount = ? AND USER.UNum = ?",
                new String[]{tName, tAmount, uNum});

        if (cursor != null && cursor.moveToFirst()) {
            double uIncome = cursor.getDouble(cursor.getColumnIndex("UIncome"));
            int tStatus = cursor.getInt(cursor.getColumnIndex("TStatus"));

            double TAmount = Double.parseDouble(tAmount);

            double newIncome;
            if (tStatus == 1) {
                newIncome = uIncome - TAmount;
                Log.d("TransactionDebug", "TStatus is 1. Subtracting TAmount from UIncome.");
            } else {
                newIncome = uIncome + TAmount;
                Log.d("TransactionDebug", "TStatus is not 1. Adding TAmount to UIncome.");
            }

            ContentValues values = new ContentValues();
            values.put("UIncome", newIncome);
            int rowsUpdated = db.update("USER", values, "UNum = ?", new String[]{uNum});
            Log.d("TransactionDebug", "Rows updated in USER table: " + rowsUpdated);

            cursor.close();
        } else {
            Log.e("TransactionDebug", "No data found for the specified transaction.");
        }

    }
    public void onDeleteTransactionClicked(View view) {
        showDeleteTransactionPrompt();
    }

    public void gohome(View v) {
        Intent i = new Intent(Transaction1.this, Home.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(Transaction1.this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(Transaction1.this, account.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }
}