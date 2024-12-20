package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class transaction_moneyin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_moneyin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set adapter for the Spinner
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item); // values of this array is in res/values/strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        ImageView plusButton = findViewById(R.id.menu_plus);
        plusButton.setOnClickListener(this::BtnClickedPlus); // Image clickable on bottom menu
    }

    // button to money In screen, button below the transaction word
    public void BtnMoneyInBtn(View v) {
        Intent i = new Intent(transaction_moneyin.this, transaction_moneyin.class); // Correct the context
        startActivity(i);
    }

    // button to money out screen, button below the transaction word
    public void BtnMoneyOutBtn(View v) {
        Intent i = new Intent(transaction_moneyin.this, transactions_moneyout.class); // Correct the context
        startActivity(i);
    }

    // call prompt
    public void BtnClickedPlus(View view) {
        showTransactionDialog();
    }

    // Show the "Money In" or "Money Out" prompt
    private void showTransactionDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking on "Money In"
        dialogView.findViewById(R.id.moneyInOption).setOnClickListener(v -> {
            // No need for money in option (current screen)
            dialog.dismiss(); // Close the dialog
        });

        // Handle clicking on "Money Out"
        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            // Start Money Out Activity
            Intent i = new Intent(transaction_moneyin.this, transactions_moneyout.class); // Correct the context
            startActivity(i);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog on "Cancel"
        });
    }

    public void gohome(View v) {
        Intent i = new Intent(transaction_moneyin.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }

    public void gonotif(View v) {
        Intent i = new Intent(transaction_moneyin.this, Notifications.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(transaction_moneyin.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(transaction_moneyin.this, account.class);
        startActivity(i);
    }
}
