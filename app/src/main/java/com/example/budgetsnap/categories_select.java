package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class categories_select extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Categories> catList;
    private TextView categoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_select);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view);
        categoryView = findViewById(R.id.Category);
        String titleCategory = getIntent().getStringExtra("Title_Category");
        categoryView.setText(titleCategory);

        if ("Home".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Faucet", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Aircon Repairs", "Oct 9", "Php 100.00"));
        }

        if ("Food".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Siomai", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Food", "Oct 9", "Php 100.00"));
        }

        if ("Bills".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Water Bill", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Electricity Bill", "Oct 9", "Php 100.00"));
        }

        if ("Health".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Vitamins", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Check-Up", "Oct 9", "Php 100.00"));
        }

        if ("Education".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Books", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Enrollment", "Oct 9", "Php 100.00"));
        }

        if ("Leisure".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Party", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Game Cards", "Oct 9", "Php 100.00"));
        }

        if ("Transportation".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Party", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Game Cards", "Oct 9", "Php 100.00"));
        }

        if ("Savings".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Car", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Education", "Oct 9", "Php 100.00"));
        }

        if ("Others".equals(titleCategory)) {
            catList = new ArrayList<>();
            catList.add(new Categories("Others", "Oct 10", "Php 1000.00"));
            catList.add(new Categories("Education", "Oct 9", "Php 100.00"));
        }

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CategoriesAdapter(catList);
        recyclerView.setAdapter(adapter);

        Spinner spinner = findViewById(R.id.dropdown_menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_frequency, R.layout.category_spinner_item);
        adapter.setDropDownViewResource(R.layout.category_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void back(View v) {
        finish();
    }

    public void viewCategoryList(View v) {

        Intent intent = new Intent(this, categories_main.class);
        startActivity(intent);
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
