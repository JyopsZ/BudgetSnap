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
import android.util.Base64;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class transactions_moneyout extends AppCompatActivity {

    private static final String TAG = "transactions_moneyout"; // Tag for logging
    private Uri selectedImageUriMoneyOut; // Store the selected image URI
    private EditText editTextName, editTextAmount, editTextDate, editTextTime;
    private Spinner spinnerCategory;
    private Button buttonAttachImage, buttonAddExpense;
    private SQLiteDatabase db;
    private LinkedHashMap<String, String> categoryMap;
    private String currentUserUNum; // CNUM -> CNAME mapping
    private String PK_Unum;
    private byte[] byteArray;
    private String base64ImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_moneyout);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        currentUserUNum = getCurrentUserUNum();

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonAttachImage = findViewById(R.id.buttonAttachImage);
        buttonAddExpense = findViewById(R.id.buttonAddExpenses);

        // Fetch categories from the database
        fetchCategoriesFromDB();

        // Set up Spinner for categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(categoryMap.values()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Handle Attach Image button
        buttonAttachImage.setOnClickListener(v -> openImagePicker());

        // Handle Add Expense button
        buttonAddExpense.setOnClickListener(v -> addExpenseToDatabase());

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + PK_Unum, Toast.LENGTH_SHORT).show();
    }

    private String getCurrentUserUNum() {
        // Try fetching directly from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userUNum = sharedPreferences.getString("userUNum", null);

        if (userUNum == null) {
            Log.e(TAG, "User UNum not found in SharedPreferences. Check login process.");
            Toast.makeText(this, "User session error. Please log in again.", Toast.LENGTH_SHORT).show();
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
            Log.e(TAG, "Error fetching categories from DB: " + e.getMessage());
        }
    }

    private void addExpenseToDatabase() {
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

            // Set TStatus as boolean (false for Money Out)
            boolean tStatus = false;

            // Prepare SQLite ContentValues
            ContentValues values = new ContentValues();
            values.put("TNum", newTransactionID);
            values.put("TName", name);
            values.put("TAmount", Double.parseDouble(amount));
            values.put("TDate", date);
            values.put("TTime", time);
            values.put("CNum", categoryCNUM);

            // If there's an image, convert it to a byte array and store it as a blob in SQLite
            if (base64ImageString != null) {
                byte[] imageBlob = Base64.decode(base64ImageString, Base64.DEFAULT);
                values.put("TImage", imageBlob); // Store image as a blob in SQLite
            }

            values.put("TStatus", tStatus ? 1 : 0); // SQLite stores TStatus as 1 (true) or 0 (false)
            values.put("UNum", currentUserUNum);

            // Insert into SQLite
            long result = db.insert("TRANSACTIONS", null, values);

            if (result != -1) {
                Toast.makeText(this, "Expense added successfully to SQLite", Toast.LENGTH_SHORT).show();

                // Insert into Firebase Firestore
                insertExpenseIntoFirebase(newTransactionID, name, Double.parseDouble(amount), date, time, categoryCNUM, tStatus, base64ImageString);

                updateBalanceForTransaction();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to add transaction to SQLite", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error adding transaction: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertExpenseIntoFirebase(String transactionID, String name, double amount, String date, String time, String categoryCNUM, boolean status, String base64Image) {
        FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();

        // Create a map of the transaction data
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("TName", name);
        transactionData.put("TAmount", amount);
        transactionData.put("TDate", date);
        transactionData.put("TTime", time);
        transactionData.put("CNum", categoryCNUM);
        transactionData.put("TStatus", status); // Store TStatus as boolean (false for Money Out)
        transactionData.put("UNum", currentUserUNum);

        // Add Base64 image to Firestore if available
        if (base64Image != null) {
            transactionData.put("TImageBase64", base64Image); // Store Base64 image string in Firestore
        }

        // Insert into Firestore
        dbFirebase.collection("TRANSACTIONS")
                .document(transactionID)
                .set(transactionData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Expense added to Firebase"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error adding expense to Firebase", e));
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

                        if (status == 0) {
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
                double newBalance = currentBalance - amount;

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
            String query = "SELECT TNum FROM TRANSACTIONS ORDER BY TNum DESC LIMIT 1";
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    String lastID = cursor.getString(0);
                    int numericPart = Integer.parseInt(lastID.substring(1)); // Remove the "T" prefix
                    numericPart++;
                    newID = "T" + String.format("%04d", numericPart); // Format as T000X
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating new transaction ID: " + e.getMessage());
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

    // Open image picker
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

                    // Convert bitmap to Base64 string
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    base64ImageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    Toast.makeText(this, "Image captured and converted to Base64", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void clearFields() {
        editTextName.setText("");
        editTextAmount.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        spinnerCategory.setSelection(0);
        selectedImageUriMoneyOut = null;
    }

    private void showTransactionDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.moneyInOption).setOnClickListener(v -> {
            Intent i = new Intent(transactions_moneyout.this, transaction_moneyin.class);
            startActivity(i);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            Intent i = new Intent(transactions_moneyout.this, transactions_moneyout.class);
            startActivity(i);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
    }

    // Show transaction dialog when "plus" button is clicked
    public void BtnClickedPlus2(View view) {
        showTransactionDialog();
    }

    public void BtnMoneyInBtn2(View v) {
        Intent i = new Intent(transactions_moneyout.this, transaction_moneyin.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void BtnMoneyOutBtn2(View v) {
        Intent i = new Intent(transactions_moneyout.this, transactions_moneyout.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gohome(View v) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("PK_UNUM", currentUserUNum);
        startActivity(intent);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(transactions_moneyout.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(transactions_moneyout.this, account.class);
        startActivity(i);
    }
}
