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

import com.google.firebase.firestore.FirebaseFirestore;

public class SavingsEditing extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView nameText, goalText, frequencyText, dateText;
    EditText editName, editGoal, editDate;
    Spinner editFrequency;

    String frequency; // Store frequency chosen by user
    String snum;

    String PK_UNum;

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

        PK_UNum = getIntent().getStringExtra("PK_UNUM");

        initializeViews(); // Print out labels for input fields with red asterisks
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle Spinner selection
        switch (position) {
            case 0:
                frequency = "Daily";
                break;
            case 1:
                frequency = "Weekly";
                break;
            case 2:
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
        initializeSpinner();

        snum = getIntent().getStringExtra("snum");

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        SavingsClass savings = dbManager.getSavingsForEdit(snum); // Returns an object of type Savings with data to initially populate text fields
        dbManager.close();

        editName.setText(savings.getName());
        editGoal.setText(String.valueOf(savings.getGoalAmount()));
        editDate.setText(savings.getDateFinish());

        int freq = 0;
        switch (savings.getFrequency()) { // Get the integer equivalent of the selected frequency
            case "Daily":
                freq = 0;
                break;
            case "Weekly":
                freq = 1;
                break;
            case "Monthly":
                freq = 2;
                break;
        }
        editFrequency.setSelection(freq); // Set the spinner to the frequency initially selected.

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

            String name = editName.getText().toString();
            double goalAmount = Double.parseDouble(editGoal.getText().toString());
            String dateFinish = editDate.getText().toString();

            DBManager dbManager = new DBManager(this);
            dbManager.open();
            dbManager.editSavings(snum, name, goalAmount, frequency, dateFinish);
            dbManager.close();

            editSavingsFB(snum, name, goalAmount, frequency, dateFinish);

            finish();
            dialog.dismiss();
        });
    }

    private void editSavingsFB(String snum, String name, double goalAmount, String frequency, String dateFinish) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SAVINGS").document(snum).update("SName", name, "SGoalAmount", goalAmount, "SFrequency", frequency, "SDate", dateFinish);
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }


    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }

    public void gosavings(View v) {
        Intent i = new Intent(this, SavingsActivity.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }
}