package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class account extends AppCompatActivity {

    TextView textRestaurant, textCategory, textPrice;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        frameLayout = findViewById(R.id.frameLayout);
        textRestaurant = findViewById(R.id.textRestaurant);
        textCategory = findViewById(R.id.textCategory);
        textPrice = findViewById(R.id.textPrice);

        // Ensure padding respects system bars (e.g., status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Navigation methods
    public void goHome(View v) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void goTransactions(View v) {
        Intent intent = new Intent(this, Transaction1.class);
        startActivity(intent);
    }

    public void goCategories(View v) {
        Intent intent = new Intent(this, categories_main.class);
        startActivity(intent);
    }

    public void goAccount(View v) {

            Intent intent = new Intent(this, account.class);
            startActivity(intent);

    }

    /*public void goFriends(View v) {
        Intent intent = new Intent(this, AccountViewFriends.class);
        startActivity(intent);
    }*/

    public void goSavings(View v) {
        Intent intent = new Intent(this, SavingsActivity.class);
        startActivity(intent);
    }

   /* public void goEdit(View v) {
        Intent intent = new Intent(this, accountEditProfile.class);
        startActivity(intent);
    }*/
}
