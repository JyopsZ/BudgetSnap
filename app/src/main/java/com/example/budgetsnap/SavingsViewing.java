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

public class SavingsViewing extends AppCompatActivity {

    TextView nameValue, goalAmountValue, frequencyValue, dateValue;
    TextView savingsChallView, totalAmountView;
    Button activateButton;

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

    public void initializeViews() {

        nameValue = findViewById(R.id.nameValue);
        goalAmountValue = findViewById(R.id.goalAmountValue);
        frequencyValue = findViewById(R.id.frequencyValue);
        dateValue = findViewById(R.id.dateValue);
        savingsChallView = findViewById(R.id.savingsChallView);
        totalAmountView = findViewById(R.id.totalAmountView);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String goalAmount = i.getStringExtra("goalAmount");
        String frequency = i.getStringExtra("frequency");
        String date = i.getStringExtra("dateFinish");

        savingsChallView.setText(Html.fromHtml("<b>Savings Challenge: "+ name + "</b>"));
        nameValue.setText(name);
        //goalAmountValue.setText(goalAmount);
        frequencyValue.setText(frequency);
        dateValue.setText(date);
        //totalAmountView.setText(Html.fromHtml("<b>/</b>" + goalAmount));
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

            // Handle clicking Back button
            deletionConfirmView.findViewById(R.id.backButton).setOnClickListener(v2 -> {
                deletionConfirmDialog.dismiss();

                finish();
            });
        });
    }

    public void inputMoney(View v) {

        Intent i = new Intent(this, SavingsInputAmount.class);
        startActivity(i);
    }

    public void toggleActivation(View view) {
        activateButton = (Button) view;
        if (activateButton.getText().toString().equals("Deactivate")) {
            activateButton.setText("Activate");
            activateButton.setBackgroundColor(0xFF4CAF50); // Green color
        } else {
            activateButton.setText("Deactivate");
            activateButton.setBackgroundColor(0xFFFF2020); // Red color
        }
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