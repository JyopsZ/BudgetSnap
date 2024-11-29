package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class budgetingEditCategory extends AppCompatActivity {

    private CheckBox checkBoxHome, checkBoxFood, checkBoxBills, checkBoxHealth, checkBoxEducation, checkBoxLeisure, checkBoxTransportation, checkBoxSavings, checkBoxOthers;
    private EditText editTextHomeAmount, editTextFoodAmount, editTextBillsAmount, editTextHealthAmount, editTextEducationAmount, editTextLeisureAmount, editTextTransportationAmount, editTextSavingsAmount, editTextOthersAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting_edit_category);

        // Initialize CheckBoxes and EditTexts
        checkBoxHome = findViewById(R.id.checkBoxHome);
        editTextHomeAmount = findViewById(R.id.editTextHomeAmount);

        checkBoxFood = findViewById(R.id.checkBoxFood);
        editTextFoodAmount = findViewById(R.id.editTextFoodAmount);

        checkBoxBills = findViewById(R.id.checkBoxBills);
        editTextBillsAmount = findViewById(R.id.editTextBillsAmount);

        checkBoxHealth = findViewById(R.id.checkBoxHealth);
        editTextHealthAmount = findViewById(R.id.editTextHealthAmount);

        checkBoxEducation = findViewById(R.id.checkBoxEducation);
        editTextEducationAmount = findViewById(R.id.editTextEducationAmount);

        checkBoxLeisure = findViewById(R.id.checkBoxLeisure);
        editTextLeisureAmount = findViewById(R.id.editTextLeisureAmount);

        checkBoxTransportation = findViewById(R.id.checkBoxTranspo);
        editTextTransportationAmount = findViewById(R.id.editTextTransportationAmount);

        checkBoxSavings = findViewById(R.id.checkBoxSavings);
        editTextSavingsAmount = findViewById(R.id.editTextSavingsAmount);

        checkBoxOthers = findViewById(R.id.checkBoxOthers);
        editTextOthersAmount = findViewById(R.id.editTextOthersAmount);

        // Set listeners to toggle EditText visibility
        setupCheckboxListener(checkBoxHome, editTextHomeAmount);
        setupCheckboxListener(checkBoxFood, editTextFoodAmount);
        setupCheckboxListener(checkBoxBills, editTextBillsAmount);
        setupCheckboxListener(checkBoxHealth, editTextHealthAmount);
        setupCheckboxListener(checkBoxEducation, editTextEducationAmount);
        setupCheckboxListener(checkBoxLeisure, editTextLeisureAmount);
        setupCheckboxListener(checkBoxTransportation, editTextTransportationAmount);
        setupCheckboxListener(checkBoxSavings, editTextSavingsAmount);
        setupCheckboxListener(checkBoxOthers, editTextOthersAmount);

        // Handle insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupCheckboxListener(CheckBox checkBox, EditText editText) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editText.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
            }
        });
    }

    public void gohome(View v) {
        Intent i = new Intent(budgetingEditCategory.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(budgetingEditCategory.this, Transaction1.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(budgetingEditCategory.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(budgetingEditCategory.this, account.class);
        startActivity(i);
    }

    public void back(View v) {
        finish();
    }
}
