package com.example.budgetsnap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    TextView emailText, passwordText;
    EditText editEmail, editPassword;
    ImageButton eyeButton;

    private FirebaseFirestore db; // Results in error if declared in method for some reason >:(

    boolean isPasswordVisible = false;
    String UNum = null;

    ArrayList<UserClass> userClassList = new ArrayList<>(); // Sample data for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Savings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBManager dbManager = new DBManager(this); // Reset local db before updating with firebase
        dbManager.open();
        dbManager.deleteSQLInitial();
        dbManager.close();

        initializeViews();

        syncFirebaseToSQLite(); // Duplicate Firebase entries into SQLite DB
        syncBudgetCategories(); // Duplicate the rest of the tables, except user, categories, savings'
        syncBudgets();
        syncBudgetAdditions();
        syncTransactions();
        syncSavings();
    }

    private void initializeViews() {

        emailText = findViewById(R.id.emailText);
        String eText = "<font color=#000000>Email</font> <font color=#E16162>*</font>"; // Declare text with HTML tags for different font colors
        emailText.setText(Html.fromHtml(eText, Html.FROM_HTML_MODE_LEGACY)); // Set HTML text to TextView

        passwordText = findViewById(R.id.passwordText);
        String pText = "<font color=#000000>Password</font> <font color=#E16162>*</font>";
        passwordText.setText(Html.fromHtml(pText, Html.FROM_HTML_MODE_LEGACY));

        editEmail = findViewById(R.id.editEmail);

        editPassword = findViewById(R.id.editPassword);
        eyeButton = findViewById(R.id.eyeButton);

        // Reference for Html: https://alfredmyers.com/2018/02/06/warning-cs0618-html-fromhtmlstring-is-obsolete-deprecated/#google_vignette
    }

    private void refreshUser() {
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        userClassList.clear();

        Cursor cursor = dbManager.fetchUsers();

        if (cursor.moveToFirst()) {
            do {
                String UNum = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PK_UNUM));
                String UName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UNAME));
                String UPass = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UPASS));
                String UBDay = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UBDAY));
                String UEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UEMAIL));
                byte[] UImage = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.UIMAGE));
                double UIncome = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.UINCOME));
                double UExpense = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.UEXPENSE));

                userClassList.add(new UserClass(UNum, UName, UPass, UBDay, UEmail, UImage, UIncome, UExpense));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void syncFirebaseToSQLite() { Reference: https://firebase.google.com/docs/firestore/query-data/get-data

        db = FirebaseFirestore.getInstance();
        DBManager dbManager = new DBManager(this);

        dbManager.open();

        db.collection("USER").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                byte[] imageBytes = null;


                if (doc.contains("UImage") && doc.get("UImage") != null) {
                    String imageString = doc.getString("UImage");

                    try {
                        imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }


                UserClass user = new UserClass(
                        doc.getId(),
                        doc.getString("UName"),
                        doc.getString("UPass"),
                        doc.getString("UBday"),
                        doc.getString("UEmail"),
                        imageBytes,
                        doc.getDouble("UIncome"),
                        doc.getDouble("UExpense")
                );


                dbManager.insertUser(user);
            }

            refreshUser();
        });
    }

    private void syncBudgetCategories () {

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        db.collection("BUDGET_CATEGORY").get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (DocumentSnapshot doc : queryDocumentSnapshots) {

                dbManager.insertBudgetCategory(
                        doc.getId(),
                        doc.getDouble("BCBudget"),
                        doc.getString("BNum"),
                        doc.getString("CNum")
                );
            }

            dbManager.close();
        });
    }

    private void syncBudgets () {

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        db.collection("BUDGET").get().addOnSuccessListener(budgetSnapshots -> {

            for (DocumentSnapshot doc : budgetSnapshots) {

                dbManager.insertBudget(
                        doc.getId(),
                        doc.getString("UNum")
                );
            }

            dbManager.close();
        });
    }

    private void syncBudgetAdditions () {

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        db.collection("BUDGET_ADD").get().addOnSuccessListener(addSnapshots -> {

            for (DocumentSnapshot doc : addSnapshots) {

                dbManager.insertBudgetAdd(
                        doc.getId(),
                        doc.getString("BAName"),
                        doc.getDouble("BAExpense"),
                        doc.getString("BNum"),
                        doc.getString("CNum")
                );
            }

            dbManager.close();
        });
    }

    private void syncTransactions() {

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        db.collection("TRANSACTIONS").get().addOnSuccessListener(transSnapshots -> {

            for (DocumentSnapshot doc : transSnapshots) {

                dbManager.insertTransaction(

                        doc.getId(),
                        doc.getString("TName"),
                        doc.getString("TDate"),
                        doc.getString("TTime"),
                        doc.getDouble("TAmount"),
                        doc.getString("TImage"), // TODO: Remove comments for TImage Part 2 of 2
                        String.valueOf(doc.getBoolean("TStatus")),
                        doc.getString("CNum"),
                        doc.getString("UNum")
                );
            }

            dbManager.close();
        });
    }

    private void syncSavings() {

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        db.collection("SAVINGS").get().addOnSuccessListener(savingsSnapshots -> {

            for (DocumentSnapshot doc : savingsSnapshots) {

                SavingsClass savings = new SavingsClass(
                        doc.getId(),
                        doc.getString("SName"),
                        doc.getDouble("SCurrentAmount"),
                        doc.getDouble("SGoalAmount"),
                        doc.getString("SFrequency"),
                        doc.getString("SDate"),
                        doc.getBoolean("SStatus"),
                        doc.getString("UNum")
                );

                dbManager.insertSavings(savings);
            }

            dbManager.close();
        });
    }

    public void togglePassword(View v) { // Method for toggling password visibility onClick of eye symbol

        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {

            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeButton.setImageResource(R.drawable.view_pass);
        }

        else {

            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeButton.setImageResource(R.drawable.no_view_pass);
        }

        editPassword.setSelection(editPassword.getText().length());
    }

    public void login (View v) {

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        boolean isValid = false;
        for (UserClass userClass : userClassList) {

            if (userClass.getEmail().equals(email) && userClass.getPassword().equals(password)) {
               UNum = userClass.getUNum();
                isValid = true;
                break;
            }
        }

        if (isValid) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userUNum", UNum); // Store the UNum
            editor.apply();

            Intent i = new Intent(LoginActivity.this, Home.class);
            i.putExtra("PK_UNUM", UNum);
            startActivity(i);
        }

        else {

            Toast.makeText(LoginActivity.this, "Invalid Email or Password, please try again.", Toast.LENGTH_SHORT).show();

            Intent i = getIntent();
            finish();
            startActivity(i);
        }
    }

    public void register (View v) {

        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }
}