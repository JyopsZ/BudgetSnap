package com.example.budgetsnap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity {

    private List<String> xValues = Arrays.asList("Savings Goal Progress", "Total Income", "Total Expenses");
    private String PK_Unum;
    private DatabaseHelper dbHelper;
    private String selectedItem = "Daily";
    private Integer TB1 = 0, TB2 = 0, TB3 = 0;

    @SuppressLint("SuspiciousIndentation")
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

        dbHelper = new DatabaseHelper(this);

        // CURRENT USER'S NUMBER
        PK_Unum = getIntent().getStringExtra("PK_UNUM");

        if (PK_Unum == null) { // If PK_Unum is not given value by prev activity, get value from sharedPref

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            PK_Unum = prefs.getString("PK_UNUM", "");
        }

        else {

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("PK_UNUM", PK_Unum);
            editor.apply();
        }



        Spinner spinner = findViewById(R.id.spinner_frequency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItem = parentView.getItemAtPosition(position).toString();
                updateTransactionValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        BarChart barChart = findViewById(R.id.chart);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, TB2));
        entries.add(new BarEntry(1, TB3));
        entries.add(new BarEntry(2, TB1));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(100000f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(0f);
        yAxis.setAxisLineColor(android.R.color.black);
        yAxis.setLabelCount(0);

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        dataSet.setDrawValues(true);

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

        updateBalanceTextView();
        // Testing only
        //Toast.makeText(this, "Current User ID: " + PK_Unum, Toast.LENGTH_SHORT).show();
    }


    private void updateBalanceTextView() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT UIncome FROM USER WHERE UNum = ?";
        String[] queryArgs = {PK_Unum};


        TextView balanceTextView = findViewById(R.id.balance_Text);

        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, queryArgs);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("UIncome");
                if (columnIndex != -1) {
                    double uIncome = cursor.getDouble(columnIndex);

                    String formattedIncome = String.format(Locale.getDefault(), "Php %,.2f", uIncome);

                    balanceTextView.setText(formattedIncome);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateTransactionValues();
    }

    private void updateTransactionValues() {
        double TI = 10;
        double TE = 0;

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String query = null;
        String UNum = PK_Unum;
        String[] queryArgs = {UNum};

        Log.d("SelectedItem", "Selected item: " + selectedItem);

        Cursor cursor = null;

        switch (selectedItem) {
            case "Daily":
                query = "SELECT TAmount, TStatus " +
                        "FROM TRANSACTIONS " +
                        "WHERE UNum = ? " +
                        "AND strftime(TDate) = strftime('%m/%d/%Y', 'now', 'localtime')";
                break;

            case "Weekly":
                query = "SELECT TAmount, TStatus " +
                        "FROM TRANSACTIONS " +
                        "WHERE UNum = ? " +
                        "AND strftime(TDate) >= strftime('%m/%d/%Y', 'now', '-7 days', 'localtime')";
                break;

            case "Monthly":
                query = "SELECT TAmount, TStatus " +
                        "FROM TRANSACTIONS " +
                        "WHERE UNum = ? " +
                        "AND strftime(TDate) >= strftime('%m/%d/%Y', 'now', '-30 days', 'localtime')";
                break;

            case "Yearly":
                query = "SELECT TAmount, TStatus " +
                        "FROM TRANSACTIONS " +
                        "WHERE UNum = ? " +
                        "AND strftime('TDate') >= strftime('%m/01/%Y', 'now', 'localtime')";
                break;

            default:
                query = null;
                Log.w("UpdateTransactions", "Unhandled selectedItem: " + selectedItem);
                break;
        }

        if (query != null) {
            cursor = database.rawQuery(query, queryArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double amount = cursor.getDouble(cursor.getColumnIndex("TAmount"));
                    int status = cursor.getInt(cursor.getColumnIndex("TStatus"));

                    if (!cursor.isNull(cursor.getColumnIndex("TAmount"))) {
                        if (status == 1) {
                            TI += amount;
                        } else {
                            TE += amount;
                        }
                    }

                } while (cursor.moveToNext());
            }
        }


        if (cursor != null) {
            cursor.close();
        }

        Log.d("TransactionQuery", "Query Args: " + Arrays.toString(queryArgs));
        Log.d("TransactionQuery", "Total Income: " + TI + ", Total Expenses: " + TE);


        TextView TIText = findViewById(R.id.Php_1);
        TextView TEText = findViewById(R.id.Php_2);

        TB2 = (int) TI;
        TB3 = (int) TE;
        TB1 = 5000;

        if (TIText != null) TIText.setText("PHP " + String.format("%.2f", TI));
        if (TEText != null) TEText.setText("PHP " + String.format("%.2f", TE));

        refreshChart();
    }

    private void refreshChart() {
        BarChart barChart = findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, TB1));
        entries.add(new BarEntry(1, TB2));
        entries.add(new BarEntry(2, TB3));

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setDrawValues(true);
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void gonotif(View v) {
        Intent i = new Intent(this, Notifications.class);
        startActivity(i);
    }

    public void gosavings(View v) {

        Intent i = new Intent(this, SavingsActivity.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gobudgeting(View v) {
        Intent i = new Intent(this, budgeting1.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_Unum);
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
