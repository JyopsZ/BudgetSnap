package com.example.budgetsnap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SavingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<SavingsClass> savingsList = new ArrayList<>(); // Sample data for testing
    private String unum;

    FirebaseFirestore db;

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

        unum = getIntent().getStringExtra("PK_UNUM");

        syncSQLiteSavings(); // Firebase to sqlite, before storing in arrayList
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1  && resultCode  == RESULT_OK) {

            String name = data.getStringExtra("name");
            double currentAmount = data.getDoubleExtra("currentAmount", 0.0);
            double goalAmount = data.getDoubleExtra("goalAmount", 0.0);
            String frequency = data.getStringExtra("frequency");
            String dateFinish = data.getStringExtra("dateFinish");
            boolean isActivated = data.getBooleanExtra("isActivated", true);

            DBManager dbManager = new DBManager(this);
            dbManager.open();

            String maxSNum = dbManager.getSavingsMax();
            int currentSavings = Integer.parseInt(maxSNum.substring(1));
            String nextSavings = String.format("S%04d", currentSavings + 1);

            SavingsClass newSavings = new SavingsClass (
                    nextSavings,
                    name,
                    currentAmount,
                    goalAmount,
                    frequency,
                    dateFinish,
                    isActivated,
                    unum
            );

            dbManager.insertSavings(newSavings);
            dbManager.close();

            syncSQLiteSavings(); // sync after new savings challenge is created
        }
    }

    protected void onResume() {

        super.onResume();
        loadSavings(); // Method call to add values to savingsList arrayList
    }

    public void syncSQLiteSavings () {

        db = FirebaseFirestore.getInstance();
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        db.collection("SAVINGS").get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (DocumentSnapshot doc : queryDocumentSnapshots) {

                SavingsClass savings = new SavingsClass (

                        doc.getId(),
                        doc.getString("SName"),
                        doc.getDouble("SCurrentAmount"),
                        doc.getDouble("SGoalAmount"),
                        doc.getString("SFrequency"),
                        doc.getString("SDate"),
                        doc.getBoolean("SStatus"),
                        doc.getString("UNum")
                );
                dbManager.insertSavings(savings);
            }
            dbManager.close();
            loadSavings();
        });
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

        DBManager dbManager = new DBManager(this);
        dbManager.open();

        savingsList.clear();

        Cursor cursor = dbManager.fetchSavings(unum);

        if (cursor.moveToFirst()) {
            do {

                savingsList.add(new SavingsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PK_SNUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SNAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.SCURRENTAMOUNT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.SGOALAMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SFREQUENCY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SDATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SSTATUS)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FK_SUNUM))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        dbManager.close();

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