package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavingsViewing extends AppCompatActivity {

    TextView nameValue, goalAmountValue, frequencyValue, dateValue;
    TextView savingsChallView, totalAmountView;

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

    public void edit(View view) {

        Intent i = new Intent(this, SavingsEditing.class);
        startActivity(i);
    }

    public void delete(View v) {

    }

    public void inputMoney(View v) {

        Intent i = new Intent(this, SavingsInputAmount.class);
        startActivity(i);
    }

}