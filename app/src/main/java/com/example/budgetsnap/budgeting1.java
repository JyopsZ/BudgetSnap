package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        // Populate budget list
        budList = new ArrayList<>();
        budList.add(new Budget("Food", "Monthly Groceries", 1200.00, 800.00));
        budList.add(new Budget("Transport", "Commute Expenses", 300.00, 100.00));
        budList.add(new Budget("Entertainment", "Streaming Services", 100.00, 10.00));
        budList.add(new Budget("Utilities", "Electric Bill", 500.00, 200.00));

        // Set adapter
        adapter = new BudgetingAdapter(budList);
        recyclerView.setAdapter(adapter);
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
}
