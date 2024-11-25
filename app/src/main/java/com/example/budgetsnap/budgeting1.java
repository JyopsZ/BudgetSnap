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

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + PK_Unum, Toast.LENGTH_SHORT).show();

        // Fetch data from the database and populate the RecyclerView
        loadBudgetsFromDatabase();

        calculateAndDisplayExpenses();

        calculateAndDisplayBudget();

        calculateRemainingBalance();

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

        // Query to fetch budget and expense data for each category
        String query = "SELECT CATEGORIES.CName, " +
                "IFNULL(SUM(BUDGET_CATEGORY.BCBudget), 0) AS TotalBudget, " +
                "IFNULL(SUM(BUDGET_ADD.BAExpense), 0) AS TotalExpenses " +
                "FROM CATEGORIES " +
                "LEFT JOIN BUDGET_CATEGORY ON CATEGORIES.CNum = BUDGET_CATEGORY.CNum AND BUDGET_CATEGORY.BNum = ? " +
                "LEFT JOIN BUDGET_ADD ON BUDGET_ADD.CNum = CATEGORIES.CNum AND BUDGET_ADD.BNum = ? " +
                "GROUP BY CATEGORIES.CName";

        String currentBNum = getBNumForCurrentUser();

        if (currentBNum == null || currentBNum.isEmpty()) {
            Toast.makeText(this, "No budget found for current user", Toast.LENGTH_SHORT).show();
            return;
        }

        try (Cursor cursor = db.rawQuery(query, new String[]{currentBNum, currentBNum})) {
            if (cursor.moveToFirst()) {
                do {
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("CName"));
                    double totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalBudget"));
                    double totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalExpenses"));

                    // Calculate remaining budget
                    double remaining = totalBudget - totalExpenses;

                    // Add the category with budget, expenses, and calculated remaining
                    budList.add(new Budget(categoryName, remaining, totalExpenses));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error loading budgets: " + e.getMessage());
            Toast.makeText(this, "Error loading budgets", Toast.LENGTH_SHORT).show();
        }

        // Update the RecyclerView adapter with the updated list
        adapter = new BudgetingAdapter(budList);
        recyclerView.setAdapter(adapter);
    }

    private void calculateAndDisplayExpenses() {
        double totalExpenses = 0.0;

        String currentBNum = getBNumForCurrentUser();
        if (currentBNum == null || currentBNum.isEmpty()) {
            Toast.makeText(this, "No budget found for current user", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Budgeting1", "Current BNum: " + currentBNum);

        // Query to sum up the BAExpense column from the BUDGET_ADD table
        String query = "SELECT IFNULL(SUM(BAExpense), 0) AS TotalExpenses FROM BUDGET_ADD WHERE BNum = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{currentBNum})) {
            if (cursor.moveToFirst()) {
                totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalExpenses"));
                Log.d("Budgeting1", "Total Expenses: " + totalExpenses);
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error calculating total expenses: " + e.getMessage());
            Toast.makeText(this, "Error calculating expenses", Toast.LENGTH_SHORT).show();
        }

        // Update the TextView with the calculated total expenses
        TextView totalExpensesTextView = findViewById(R.id.textView8);
        totalExpensesTextView.setText(String.format("Php %.2f", totalExpenses));
    }

    private void calculateAndDisplayBudget() {
        double totalBudget = 0.0;

        // Query to sum up the BCBudget column from the BUDGET_CATEGORY table
        String query = "SELECT IFNULL(SUM(BCBudget), 0) AS TotalBudget FROM BUDGET_CATEGORY WHERE BNum = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{getBNumForCurrentUser()})) {
            if (cursor.moveToFirst()) {
                totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalBudget"));
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error calculating total budget: " + e.getMessage());
            Toast.makeText(this, "Error calculating budget", Toast.LENGTH_SHORT).show();
        }

        // Update the TextView with the calculated total budget
        TextView totalBudgetTextView = findViewById(R.id.textView3);
        totalBudgetTextView.setText(String.format("Php %.2f", totalBudget));
    }

    private void calculateRemainingBalance() {
        double totalBudget = 0.0;
        double totalExpenses = 0.0;
        double remainingBalance;

        // Query to sum up the BCBudget column from the BUDGET_CATEGORY table
        String budgetQuery = "SELECT IFNULL(SUM(BCBudget), 0) AS TotalBudget FROM BUDGET_CATEGORY WHERE BNum = ?";
        try (Cursor cursor = db.rawQuery(budgetQuery, new String[]{getBNumForCurrentUser()})) {
            if (cursor.moveToFirst()) {
                totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalBudget"));
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error calculating total budget: " + e.getMessage());
            Toast.makeText(this, "Error calculating budget", Toast.LENGTH_SHORT).show();
        }

        // Query to sum up the BAExpense column from the BUDGET_ADD table
        String expensesQuery = "SELECT IFNULL(SUM(BAExpense), 0) AS TotalExpenses FROM BUDGET_ADD WHERE BNum = ?";
        try (Cursor cursor = db.rawQuery(expensesQuery, new String[]{getBNumForCurrentUser()})) {
            if (cursor.moveToFirst()) {
                totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalExpenses"));
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error calculating total expenses: " + e.getMessage());
            Toast.makeText(this, "Error calculating expenses", Toast.LENGTH_SHORT).show();
        }

        // Calculate remaining balance
        remainingBalance = totalBudget - totalExpenses;

        // Update the TextView with the remaining balance
        TextView remainingBalanceTextView = findViewById(R.id.textView10);
        remainingBalanceTextView.setText(String.format("Php %.2f", remainingBalance));
    }

    private String getBNumForCurrentUser() {
        String bNum = null;
        String query = "SELECT BNum FROM BUDGET WHERE UNum = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{PK_Unum})) {
            if (cursor.moveToFirst()) {
                bNum = cursor.getString(0); // Get the first result
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching BNum: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return bNum;
    }

    // Navigate to Add Expenses for Budget (Data Input)
    public void addExpense(View v) {
        // Fetch BNum for the current user
        String PK_BNum = getBNumForCurrentUser();

        // Pass BNum via Intent
        Intent i = new Intent(budgeting1.this, budgetingAddExpense.class);
        i.putExtra("PK_BNUM", PK_BNum); // Pass BNum to the next activity
        startActivity(i); // Launch activity for result
    }

    // Navigate to the Edit Category activity
    public void editCategory(View v) {
        String PK_BNum = getBNumForCurrentUser();

        Intent i = new Intent(budgeting1.this, budgetingEditCategory.class);
        i.putExtra("PK_BNUM", PK_BNum); // Pass BNum to the next activity
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
}
