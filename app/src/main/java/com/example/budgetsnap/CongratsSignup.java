package com.example.budgetsnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CongratsSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_congrats_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Savings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void continueSignup (View v) {

        /* Legacy code from MCO2. Break in case of emergency.
        Intent i = getIntent(); // Retrieve data after sign up
        String name = i.getStringExtra("name");
        String birthday = i.getStringExtra("birthday");
        String email = i.getStringExtra("email");
        String password = i.getStringExtra("password");

        i2.putExtra("name", name);
        i2.putExtra("birthday", birthday);
        i2.putExtra("email", email);
        i2.putExtra("password", password);
        */

        Intent i2 = new Intent(CongratsSignup.this, LoginActivity.class); // Passing data to LoginActivity for verification
        startActivity(i2);
    }
}