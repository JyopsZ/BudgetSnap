package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class SavingsViewing extends AppCompatActivity {

    TextView nameValue, goalAmountValue, frequencyValue, dateValue;
    TextView savingsChallView, totalAmountView, phpView;
    Button activateButton;

    String snum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings_viewing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }

    protected void onResume() {

        super.onResume();

        DBManager dbManager = new DBManager(this);
        dbManager.open();

        SavingsClass savings = dbManager.getSavingsForEdit(snum);
        savingsChallView.setText(Html.fromHtml("<b>Savings Challenge: "+ savings.getName() + "</b>"));
        nameValue.setText(savings.getName());
        phpView.setText(String.format("PHP %.2f ", savings.getCurrentAmount()));
        totalAmountView.setText(String.format("/ %.2f", savings.getGoalAmount()));
        goalAmountValue.setText(String.format("PHP %.2f", savings.getGoalAmount()));
        frequencyValue.setText(savings.getFrequency());
        dateValue.setText(savings.getDateFinish());

        dbManager.close();
    }

    public void initializeViews() {

        nameValue = findViewById(R.id.nameValue);
        goalAmountValue = findViewById(R.id.goalAmountValue);
        frequencyValue = findViewById(R.id.frequencyValue);
        dateValue = findViewById(R.id.dateValue);
        savingsChallView = findViewById(R.id.savingsChallView);
        totalAmountView = findViewById(R.id.totalAmountView);
        phpView = findViewById(R.id.phpView);
        activateButton = findViewById(R.id.activateButton);

        Intent i = getIntent();
        snum = i.getStringExtra("snum");
        String name = i.getStringExtra("name");
        double currentAmount = i.getDoubleExtra("currentAmount", 0.0);
        double goalAmount = i.getDoubleExtra("goalAmount", 0.0);
        String frequency = i.getStringExtra("frequency");
        String date = i.getStringExtra("dateFinish");

        boolean isActivated = i.getBooleanExtra("isActivated", true);

        savingsChallView.setText(Html.fromHtml("<b>Savings Challenge: "+ name + "</b>"));
        nameValue.setText(name);
        phpView.setText(String.format("PHP %.2f ", currentAmount));
        totalAmountView.setText(String.format("/ %.2f", goalAmount)); // Progress at the bottom
        goalAmountValue.setText(String.format("PHP %.2f", goalAmount));
        frequencyValue.setText(frequency);
        dateValue.setText(date);

        // Set Initial Button Text and Color based on the current status (isActivated)
        if (isActivated) {
            activateButton.setText("Deactivate");
            activateButton.setBackgroundColor(0xFFFF2020); // Red color
        } else {
            activateButton.setText("Activate");
            activateButton.setBackgroundColor(0xFF4CAF50); // Green color
        }
    }

    public void back(View view) {

        finish();
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void edit(View view) {

        Intent i = new Intent(this, SavingsEditing.class);
        i.putExtra("snum", snum);
        startActivity(i);
    }

    public void delete(View v) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_savings_sure, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking Cancel button
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v1 -> {
            dialog.dismiss();
        });

        // Handle clicking Confirm button
        dialogView.findViewById(R.id.confirmButton).setOnClickListener(v1 -> {
            dialog.dismiss();

            // Show the deletion confirmation dialog
            View deletionConfirmView = inflater.inflate(R.layout.dialog_savings_delete, null);
            AlertDialog.Builder deletionConfirmBuilder = new AlertDialog.Builder(this);
            deletionConfirmBuilder.setView(deletionConfirmView)
                    .setCancelable(false);
            AlertDialog deletionConfirmDialog = deletionConfirmBuilder.create();
            deletionConfirmDialog.show();

            // Delete the savings record
            DBManager dbManager = new DBManager(this);
            dbManager.open();
            dbManager.deleteSavingsGoal(snum);
            dbManager.close();

            deleteFirebaseSavings(snum);

            // Handle clicking Back button
            deletionConfirmView.findViewById(R.id.backButton).setOnClickListener(v2 -> {
                deletionConfirmDialog.dismiss();

                finish();
            });
        });
    }

    private void deleteFirebaseSavings(String snum) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SAVINGS").document(snum).delete();
    }

    public void inputMoney(View v) {

        Intent i = new Intent(this, SavingsInputAmount.class);
        i.putExtra("snum", snum);
        startActivity(i);
    }

    public void toggleActivation(View view) {
        activateButton = (Button) view;
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        if (activateButton.getText().toString().equals("Deactivate")) {
            activateButton.setText("Activate");
            activateButton.setBackgroundColor(0xFF4CAF50); // Green color
            dbManager.updateSavingsStatus(snum, false);
            updateFirebaseStatus(snum, false);
        } else {
            activateButton.setText("Deactivate");
            activateButton.setBackgroundColor(0xFFFF2020); // Red color
            dbManager.updateSavingsStatus(snum, true);
            updateFirebaseStatus(snum, true);
        }
    }

    private void updateFirebaseStatus(String snum, boolean status) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SAVINGS").document(snum).update("SStatus", status);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }


    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        startActivity(i);
    }

    public void gosavings(View v) {
        Intent i = new Intent(this, SavingsActivity.class);
        startActivity(i);
    }
}