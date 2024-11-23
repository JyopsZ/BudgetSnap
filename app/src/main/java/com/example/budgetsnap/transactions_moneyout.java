package com.example.budgetsnap;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class transactions_moneyout extends AppCompatActivity {

    private Uri selectedImageUriMoneyOut; // Store the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_moneyout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize and set adapter for the Spinner
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item); // values of this array is in res/values/strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set onClickListener for the plus button to open a dialog
        ImageView plusButton = findViewById(R.id.menu_plus2);
        plusButton.setOnClickListener(this::BtnClickedPlus2); // Image clickable on bottom menu

        // Set up Attach Image button
        Button buttonAttachImage = findViewById(R.id.buttonAttachImage);
        buttonAttachImage.setOnClickListener(v -> openImagePicker());
    }

    // ActivityResultLauncher for modern image picker handling
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUriMoneyOut = result.getData().getData(); // Get the image URI
                    if (selectedImageUriMoneyOut != null) {
                        Toast.makeText(this, "Image Selected: " + selectedImageUriMoneyOut.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Filter for image files only
        imagePickerLauncher.launch(intent); // Launch the image picker
    }

    // Button to switch to Money In screen (button below the transaction word)
    public void BtnMoneyInBtn2(View v) {
        Intent i = new Intent(transactions_moneyout.this, transaction_moneyin.class); // Switch to money in screen
        startActivity(i);
    }

    // Button to switch to Money out screen (button below the transaction word)
    public void BtnMoneyOutBtn2(View v) {
        Intent i = new Intent(transactions_moneyout.this, transactions_moneyout.class); // Switch to money in screen
        startActivity(i);
    }


    // Call prompt when plus button is clicked
    public void BtnClickedPlus2(View view) {
        showTransactionDialog();
    }

    // Show the "Money In" or "Money Out" prompt dialog
    private void showTransactionDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle clicking on "Money In"
        dialogView.findViewById(R.id.moneyInOption).setOnClickListener(v -> {
            Intent i = new Intent(transactions_moneyout.this, transaction_moneyin.class); // Switch to money in screen
            startActivity(i);
            dialog.dismiss(); // Close the dialog
        });

        // No action for money out option as we're on the money out screen
        dialogView.findViewById(R.id.moneyOutOption).setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
        });

        // Handle clicking on the "Cancel" button
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            dialog.dismiss(); // Close dialog on "Cancel"
        });
    }

    public void gohome(View v) {
        Intent i = new Intent(transactions_moneyout.this, Home.class);
        startActivity(i);
    }

    public void gotransactions(View v) {
        Intent i = new Intent(this, Transaction1.class);
        startActivity(i);
    }

    public void gonotif(View v) {
        Intent i = new Intent(transactions_moneyout.this, Notifications.class);
        startActivity(i);
    }

    public void gocategories(View v) {
        Intent i = new Intent(transactions_moneyout.this, categories_main.class);
        startActivity(i);
    }

    public void goaccount(View v) {
        Intent i = new Intent(transactions_moneyout.this, account.class);
        startActivity(i);
    }
}
