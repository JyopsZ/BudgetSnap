package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view);

        reminderList = new ArrayList<>();
        reminderList.add(new Reminder("October 14", "Daily Reminder", "Don't forget to log today's expenses!"));
        reminderList.add(new Reminder("October 14 - 20", "Weekly Progress Update", "You’ve spent 80% of your budget for the week of October 14-20. Be mindful of your spending!"));
        reminderList.add(new Reminder("October 2024", "Monthly Savings Goal", "Great job! You’re 90% towards your October savings goal. Just a little more to go!"));

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(adapter);
    }

    public void back(View v) {
        finish();
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
