package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SavingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<SavingsClass> savingsList = new ArrayList<>(); // Sample data for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSavings(); // Method call to add values to savingsList arrayList
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1  && resultCode  == RESULT_OK) {

            String name = data.getStringExtra("name");
            double goalAmount = data.getDoubleExtra("goalAmount", 0.0);
            String frequency = data.getStringExtra("frequency");
            String dateFinish = data.getStringExtra("dateFinish");
            double currentAmount = data.getDoubleExtra("currentAmount", 0.0);
            boolean isActivated = data.getBooleanExtra("isActivated", true);

            savingsList.add(new SavingsClass(name, goalAmount, frequency, dateFinish, currentAmount, isActivated));
            recyclerView.getAdapter().notifyItemInserted(savingsList.size() - 1);
        }
    }

    public void back(View v) { // When the back button is pressed, return to the previous activity (Home)

        finish();
    }

    public void addSavings(View v) { // When either the +Add savings challenge button or orange box is pressed, start a new savings challenge activity

        Intent i = new Intent(this, SavingsChallenge.class);
        startActivityForResult(i, 1);
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    private void loadSavings() { // Load hard-coded savings into arrayList for testing and demo purposes

        savingsList.add(new SavingsClass("Concert", 5000, "Daily", "10/17/2024", 0.00, true));
        savingsList.add(new SavingsClass("Tuition", 100000, "Monthly", "01/03/2025", 9878.00, false));

        SavingsAdapter savingsAdapter = new SavingsAdapter(savingsList, this);
        recyclerView.setAdapter(savingsAdapter);
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
}