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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class transaction_moneyin extends AppCompatActivity {

    private static final String TAG = "transaction_moneyin"; // Tag for logging
    private Uri selectedImageUriMoneyIn; // Store the selected image URI
    private EditText editTextName, editTextAmount, editTextDate, editTextTime;
    private Spinner spinnerCategory;
    private Button buttonAttachImage, buttonAddIncome;
    private SQLiteDatabase db; // SQLite database instance
    private LinkedHashMap<String, String> categoryMap; // Stores CNUM -> CNAME mapping
    private String currentUserUNum; // Current user's unique number
    private String PK_Unum; // Primary key user number passed through intent
    private String base64ImageString; // String to hold the Base64-encoded image

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

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
        Toast.makeText(this, "Current User: " + PK_Unum, Toast.LENGTH_SHORT).show();
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

            // Set TStatus as boolean (true for Money In)
            boolean tStatus = true;

            // Convert the image to a byte array (if image exists)
            byte[] imageBytes = null;
            if (base64ImageString != null) {
                // Convert Base64 to byte array for SQLite storage as BLOB
                imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
            }

            // Prepare SQLite ContentValues
            ContentValues values = new ContentValues();
            values.put("TNum", newTransactionID);
            values.put("TName", name);
            values.put("TAmount", Double.parseDouble(amount));
            values.put("TDate", date);
            values.put("TTime", time);
            values.put("CNum", categoryCNUM);
            if (imageBytes != null) {
                values.put("TImage", imageBytes); // Store image as BLOB in SQLite
            }
            values.put("TStatus", tStatus ? 1 : 0); // SQLite stores TStatus as an integer (1 for true, 0 for false)
            values.put("UNum", currentUserUNum);

            // Insert into SQLite
            long result = db.insert("TRANSACTIONS", null, values);

            if (result != -1) {
                Toast.makeText(this, "Transaction added successfully to SQLite", Toast.LENGTH_SHORT).show();

                // Insert into Firebase Firestore
                insertIntoFirebase(newTransactionID, name, amount, date, time, categoryCNUM, tStatus, base64ImageString);

                clearFields();
            } else {
                Toast.makeText(this, "Failed to add transaction to SQLite", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SQLiteError", "Error adding transaction: " + e.getMessage());
        }
    }

    private void insertIntoFirebase(String transactionID, String name, String amount, String date, String time, String categoryCNUM, boolean status, String base64Image) {
        FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();

        // Create a map of the transaction data
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("TName", name);
        transactionData.put("TAmount", Double.parseDouble(amount));
        transactionData.put("TDate", date);
        transactionData.put("TTime", time);
        transactionData.put("CNum", categoryCNUM);
        transactionData.put("TStatus", status); // Store as boolean (true for Money In, false for Money Out)
        transactionData.put("UNum", currentUserUNum);
        if (base64Image != null) {
            transactionData.put("TImageBase64", base64Image); // Add Base64 image string to Firestore
        }

        // Insert into Firestore
        dbFirebase.collection("TRANSACTIONS")
                .document(transactionID)
                .set(transactionData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Transaction added to Firebase"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error adding transaction to Firebase", e));
    }

    private String generateNewTransactionID() {
        String newID = "T0001"; // Default ID for the first transaction
        try {
            String query = "SELECT TNum FROM TRANSACTIONS ORDER BY TNum DESC LIMIT 1";
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    String lastID = cursor.getString(0);
                    int numericPart = Integer.parseInt(lastID.substring(1));
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
        selectedImageUriMoneyIn = null;
        base64ImageString = null;
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

    public void BtnClickedPlus(View view) {
        showTransactionDialog();
    }

    public void gohome(View v) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("PK_UNUM", currentUserUNum);
        startActivity(intent);
    }

    public void BtnMoneyOutBtn1(View v) {
        Intent i = new Intent(transaction_moneyin.this, transactions_moneyout.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void ButtonMoneyIn1(View v) {
        Intent i = new Intent(transaction_moneyin.this, transaction_moneyin.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gocategories(View v) {
        startActivity(new Intent(this, categories_main.class));
    }

    public void goaccount(View v) {
        startActivity(new Intent(this, account.class));
        base64ImageString = null;
    }
}

