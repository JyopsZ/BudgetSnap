package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class budgetingAddExpense extends AppCompatActivity {

    private EditText editEnterName, editEnterAmount, editTextDate;
    private Spinner spinnerCategory;
    private Button buttonBudgeting;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budgeting_add_expense);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper (SQLite)
        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        editEnterName = findViewById(R.id.editEnterName);
        editEnterAmount = findViewById(R.id.editTextNumberDecimal);
        editTextDate = findViewById(R.id.editTextDate2);
        spinnerCategory = findViewById(R.id.spinnerAddbudget);
        buttonBudgeting = findViewById(R.id.buttonBudgeting);

        // Set up Spinner for categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Handle Add Budget button
        buttonBudgeting.setOnClickListener(v -> addBudgetToSQLite());
    }

    private void addBudgetToSQLite() {
        // Get user input
        String name = editEnterName.getText().toString().trim();
        String amount = editEnterAmount.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate input fields
        if (name.isEmpty() || amount.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data in SQLite Database
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Generate a new unique BNum
            String newBNum = generateNewBNum(db);

            ContentValues values = new ContentValues();
            values.put("BNum", newBNum); // Generated unique BNum
            values.put("BName", name); // Budget name
            values.put("BExpense", Double.parseDouble(amount)); // Expense amount
            values.put("BDate", date); // Date of expense
            values.put("UNum", "default-user-id"); // Replace with actual user ID

            long result = db.insert("BUDGET", null, values);

            if (result != -1) {
                Toast.makeText(this, "Budget added successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to add budget.", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateNewBNum(SQLiteDatabase db) {
        String newBNum = "BT0001"; // Default for first entry
        try {
            String query = "SELECT BNum FROM BUDGET ORDER BY BNum DESC LIMIT 1";
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    String lastBNum = cursor.getString(0); // Get the last BNum
                    int numericPart = Integer.parseInt(lastBNum.substring(2)); // Remove "BT" prefix and parse as int
                    numericPart++; // Increment numeric part
                    newBNum = "BT" + String.format("%04d", numericPart); // Format as BT000X
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error generating BNum: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return newBNum;
    }
    private void clearFields() {
        editEnterName.setText("");
        editEnterAmount.setText("");
        editTextDate.setText("");
        spinnerCategory.setSelection(0);
    }

    public void gohome(View v) {
        Intent i = new Intent(budgetingAddExpense.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(budgetingAddExpense.this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(budgetingAddExpense.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(budgetingAddExpense.this, account.class);
        startActivity(i);
    }

    public void back(View v) {
        finish();
    }
}
