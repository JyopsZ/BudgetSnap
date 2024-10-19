package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class budgetingAddExpense extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budgeting_add_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set adapter for the Spinner
        Spinner spinnerCategory = findViewById(R.id.spinnerAddbudget);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item); // values of this array is in res/values/strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    public void gohome(View v) {
        Intent i = new Intent(budgetingAddExpense.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(budgetingAddExpense.this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(budgetingAddExpense.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(budgetingAddExpense.this, account.class);
        startActivity(i);
    }

    public void back (View v){
        finish();
    }
}