package com.example.budgetsnap;

import android.content.Intent;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    TextView nameText, birthdayText, emailText, passwordText, rePasswordText;

    ImageButton eyeButton, eyeButton2;

    EditText editName, editBirthday, editEmail;
    EditText editPassword, editRePassword;

    boolean isPasswordVisible = false;
    boolean isRePasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Savings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews(); // Call to initialize views
    }

    private void initializeViews() {

        nameText = findViewById(R.id.nameText);
        String nText = "<font color=#000000>Name</font> <font color=#E16162>*</font>";
        nameText.setText(Html.fromHtml(nText, Html.FROM_HTML_MODE_LEGACY));

        birthdayText = findViewById(R.id.birthdayText);
        String bText = "<font color=#000000>Birthday</font> <font color=#E16162>*</font>";
        birthdayText.setText(Html.fromHtml(bText, Html.FROM_HTML_MODE_LEGACY));

        emailText = findViewById(R.id.emailText);
        String eText = "<font color=#000000>Email</font> <font color=#E16162>*</font>";
        emailText.setText(Html.fromHtml(eText, Html.FROM_HTML_MODE_LEGACY));

        passwordText = findViewById(R.id.passwordText);
        String pText = "<font color=#000000>Password</font> <font color=#E16162>*</font>";
        passwordText.setText(Html.fromHtml(pText, Html.FROM_HTML_MODE_LEGACY));

        rePasswordText = findViewById(R.id.rePasswordText);
        String rpText = "<font color=#000000>Re-enter Password</font> <font color=#E16162>*</font>";
        rePasswordText.setText(Html.fromHtml(rpText, Html.FROM_HTML_MODE_LEGACY));

        editName = findViewById(R.id.editName);
        editBirthday = findViewById(R.id.editBirthday);
        editEmail = findViewById(R.id.editEmail);

        editPassword = findViewById(R.id.editPassword);
        editRePassword = findViewById(R.id.editRePassword);
        eyeButton = findViewById(R.id.eyeButton);
        eyeButton2 = findViewById(R.id.eyeButton2);

        // Reference for Html: https://alfredmyers.com/2018/02/06/warning-cs0618-html-fromhtmlstring-is-obsolete-deprecated/#google_vignette
    }


    public void togglePassword(View v) {

        isPasswordVisible = !isPasswordVisible;
        togglePasswordVisibility(editPassword, eyeButton, isPasswordVisible);
    }

    public void toggleRePassword(View v) {

        isRePasswordVisible = !isRePasswordVisible;
        togglePasswordVisibility(editRePassword, eyeButton2, isRePasswordVisible);
    }

    // Reference: https://tiloid.com/p/show-hide-password-in-edittext-in-android
    public void togglePasswordVisibility(EditText passwordText, ImageButton toggleButton, boolean isVisible) {

        if (isVisible) {

            passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleButton.setImageResource(R.drawable.view_pass);
        }

        else {

            passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.no_view_pass);
        }

        passwordText.setSelection(passwordText.getText().length());
    }

    public void sign(View v) {
        String name = editName.getText().toString();
        String birthday = editBirthday.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String rePassword = editRePassword.getText().toString();

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        DBManager dbManager = new DBManager(this);
        dbManager.open();

        String maxUNum = dbManager.getUserMax();
        int currentNum = Integer.parseInt(maxUNum.substring(1));
        String nextUNum = String.format("U%04d", currentNum + 1);

        UserClass newUser = new UserClass(
                nextUNum,
                name,
                password,
                birthday,
                email,
                "",
                0.0,
                0.0
        );

        dbManager.insertUser(newUser);
        dbManager.close();

        FirebaseFirestore dbF = FirebaseFirestore.getInstance();
        CollectionReference usersRef = dbF.collection("USER");

        // Query all documents and sort  by document ID
        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Find the document with the "highest" ID
                        String maxDocId = "";
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String docId = doc.getId();
                            if (docId.compareTo(maxDocId) > 0) {
                                maxDocId = docId;
                            }
                        }

                        // Use the nextUNum for the new user
                        DocumentReference docRef = usersRef.document(nextUNum);

                        Map<String, Object> user = new HashMap<>();
                        user.put("UBDAY", birthday);
                        user.put("UEmail", email);
                        user.put("UExpense", 0.0);
                        user.put("UImage", "");
                        user.put("UIncome", 0.0);
                        user.put("UName", name);
                        user.put("UPass", password);

                        // Save user to Firestore
                        docRef.set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Intent i = new Intent(SignupActivity.this, CongratsSignup.class);
                                    startActivity(i);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Error saving data to Firebase", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(SignupActivity.this, "No user found to generate UNum", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show();
                });
    }



    public void log (View v) {

        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
    }
}