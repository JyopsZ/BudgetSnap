package com.example.budgetsnap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

    // TextViews for totals
    private TextView totalBudgetTextView, totalExpensesTextView, budgetLimitTextView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting1);

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
    }

    // Fetch budget data from SQLite database
    private void loadBudgetsFromDatabase() {
        budList = new ArrayList<>();

        // Prepopulate categories with default values
        String[] defaultCategories = {
                "Food", "Transportation", "Health", "Utilities", "Education", "Entertainment", "Savings", "Others"
        };
        for (String category : defaultCategories) {
            budList.add(new Budget(category, "No title", 0.0, 0.0)); // Default values
        }

        // Get readable database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query to retrieve budget data
        String query = "SELECT CATEGORIES.CName, BName, BBudget, BExpense " +
                "FROM BUDGET " +
                "INNER JOIN CATEGORIES ON BUDGET.BNum = CATEGORIES.CNum";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Fetch values for each budget entry
                String category = cursor.getString(cursor.getColumnIndexOrThrow("CName"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("BName"));
                double totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("BBudget"));
                double totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("BExpense"));

                // Update or add to the list
                boolean updated = false;
                for (Budget budget : budList) {
                    if (budget.getCategory().equals(category)) {
                        budget.setTitle(title);
                        budget.setRemaining(totalBudget);
                        budget.setExpenses(totalExpenses);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    // If category was not in the default list, add it
                    budList.add(new Budget(category, title, totalBudget, totalExpenses));
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

    // Navigate to the Edit Category activity
    public void editCategory(View v) {
        Intent i = new Intent(budgeting1.this, budgetingEditCategory.class);
        startActivity(i);
    }

    // Navigate to Add Expense activity
    public void addExpense(View v) {
        Intent i = new Intent(budgeting1.this, budgetingAddExpense.class);
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
    protected void onResume() {
        super.onResume();
        // Refresh data and totals in case of data changes
        loadBudgetsFromDatabase();
        calculateAndDisplayTotalsFromTransactions();
    }
}
