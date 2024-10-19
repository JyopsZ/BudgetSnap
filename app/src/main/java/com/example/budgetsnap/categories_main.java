package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class categories_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void viewCategory(View v) {

        Intent i = new Intent(this, categories_select.class);
        startActivity(i);
    }
    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void goTransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }

    public void goCategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        startActivity(i);
    }
    public void goAccount(View v) {
        Intent i = new Intent(this, account.class);
        startActivity(i);
    }
}