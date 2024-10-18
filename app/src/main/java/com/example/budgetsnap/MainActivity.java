package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        Intent i = new Intent(MainActivity.this, categories_main.class);
        startActivity(i);
        finish();

//        // automatically go to onboarding1 after 3 seconds
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // intent to navigate to onboarding1
//                Intent i = new Intent(MainActivity.this, onboarding1.class);
//                startActivity(i);
//
//                // Optionally finish MainActivity so the user cannot return to it
//                finish();
//            }
//        }, 3000); // 3-second delay (can edit this to extend or shorten the delay)

    }
}