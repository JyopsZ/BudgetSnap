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

    public String Title_Category;
    private String PK_Unum;
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
        PK_Unum = getIntent().getStringExtra("PK_UNUM");
    }



    public void viewHome(View v) {
        Title_Category = "Home";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewFood(View v) {
        Title_Category = "Food";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewBills(View v) {
        Title_Category = "Bills";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewHealth(View v) {
        Title_Category = "Health";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewEducation(View v) {
        Title_Category = "Education";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewLeisure(View v) {
        Title_Category = "Leisure";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewTransportation(View v) {
        Title_Category = "Transportation";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewSavings(View v) {
        Title_Category = "Savings";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void viewOthers(View v) {
        Title_Category = "Others";
        Intent i = new Intent(this, categories_select.class);
        i.putExtra("Title_Category", Title_Category);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goHome (View v) {
        Intent i = new Intent(this, Home.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goTransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goCategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }
    public void goAccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }
}