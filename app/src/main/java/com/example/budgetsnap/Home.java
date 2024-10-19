package com.example.budgetsnap;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {

    private List<String> xValues = Arrays.asList("Savings Goal Progress", "Total Income", "Total Expenses");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Savings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up Spinner
        Spinner spinner = findViewById(R.id.spinner_frequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                Toast.makeText(Home.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        BarChart barChart = findViewById(R.id.chart);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 3000f));
        entries.add(new BarEntry(1, 6000f));
        entries.add(new BarEntry(2, 4000f));
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(10000f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(0f);
        yAxis.setAxisLineColor(android.R.color.black);
        yAxis.setLabelCount(0);

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

// Enable value labels above the bars
        dataSet.setDrawValues(true); // Re-enable value labels

// Create a custom ValueFormatter to remove decimals
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format("%d", (int) barEntry.getY());
            }
        });
        dataSet.setValueTextSize(8f);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);

    }

    public void gonotif(View v) {
        Intent i = new Intent(this, Notifications.class);
        startActivity(i);
    }

    public void savings(View v) {

        Intent intent = new Intent(this, SavingsActivity.class);
        startActivity(intent);
    }


//    public void gobudgeting(View v) {
//        Intent i = new Intent(this, budgeting1.class);
//        startActivity(i);
//    }
//    public void gotransactions(View v) {
//        Intent i = new Intent(this, transaction1.class);
//        startActivity(i);
//    }
//
//    public void gocategories(View v) {
//        Intent i = new Intent(this, categories.class);
//        startActivity(i);
//    }
//    public void goaccount(View v) {
//        Intent i = new Intent(this, account.class);
//        startActivity(i);
//    }
}
