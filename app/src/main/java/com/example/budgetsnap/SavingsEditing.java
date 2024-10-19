package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavingsEditing extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView nameText, goalText, frequencyText, dateText;
    EditText editName, editGoal, editDate;
    Spinner editFrequency;

    String frequency; // Store frequency chosen by user

    private static final String[] freq = {"Daily", "Weekly", "Monthly"}; // Reference: Adrian Tan Villador for Spinner (dropdown code)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings_challenge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews(); // Print out labels for input fields with red asterisks
        initializeSpinner();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle Spinner selection
        switch (position) {
            case 1:
                frequency = "Daily";
                break;
            case 2:
                frequency = "Weekly";
                break;
            case 3:
                frequency = "Monthly";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        frequency = "Daily";
    }

    private void initializeViews() {

        nameText = findViewById(R.id.nameText);
        String nText = "<font color=#000000>Name</font> <font color=#E16162>*</font>";
        nameText.setText(Html.fromHtml(nText, Html.FROM_HTML_MODE_LEGACY));

        goalText = findViewById(R.id.goalText);
        String gText = "<font color=#000000>Goal Amount</font> <font color=#E16162>*</font>";
        goalText.setText(Html.fromHtml(gText, Html.FROM_HTML_MODE_LEGACY));

        frequencyText = findViewById(R.id.frequencyText);
        String fText = "<font color=#000000>Frequency</font> <font color=#E16162>*</font>";
        frequencyText.setText(Html.fromHtml(fText, Html.FROM_HTML_MODE_LEGACY));

        dateText = findViewById(R.id.dateText);
        String pText = "<font color=#000000>Date to finish</font> <font color=#E16162>*</font>";
        dateText.setText(Html.fromHtml(pText, Html.FROM_HTML_MODE_LEGACY));

        editName = findViewById(R.id.editName);
        editGoal = findViewById(R.id.editGoal);
        editDate = findViewById(R.id.editDate);

        editFrequency = findViewById(R.id.editFrequency);

        // Reference for Html: https://alfredmyers.com/2018/02/06/warning-cs0618-html-fromhtmlstring-is-obsolete-deprecated/#google_vignette

    }

    public void initializeSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freq);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editFrequency.setAdapter(adapter);
        editFrequency.setOnItemSelectedListener(this);
    }

    public void back (View v) {

        finish();
    }

    public void save (View v) {

        showSavingDialog();
    }

    // Show the savings dialog
    private void showSavingDialog() {
        // Inflate the custom dialog layouts
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_savings_edited, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking
        dialogView.findViewById(R.id.backButton).setOnClickListener(v -> {

            Intent i = new Intent(this, SavingsActivity.class);
            i.putExtra("name", editName.getText().toString());
            i.putExtra("goalAmount", editGoal.getText().toString());
            i.putExtra("frequency", frequency);
            i.putExtra("dateFinish", editDate.getText().toString());
            startActivity(i);

            dialog.dismiss();
        });
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }


    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        startActivity(i);
    }

    public void gosavings(View v) {
        Intent i = new Intent(this, SavingsActivity.class);
        startActivity(i);
    }
}