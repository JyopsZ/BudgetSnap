package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class transaction_moneyin extends AppCompatActivity {

    private Uri selectedImageUriMoneyIn; // Store the selected image URI
    private EditText editTextName, editTextAmount, editTextDate, editTextTime;
    private Spinner spinnerCategory;
    private Button buttonAttachImage, buttonAddIncome;
    private SQLiteDatabase db;
    private LinkedHashMap<String, String> categoryMap; // Stores CNUM -> CNAME mapping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_moneyin);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonAttachImage = findViewById(R.id.buttonAttachImage);
        buttonAddIncome = findViewById(R.id.buttonAddIncome);

        // Fetch categories from the database
        fetchCategoriesFromDB();

        // Set up Spinner for categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(categoryMap.values()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Handle Attach Image button
        buttonAttachImage.setOnClickListener(v -> openImagePicker());

        // Handle Add Income button
        buttonAddIncome.setOnClickListener(v -> addIncomeToDatabase());
    }

    // Method triggered when "plus" button is pressed
    public void BtnClickedPlus(View view) {
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
            Intent i = new Intent(transaction_moneyin.this, transaction_moneyin.class);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            // Start Money Out Activity
            Intent i = new Intent(transaction_moneyin.this, transactions_moneyout.class);
            startActivity(i);
            dialog.dismiss(); // Close the dialog after starting the activity
        });

        // Handle clicking on the custom "Cancel" button inside your layout
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog on "Cancel"
        });
    }

    private void fetchCategoriesFromDB() {
        categoryMap = new LinkedHashMap<>();
        try (Cursor cursor = db.rawQuery("SELECT CNUM, CNAME FROM CATEGORIES", null)) {
            while (cursor.moveToNext()) {
                String cnum = cursor.getString(0);
                String cname = cursor.getString(1);
                categoryMap.put(cnum, cname);
            }
        } catch (Exception e) {
            Log.e("CategoryFetchError", "Error fetching categories from DB: " + e.getMessage());
        }
    }

    private void addIncomeToDatabase() {
        try {
            String name = editTextName.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();
            String categoryName = spinnerCategory.getSelectedItem().toString();

            // Validate inputs
            if (name.isEmpty() || amount.isEmpty() || date.isEmpty() || time.isEmpty() || categoryName.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Map category name to CNUM
            String categoryCNUM = getCategoryCNUM(categoryName);
            if (categoryCNUM == null) {
                Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a new transaction ID
            String newTransactionID = generateNewTransactionID();

            // Prepare content values
            ContentValues values = new ContentValues();
            values.put("TNum", newTransactionID); // Use the generated transaction ID
            values.put("TName", name);
            values.put("TAmount", Double.parseDouble(amount));
            values.put("TDate", date);
            values.put("TTime", time);
            values.put("CNum", categoryCNUM); // Save CNUM instead of category name
            values.put("TImage", selectedImageUriMoneyIn != null ? selectedImageUriMoneyIn.toString() : null);
            values.put("TStatus", 1); // 1 for Money In

            // Insert into the database
            long result = db.insert("TRANSACTIONS", null, values);

            if (result != -1) {
                Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error adding transaction: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateNewTransactionID() {
        String newID = "T0001"; // Default ID for the first transaction
        try {
            // Query the database for the latest transaction ID
            String query = "SELECT TNum FROM TRANSACTIONS ORDER BY TNum DESC LIMIT 1";
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    // Get the last ID
                    String lastID = cursor.getString(0);

                    // Extract the numeric part and increment
                    int numericPart = Integer.parseInt(lastID.substring(1)); // Remove the "T" prefix
                    numericPart++;
                    newID = "T" + String.format("%04d", numericPart); // Format as T000X
                }
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error generating new transaction ID: " + e.getMessage());
        }
        return newID;
    }

    private String getCategoryCNUM(String categoryName) {
        for (String cnum : categoryMap.keySet()) {
            if (categoryMap.get(cnum).equals(categoryName)) {
                return cnum;
            }
        }
        return null;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Filter for image files only
        imagePickerLauncher.launch(intent); // Launch the image picker
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUriMoneyIn = result.getData().getData(); // Get the image URI
                    Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    private void clearFields() {
        editTextName.setText("");
        editTextAmount.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        spinnerCategory.setSelection(0);
        selectedImageUriMoneyIn = null;
    }

    public void BtnMoneyOutBtn(View v){
        Intent i = new Intent(transaction_moneyin.this, transactions_moneyout.class);
        startActivity(i);
    }

    public void gohome(View v) {
        Intent i = new Intent(transaction_moneyin.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }

    public void gonotif(View v) {
        Intent i = new Intent(transaction_moneyin.this, Notifications.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(transaction_moneyin.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(transaction_moneyin.this, account.class);
        startActivity(i);
    }

}
