package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

public class transaction_moneyin extends AppCompatActivity {

    private static final String TAG = "transaction_moneyin"; // Tag for logging
    private Uri selectedImageUriMoneyIn; // Store the selected image URI
    private EditText editTextName, editTextAmount, editTextDate, editTextTime;
    private Spinner spinnerCategory;
    private Button buttonAttachImage, buttonAddIncome;
    private SQLiteDatabase db;
    private LinkedHashMap<String, String> categoryMap; // Stores CNUM -> CNAME mapping
    private String currentUserUNum;
    private double balance;// Store the current user's UNum

    private byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_moneyin);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Fetch the current user's UNum (from shared preferences)
        currentUserUNum = getCurrentUserUNum();

        if (currentUserUNum == null) {
            Toast.makeText(this, "Error: Unable to fetch user session. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)); // Redirect to login
            finish();
            return;
        }

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

    private String getCurrentUserUNum() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userUNum = sharedPreferences.getString("userUNum", null);

        if (userUNum == null) {
            Log.e(TAG, "User UNum not found in SharedPreferences. Check login process.");
        } else {
            Log.d(TAG, "Fetched UNum from SharedPreferences: " + userUNum);
        }
        return userUNum;
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
            values.put("TImage", byteArray);
            values.put("TStatus", 1); // 1 for Money In
            values.put("UNum", currentUserUNum); // Save current user's UNum

            // Insert into the database
            long result = db.insert("TRANSACTIONS", null, values);

            if (result != -1) {
                Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();


                updateBalanceForTransaction();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error adding transaction: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBalanceForTransaction() {
        try {
            String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
            Log.d("BalanceUpdate", "Current date formatted: " + currentDate);

            String queryDateCheck = "SELECT TDate FROM TRANSACTIONS WHERE UNum = ? ORDER BY TNum DESC LIMIT 1";
            String[] dateCheckArgs = {currentUserUNum};

            Cursor dateCursor = db.rawQuery(queryDateCheck, dateCheckArgs);
            if (dateCursor != null && dateCursor.moveToFirst()) {
                String lastTransactionDate = dateCursor.getString(dateCursor.getColumnIndex("TDate"));
                Log.d("BalanceUpdate", "Last transaction date fetched: " + lastTransactionDate);


                if (currentDate.equals(lastTransactionDate)) {
                    String queryTransaction = "SELECT TAmount, TStatus FROM TRANSACTIONS WHERE UNum = ? AND TDate = ? ORDER BY TNum DESC LIMIT 1";
                    String[] transactionArgs = {currentUserUNum, currentDate};

                    Log.d("BalanceUpdate", "Executing query: " + queryTransaction + " with arguments: " + Arrays.toString(transactionArgs));
                    Cursor transactionCursor = db.rawQuery(queryTransaction, transactionArgs);

                    if (transactionCursor != null && transactionCursor.moveToFirst()) {
                        double amount = transactionCursor.getDouble(transactionCursor.getColumnIndex("TAmount"));
                        int status = transactionCursor.getInt(transactionCursor.getColumnIndex("TStatus"));

                        Log.d("BalanceUpdate", "Transaction found: TAmount = " + amount + ", TStatus = " + status);

                        if (status == 1) {
                            updateUserBalance(amount);
                        }
                        transactionCursor.close();
                    }
                } else {
                    Log.d("BalanceUpdate", "No transactions for the current date: " + currentDate);
                }
                dateCursor.close();
            } else {
                Log.d("BalanceUpdate", "No transactions found for the user.");
            }
        } catch (Exception e) {
            Log.e("BalanceError", "Error updating balance: " + e.getMessage());
        }
    }


    private void updateUserBalance(double amount) {
        try {
            String query = "SELECT UIncome FROM USER WHERE UNum = ?";
            String[] queryArgs = {currentUserUNum};

            Cursor cursor = db.rawQuery(query, queryArgs);

            if (cursor != null && cursor.moveToFirst()) {
                double currentBalance = cursor.getDouble(cursor.getColumnIndex("UIncome"));
                double newBalance = currentBalance + amount;

                ContentValues values = new ContentValues();
                values.put("UIncome", newBalance);

                int rowsUpdated = db.update("USER", values, "UNum = ?", new String[]{currentUserUNum});
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("BalanceUpdateError", "Error updating user balance: " + e.getMessage());
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

        /*
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Filter for image files only
        imagePickerLauncher.launch(intent); // Launch the image picker
         */

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    /*
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
     */

    // Method is generated with AI. Claude 3.5 Sonnet. Prompt = "take an image, then save the captured image as a blob data type to be stored in the sqlite database"
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");

                    // Convert bitmap to byte array for BLOB storage
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();

                    // The byte array will be stored as BLOB in SQLite
                    selectedImageUriMoneyIn = Uri.parse("blob://" + System.currentTimeMillis());
                    Toast.makeText(this, "Image captured and ready for storage", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void clearFields() {
        editTextName.setText("");
        editTextAmount.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        spinnerCategory.setSelection(0);
        selectedImageUriMoneyIn = null;
    }

    public void BtnClickedPlus(View view) {
        showTransactionDialog();
    }

    private void showTransactionDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle dialog buttons
        dialogView.findViewById(R.id.moneyInOption).setOnClickListener(v -> {
            startActivity(new Intent(this, transaction_moneyin.class));
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            startActivity(new Intent(this, transactions_moneyout.class));
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
    }

    public void gohome(View v) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("PK_UNUM", currentUserUNum);
        startActivity(intent);
    }


    public void gotransactions(View v) {
        startActivity(new Intent(this, Transaction1.class));
    }

    public void gonotif(View v) {
        startActivity(new Intent(this, Notifications.class));
    }

    public void gocategories(View v) {
        startActivity(new Intent(this, categories_main.class));
    }

    public void goaccount(View v) {
        startActivity(new Intent(this, account.class));
    }
}
