package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class budgeting1 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BudgetingAdapter adapter;
    private List<Budget> budList;
    private String PK_Unum;
    private SQLiteDatabase db;

    // TextViews for totals
    private TextView totalBudgetTextView, totalExpensesTextView, budgetLimitTextView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting1);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Handle insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TextViews for totals
        totalBudgetTextView = findViewById(R.id.textView3); // Total Budget
        totalExpensesTextView = findViewById(R.id.textView8); // Total Expenses
        budgetLimitTextView = findViewById(R.id.textView10); // Budget Limit

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Fetch data from the database and populate the RecyclerView
        loadBudgetsFromDatabase();

        // Display totals from transactions
        calculateAndDisplayTotalsFromTransactions();

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + PK_Unum, Toast.LENGTH_SHORT).show();

        checkAndGenerateBNum();
    }

    private void checkAndGenerateBNum() {
        // Retrieve the current user's number
        String currentUnum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + currentUnum, Toast.LENGTH_SHORT).show();

        if (currentUnum != null && !currentUnum.isEmpty()) {
            // Check if the user already has an associated BNum
            boolean userExists = checkUserExists(currentUnum);

            if (!userExists) {
                // Generate a new BNum for the new user
                String newBNum = generateNewBNum();
                if (newBNum != null) {
                    // Save the new BNum and associate it with the current user
                    saveNewBNumForUser(currentUnum, newBNum);
                    Toast.makeText(this, "New BNum generated: " + newBNum, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to generate a new BNum.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User already exists with a BNum.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user information provided.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if the user already has an associated BNum
    private boolean checkUserExists(String userUnum) {
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM BUDGET WHERE UNum = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{userUnum})) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0; // If the count is greater than 0, the user exists
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error checking user existence: " + e.getMessage());
        }
        return exists;
    }

    // Method to save the new BNum for the user
    private void saveNewBNumForUser(String userUnum, String bNum) {
        try {
            ContentValues values = new ContentValues();
            values.put("BNum", bNum);
            values.put("UNum", userUnum);
            db.insert("BUDGET", null, values);
        } catch (Exception e) {
            Log.e("SQLiteError", "Error saving new BNum for user: " + e.getMessage());
        }
    }

    // Method to generate a new BNum (from earlier)
    private String generateNewBNum() {
        String newBNum = "B0001"; // Default ID for the first entry
        try {
            String query = "SELECT BNum FROM BUDGET ORDER BY BNum DESC LIMIT 1";
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    String lastBNum = cursor.getString(0);
                    int numericPart = Integer.parseInt(lastBNum.substring(1));
                    numericPart++;
                    newBNum = "B" + String.format("%04d", numericPart);
                }
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error generating new BNum ID: " + e.getMessage());
        }
        return newBNum;
    }

    private void loadBudgetsFromDatabase() {
        budList = new ArrayList<>();

        // Prepopulate categories with default values
        String[] defaultCategories = {
                "Food", "Transportation", "Health", "Utilities", "Education", "Entertainment", "Savings", "Others"
        };
        for (String category : defaultCategories) {
            budList.add(new Budget(category, 0.0, 0.0)); // Default values
        }

        // Get readable database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Updated query
        String query = "SELECT CATEGORIES.CName, BUDGET_CATEGORY.BCBudget, " +
                "IFNULL(SUM(BUDGET_ADD.BAExpense), 0) AS BExpense " +
                "FROM BUDGET_CATEGORY " +
                "INNER JOIN CATEGORIES ON BUDGET_CATEGORY.CNum = CATEGORIES.CNum " +
                "LEFT JOIN BUDGET_ADD ON BUDGET_CATEGORY.CNum = BUDGET_ADD.CNum " +
                "GROUP BY CATEGORIES.CName, BUDGET_CATEGORY.BCBudget";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Fetch values for each budget entry
                String category = cursor.getString(cursor.getColumnIndexOrThrow("CName"));
                double totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("BCBudget")); // Budgeted amount
                double totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("BExpense")); // Calculated expenses

                // Update or add to the list
                boolean updated = false;
                for (Budget budget : budList) {
                    if (budget.getCategory().equals(category)) {
                        budget.setRemaining(totalBudget);
                        budget.setExpenses(totalExpenses);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    // Add new Budget to the list
                    budList.add(new Budget(category, totalBudget, totalExpenses));
                }
            } while (cursor.moveToNext());
        }

        cursor.close(); // Close the cursor
        db.close();     // Close the database

        // Set adapter with updated list
        adapter = new BudgetingAdapter(budList);
        recyclerView.setAdapter(adapter);
    }

    // Calculate and display totals for budget and expenses from transactions
    private void calculateAndDisplayTotalsFromTransactions() {
        double totalBudget = 0;
        double totalExpenses = 0;

        // Get readable database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query to calculate total budget from "money in" transactions
        String queryMoneyIn = "SELECT SUM(TAmount) AS TotalMoneyIn FROM TRANSACTIONS WHERE TStatus = 1";
        Cursor cursorMoneyIn = db.rawQuery(queryMoneyIn, null);
        if (cursorMoneyIn.moveToFirst()) {
            totalBudget = cursorMoneyIn.getDouble(cursorMoneyIn.getColumnIndexOrThrow("TotalMoneyIn"));
        }
        cursorMoneyIn.close();

        // Query to calculate total expenses from "money out" transactions
        String queryMoneyOut = "SELECT SUM(TAmount) AS TotalMoneyOut FROM TRANSACTIONS WHERE TStatus = 0";
        Cursor cursorMoneyOut = db.rawQuery(queryMoneyOut, null);
        if (cursorMoneyOut.moveToFirst()) {
            totalExpenses = cursorMoneyOut.getDouble(cursorMoneyOut.getColumnIndexOrThrow("TotalMoneyOut"));
        }
        cursorMoneyOut.close();

        db.close(); // Close the database

        // Calculate remaining budget
        double remainingBudget = totalBudget - totalExpenses;

        // Update TextViews with calculated totals
        totalBudgetTextView.setText("Php " + String.format("%.2f", totalBudget));
        totalExpensesTextView.setText("Php " + String.format("%.2f", totalExpenses));
        budgetLimitTextView.setText("Budget Limit: Php " + String.format("%.2f", remainingBudget));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data and totals in case of data changes
        loadBudgetsFromDatabase();
        calculateAndDisplayTotalsFromTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh data when returning from budgetingAddExpense
            loadBudgetsFromDatabase();
            calculateAndDisplayTotalsFromTransactions();
        }
    }

    public void addExpense(View v) {
        Intent i = new Intent(budgeting1.this, budgetingAddExpense.class);
        startActivityForResult(i, 1); // Launch activity for result
    }

    // Navigate to the Edit Category activity
    public void editCategory(View v) {
        Intent i = new Intent(budgeting1.this, budgetingEditCategory.class);
        startActivity(i);
    }

    // Navigate to Home activity
    public void gohome(View v) {
        Intent i = new Intent(budgeting1.this, Home.class);
        startActivity(i);
    }

    // Navigate to Transactions activity
    public void gotransactions(View v) {
        Intent i = new Intent(budgeting1.this, Transaction1.class);
        startActivity(i);
    }

    // Navigate to Categories activity
    public void gocategories(View v) {
        Intent i = new Intent(budgeting1.this, categories_main.class);
        startActivity(i);
    }

    // Navigate to Account activity
    public void goaccount(View v) {
        Intent i = new Intent(budgeting1.this, account.class);
        startActivity(i);
    }

    // Go back to the previous activity
    public void back(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
