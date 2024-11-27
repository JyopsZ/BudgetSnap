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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Home extends AppCompatActivity {

    private List<String> xValues = Arrays.asList( "Total Income", "Total Expenses");
    private String PK_Unum;
    private DatabaseHelper dbHelper;
    private String selectedItem = "Daily";
    private Integer  TB2 = 0, TB3 = 0;

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

        updateSavingsGoal();

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
        updateSavingsGoal();
    }

    private void updateTransactionValues() {
        double TI = 0;
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

        if (TIText != null) TIText.setText("PHP " + String.format("%.2f", TI));
        if (TEText != null) TEText.setText("PHP " + String.format("%.2f", TE));

        refreshChart();
    }

    private void refreshChart() {
        BarChart barChart = findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, TB2));
        entries.add(new BarEntry(1, TB3));

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

    public void returnToMainActivity(View v) {
        Intent i= new Intent(this, MainActivity.class);
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
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_Unum);
        startActivity(i);
    }

    public void updateSavingsGoal() { // Based on the same algorithm and implementation as SavingsChallenge
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT SGoalAmount, SDate, SFrequency FROM SAVINGS WHERE UNum = ? ORDER BY SNum ASC LIMIT 1";
        String[] queryString = {PK_Unum};
        TextView savingsGoalText = findViewById(R.id.savinggoal_Text2);

        Cursor cursor = database.rawQuery(query, queryString);

        if (cursor != null && cursor.moveToFirst()) {
            int goalAmountIndex = cursor.getColumnIndex("SGoalAmount");
            int dateIndex = cursor.getColumnIndex("SDate");
            int frequencyIndex = cursor.getColumnIndex("SFrequency");

            double goalAmount = cursor.getDouble(goalAmountIndex);
            String date = cursor.getString(dateIndex);
            String frequency = cursor.getString(frequencyIndex);

            Date dateFinish = stringToDate(date, "MM/dd/yyyy");
            Date currentDate = new Date();
            long dayDiff = getDifferenceDays(currentDate, dateFinish);

            double perAmount = 0.0;
            if (frequency.equals("Daily")) {

                perAmount = goalAmount / dayDiff;
            }

            else if (frequency.equals("Weekly") && dayDiff > 7) {

                perAmount = goalAmount / (dayDiff / 7);
            }

            else if (frequency.equals("Monthly") && dayDiff > 30) {

                perAmount = goalAmount / (dayDiff / 30);
            }

            else if ((frequency.equals("Weekly") && dayDiff < 7) ||
                    (frequency.equals("Monthly") && dayDiff < 30)) {

                perAmount = goalAmount;
            }

            savingsGoalText.setText(String.format(Locale.getDefault(), "Php: %.2f/%s", perAmount, frequency.toLowerCase()));
        } else {
            savingsGoalText.setText("Php: 0.00/day");
        }

        cursor.close();
        database.close();
    }

    public static long getDifferenceDays(Date d1, Date d2) { // Reference: https://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-java

        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public Date stringToDate(String date, String format) { // Reference: https://stackoverflow.com/questions/8573250/android-how-can-i-convert-string-to-date

        if(date==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
        Date stringDate = simpledateformat.parse(date, pos);

        return stringDate;
    }
}
