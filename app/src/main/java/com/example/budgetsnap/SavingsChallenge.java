package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SavingsChallenge extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView nameText, goalText, frequencyText, dateText;
    TextView phpView, toreachView;
    EditText editName, editGoal, editDate;
    Spinner editFrequency;

    String frequency; // Store frequency chosen by user

    String PK_UNum;

    private static final String[] freq = {"Daily", "Weekly", "Monthly"}; // Reference: Adrian Tan Villador for Spinner (dropdown code)

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

        PK_UNum = getIntent().getStringExtra("PK_UNUM");

        initializeViews(); // Print out labels for input fields with red asterisks
        initializeSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle Spinner selection
        switch (position) {
            case 0:
                frequency = "Daily";
                break;
            case 1:
                frequency = "Weekly";
                break;
            case 2:
                frequency = "Monthly";
        }

        updateStatsPane(); // Update the stats pane based on the selected frequency
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        frequency = "Daily";
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
        editGoal = findViewById(R.id.editGoal);
        editDate = findViewById(R.id.editDate);

        editFrequency = findViewById(R.id.editFrequency);
        phpView = findViewById(R.id.phpView);
        toreachView = findViewById(R.id.toreachView);

        editGoal.addTextChangedListener(new TextWatcher() { // Reference for addTextChangedListener: https://stackoverflow.com/questions/28973164/how-to-update-textview-with-edittext-realtime-using-java

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(android.text.Editable s) {

                updateStatsPane();
            }
        });

        // Reference for Html: https://alfredmyers.com/2018/02/06/warning-cs0618-html-fromhtmlstring-is-obsolete-deprecated/#google_vignette

    }

    public void updateStatsPane() {

        String goalText = editGoal.getText().toString();
        String dateText = editDate.getText().toString();

        if (goalText.isEmpty() || dateText.isEmpty()) {

            phpView.setText("PHP 0.00 /day");
            toreachView.setText("to reach PHP 0.00");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        Date currentDate = new Date();
        Date dateFinish = stringToDate(dateText, "MM/dd/yyyy");
        long dayDiff = getDifferenceDays(currentDate, dateFinish);

        double goalAmount = Double.parseDouble(goalText);
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

        // So that it just shows the full amount if the end date is less than a week or a month from current date
        else if ( (frequency.equals("Weekly") && dayDiff < 7) || (frequency.equals("Monthly") && dayDiff < 30)) {

            perAmount = goalAmount;
        }

        phpView.setText(String.format("PHP %.2f /%s", perAmount, frequency.toLowerCase()));
        toreachView.setText(String.format("to reach PHP %.2f", goalAmount));
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

    public void initializeSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freq);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editFrequency.setAdapter(adapter);
        editFrequency.setOnItemSelectedListener(this);
    }

    public void back (View v) {

        finish();
    }

    public void save (View v) {

        showSavingDialog();
    }

    // Show the savings dialog
    private void showSavingDialog() {
        // Inflate the custom dialog layouts
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_savings, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking back
        dialogView.findViewById(R.id.backButton).setOnClickListener(v -> {

            String name = editName.getText().toString();
            double goalAmount = Double.parseDouble(editGoal.getText().toString());
            String date = editDate.getText().toString();

            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("goalAmount", goalAmount);
            i.putExtra("frequency", frequency);
            i.putExtra("dateFinish", date);
            i.putExtra("currentAmount", 0.0);
            i.putExtra("isActivated", true);

            setResult(RESULT_OK, i);
            finish();
            dialog.dismiss();
        });
    }

    public void goHome (View v) {

        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }


    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }


    public void gocategories(View v) {
        Intent i = new Intent(this, categories_main.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(this, account.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }

    public void gosavings(View v) {
        Intent i = new Intent(this, SavingsActivity.class);
        i.putExtra("PK_UNUM", PK_UNum);
        startActivity(i);
    }
}