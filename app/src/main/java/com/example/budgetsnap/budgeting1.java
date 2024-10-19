package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class budgeting1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budgeting1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void editCategory(View v) {

        Intent i = new Intent(budgeting1.this, budgetingEditCategory.class);
        startActivity(i);
    }

    public void addExpense(View v) {

        Intent i = new Intent(budgeting1.this, budgetingAddExpense.class);
        startActivity(i);
    }

    public void gohome(View v) {
        Intent i = new Intent(budgeting1.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(budgeting1.this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(budgeting1.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(budgeting1.this, account.class);
        startActivity(i);
    }


public void back (View v){
        finish();
    }
}