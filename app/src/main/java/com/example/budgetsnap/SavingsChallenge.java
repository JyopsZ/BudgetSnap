package com.example.budgetsnap;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavingsChallenge extends AppCompatActivity {

    TextView nameText, goalText, frequencyText, dateText;
    EditText editName, editGoal, editFrequency, editDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_savings_challenge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews(); // Print out labels for input fields with red asterisks
    }

    private void initializeViews() {

        nameText = findViewById(R.id.nameText);
        String nText = "<font color=#000000>Name</font> <font color=#E16162>*</font>";
        nameText.setText(Html.fromHtml(nText, Html.FROM_HTML_MODE_LEGACY));

        goalText = findViewById(R.id.goalText);
        String gText = "<font color=#000000>Goal Amount</font> <font color=#E16162>*</font>";
        goalText.setText(Html.fromHtml(gText, Html.FROM_HTML_MODE_LEGACY));

        frequencyText = findViewById(R.id.frequencyText);
        String fText = "<font color=#000000>Frequency</font> <font color=#E16162>*</font>";
        frequencyText.setText(Html.fromHtml(fText, Html.FROM_HTML_MODE_LEGACY));

        dateText = findViewById(R.id.dateText);
        String pText = "<font color=#000000>Date to finish</font> <font color=#E16162>*</font>";
        dateText.setText(Html.fromHtml(pText, Html.FROM_HTML_MODE_LEGACY));

        editName = findViewById(R.id.editName);
        editGoal = findViewById(R.id.editBirthday);
        editFrequency = findViewById(R.id.editEmail);
        editDate = findViewById(R.id.editDate);

        // Reference for Html: https://alfredmyers.com/2018/02/06/warning-cs0618-html-fromhtmlstring-is-obsolete-deprecated/#google_vignette
    }

    public void back (View v) {

        finish();
    }
}