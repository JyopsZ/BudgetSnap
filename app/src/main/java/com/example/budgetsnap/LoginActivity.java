package com.example.budgetsnap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
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

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    TextView emailText, passwordText;
    EditText editEmail, editPassword;
    ImageButton eyeButton;

    boolean isPasswordVisible = false;

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

        initializeViews();
        refreshUser(); // Update arrayList with new entries to the db
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

    private void refreshUser() { // Repurposed method from MCO2. Used to add new users to arrayList from SignupActivity intent. (addUser)
                                // Updated to add new users to arrayList by refreshing database.

        DBManager dbManager = new DBManager(this);
        dbManager.open();

        userClassList.clear();

        Cursor cursor = dbManager.fetchUsers(); // Get all users currently in db

        if (cursor.moveToFirst()) {
            do {
                userClassList.add(new UserClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PK_UNUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UPASS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UBDAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UEMAIL)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.UINCOME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.UEXPENSE)))
                );
            } while (cursor.moveToNext());
        }

        // FOR DEBUGGING
        //Toast.makeText(this, "First User UNum: " + userClassList.get(0).getUNum(), Toast.LENGTH_LONG).show();

        cursor.close();
        dbManager.close();
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
        for (UserClass userClass : userClassList) { // Check every user in the arrayList

            if (userClass.getEmail().equals(email) && userClass.getPassword().equals(password)) { // If email and password provided is given, valid

                isValid = true;
                break;
            }
        }

        if (isValid) {

            Intent i = new Intent(LoginActivity.this, Home.class); // REPLACE WITH ACTUAL POST LOGIN ACTIVITY * PLACEHOLDER ACTIVITY ONLY
            startActivity(i); // Start new activity after logging in
        }

        else {

            Toast.makeText(LoginActivity.this, "Invalid Email or Password, please try again.", Toast.LENGTH_SHORT).show();

            Intent i = getIntent();
            finish();
            startActivity(i); // Reload activity if incorrect
        }
    }

    public void register (View v) {

        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }
}