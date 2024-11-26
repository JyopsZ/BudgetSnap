package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class budgetingEditCategory extends AppCompatActivity {

    private CheckBox checkBoxHome, checkBoxFood, checkBoxBills, checkBoxHealth, checkBoxEducation,
            checkBoxLeisure, checkBoxTranspo, checkBoxSavings, checkBoxOthers;
    private EditText editTextHomeAmount, editTextFoodAmount, editTextBillsAmount, editTextHealthAmount,
            editTextEducationAmount, editTextLeisureAmount, editTextTranspoAmount, editTextSavingsAmount, editTextOthersAmount;
    private DatabaseHelper dbHelper;
    private String PK_BNUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budgeting_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SQLite database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize CheckBoxes and EditTexts
        checkBoxHome = findViewById(R.id.checkBoxHome);
        checkBoxFood = findViewById(R.id.checkBoxFood);
        checkBoxBills = findViewById(R.id.checkBoxBills);
        checkBoxHealth = findViewById(R.id.checkBoxHealth);
        checkBoxEducation = findViewById(R.id.checkBoxEducation);
        checkBoxLeisure = findViewById(R.id.checkBoxLeisure);
        checkBoxTranspo = findViewById(R.id.checkBoxTranspo);
        checkBoxSavings = findViewById(R.id.checkBoxSavings);
        checkBoxOthers = findViewById(R.id.checkBoxOthers);

        editTextHomeAmount = findViewById(R.id.editTextHomeAmount);
        editTextFoodAmount = findViewById(R.id.editTextFoodAmount);
        editTextBillsAmount = findViewById(R.id.editTextBillsAmount);
        editTextHealthAmount = findViewById(R.id.editTextHealthAmount);
        editTextEducationAmount = findViewById(R.id.editTextEducationAmount);
        editTextLeisureAmount = findViewById(R.id.editTextLeisureAmount);
        editTextTranspoAmount = findViewById(R.id.editTextTranspoAmount);
        editTextSavingsAmount = findViewById(R.id.editTextSavingsAmount);
        editTextOthersAmount = findViewById(R.id.editTextOthersAmount);

        // Set listeners for CheckBoxes
        setCheckboxListener(checkBoxHome, editTextHomeAmount);
        setCheckboxListener(checkBoxFood, editTextFoodAmount);
        setCheckboxListener(checkBoxBills, editTextBillsAmount);
        setCheckboxListener(checkBoxHealth, editTextHealthAmount);
        setCheckboxListener(checkBoxEducation, editTextEducationAmount);
        setCheckboxListener(checkBoxLeisure, editTextLeisureAmount);
        setCheckboxListener(checkBoxTranspo, editTextTranspoAmount);
        setCheckboxListener(checkBoxSavings, editTextSavingsAmount);
        setCheckboxListener(checkBoxOthers, editTextOthersAmount);

        // Retrieve PK_BNUM from Intent
        PK_BNUM = getIntent().getStringExtra("PK_BNUM");
        Toast.makeText(this, "Current Budget: " + PK_BNUM, Toast.LENGTH_SHORT).show();
    }

    private void setCheckboxListener(CheckBox checkBox, EditText editText) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editText.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
            }
        });
    }

    public void addExpense(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            // Insert data for each checked CheckBox
            insertBudgetCategoryIfChecked(db, checkBoxHome, "C0001", editTextHomeAmount);
            insertBudgetCategoryIfChecked(db, checkBoxFood, "C0002", editTextFoodAmount);
            insertBudgetCategoryIfChecked(db, checkBoxBills, "C0003", editTextBillsAmount);
            insertBudgetCategoryIfChecked(db, checkBoxHealth, "C0004", editTextHealthAmount);
            insertBudgetCategoryIfChecked(db, checkBoxEducation, "C0005", editTextEducationAmount);
            insertBudgetCategoryIfChecked(db, checkBoxLeisure, "C0006", editTextLeisureAmount);
            insertBudgetCategoryIfChecked(db, checkBoxTranspo, "C0007", editTextTranspoAmount);
            insertBudgetCategoryIfChecked(db, checkBoxSavings, "C0008", editTextSavingsAmount);
            insertBudgetCategoryIfChecked(db, checkBoxOthers, "C0009", editTextOthersAmount);

            db.setTransactionSuccessful();
            Toast.makeText(this, "Categories updated successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error updating categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private String generateNewBCNum(SQLiteDatabase db) {
        String newBCNum = "BC0001";
        String query = "SELECT BCNum FROM BUDGET_CATEGORY ORDER BY BCNum DESC LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                String lastBCNum = cursor.getString(0);
                int numericPart = Integer.parseInt(lastBCNum.substring(2));
                numericPart++;
                newBCNum = "BC" + String.format("%04d", numericPart);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error generating BCNum: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return newBCNum;
    }

    private void insertBudgetCategoryIfChecked(SQLiteDatabase db, CheckBox checkBox, String categoryId, EditText editText) {
        if (checkBox.isChecked()) {
            String amountStr = editText.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Amount for category " + categoryId + " is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String newBCNum = generateNewBCNum(db);

            // Insert into SQLite
            ContentValues values = new ContentValues();
            values.put("BCNum", newBCNum);
            values.put("BCBudget", amount);
            values.put("BNum", PK_BNUM);
            values.put("CNum", categoryId);

            long result = db.insert("BUDGET_CATEGORY", null, values);
            if (result == -1) {
                Toast.makeText(this, "Failed to add category to SQLite", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert into Firebase
            Map<String, Object> firebaseData = new HashMap<>();
            firebaseData.put("BCBudget", amount);
            firebaseData.put("BNum", PK_BNUM);
            firebaseData.put("CNum", categoryId);

            FirebaseFirestore.getInstance().collection("BUDGET_CATEGORY")
                    .document(newBCNum)
                    .set(firebaseData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Category added to Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add category to Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void gohome(View v) {
        Intent i = new Intent(budgetingEditCategory.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(budgetingEditCategory.this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(budgetingEditCategory.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(budgetingEditCategory.this, account.class);
        startActivity(i);
    }

    public void back(View v) {
        finish();
    }
}
