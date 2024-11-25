package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavingsInputAmount extends AppCompatActivity {

    EditText editInputText;

    String snum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings_input_amount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editInputText = findViewById(R.id.editInput);
        snum = getIntent().getStringExtra("snum"); // snum for dbManager later on
    }

    public void save (View v) {

        if (editInputText.getText().toString().isEmpty()) {
            editInputText.setError("Please enter an amount");
            return;
        }

        double amount = Double.parseDouble(editInputText.getText().toString()); // user inputted amount

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        dbManager.incrementCurSavings(snum, amount);
        dbManager.close();

        finish();
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
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

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        startActivity(i);
    }

    public void gosavings(View v) {
        Intent i = new Intent(this, SavingsActivity.class);
        startActivity(i);
    }
}