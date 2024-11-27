package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class budgetingAddExpense extends AppCompatActivity {

    private EditText editEnterName, editEnterAmount;
    private Spinner spinnerCategory;
    private Button buttonBudgeting;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String PK_BNUM;
    private List<Budget> budList;
    SQLiteOpenHelper databaseHelper;
    private LinkedHashMap<String, String> categoryMap; // To store category CNUM -> CNAME mapping
    private String PK_Unum;

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
        db = dbHelper.getWritableDatabase();

        // Initialize UI components
        editEnterName = findViewById(R.id.editEnterName);
        editEnterAmount = findViewById(R.id.editTextNumberDecimal);
        spinnerCategory = findViewById(R.id.spinnerAddbudget);
        buttonBudgeting = findViewById(R.id.buttonBudgeting);

        // Fetch categories and populate Spinner
        fetchCategoriesFromDB();

        // Set up Spinner for categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(categoryMap.values()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Handle Add Expense button
        buttonBudgeting.setOnClickListener(v -> addExpenseToDatabase());

        // CURRENT USER'S NUMBER
        PK_BNUM = getIntent().getStringExtra("PK_BNUM");
        Toast.makeText(this, "Current User: " + PK_BNUM, Toast.LENGTH_SHORT).show();
    }

    // getting Category name
    private void fetchCategoriesFromDB() {
        categoryMap = new LinkedHashMap<>();
        try (Cursor cursor = db.rawQuery("SELECT CNUM, CNAME FROM CATEGORIES", null)) {
            while (cursor.moveToNext()) {
                String cnum = cursor.getString(0);
                String cname = cursor.getString(1);
                categoryMap.put(cnum, cname);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addExpenseToDatabase() {
        // Get user input
        String name = editEnterName.getText().toString().trim();
        String amountText = editEnterAmount.getText().toString().trim();
        String categoryName = spinnerCategory.getSelectedItem().toString();

        // Validate inputs
        if (name.isEmpty() || amountText.isEmpty() || categoryName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // Validate and fetch `CNum` for the selected category
            String categoryCNUM = getCategoryCNUM(categoryName);
            if (categoryCNUM == null) {
                Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate that `BNum` exists
            if (PK_BNUM == null || PK_BNUM.isEmpty()) {
                Toast.makeText(this, "No budget assigned to the current user.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a new `BANum`
            String newBANum = generateNewBANum();

            // Insert into SQLite
            ContentValues expenseValues = new ContentValues();
            expenseValues.put("BANum", newBANum);
            expenseValues.put("BAName", name);
            expenseValues.put("BAExpense", amount);
            expenseValues.put("BNum", PK_BNUM);
            expenseValues.put("CNum", categoryCNUM);

            long expenseResult = db.insert("BUDGET_ADD", null, expenseValues);
            if (expenseResult == -1) {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert into Firebase
            Map<String, Object> firebaseData = new HashMap<>();
            firebaseData.put("BAName", name);
            firebaseData.put("BAExpense", amount);
            firebaseData.put("BNum", PK_BNUM);
            firebaseData.put("CNum", categoryCNUM);

            FirebaseFirestore.getInstance().collection("BUDGET_ADD")
                    .document(newBANum)
                    .set(firebaseData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Expense added to Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add expense to Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            clearFields();

            // Navigate back to `budgeting1`
            Intent intent = new Intent(this, budgeting1.class);
            intent.putExtra("PK_UNUM", PK_Unum);
            setResult(RESULT_OK, intent);
            finish();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateNewBANum() {
        String newBANum = "BA0001"; // Default starting ID
        String query = "SELECT BANum FROM BUDGET_ADD ORDER BY BANum DESC LIMIT 1"; // Get the last ID
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                String lastBANum = cursor.getString(0); // Retrieve the last BANum
                int numericPart = Integer.parseInt(lastBANum.substring(2)); // Extract numeric part
                numericPart++; // Increment the numeric part
                newBANum = "BA" + String.format("%04d", numericPart); // Format as BA0001, BA0002, etc.
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error generating new BANum: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return newBANum; // Return the newly generated BANum
    }

    private String getCategoryCNUM(String categoryName) {
        for (String cnum : categoryMap.keySet()) {
            if (categoryMap.get(cnum).equals(categoryName)) {
                return cnum;
            }
        }
        return null;
    }

    private void clearFields() {
        editEnterName.setText("");
        editEnterAmount.setText("");
        spinnerCategory.setSelection(0);
    }

    public void gohome(View v) {
        Intent i = new Intent(this, Home.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void back(View v) {
        Intent i = new Intent(this, budgeting1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }
}
