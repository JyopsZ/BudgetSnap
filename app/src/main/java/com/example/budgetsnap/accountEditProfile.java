package com.example.budgetsnap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class accountEditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText profileName;
    private Button saveBtn, changeImageBtn;
    private ImageView profileImage;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private String UNum;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_profile);

        // Initialize the views
        profileName = findViewById(R.id.profileName);
        saveBtn = findViewById(R.id.savebtn);
        //changeImageBtn = findViewById(R.id.changeImageBtn);
        profileImage = findViewById(R.id.profilePicture);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        // Retrieve UNum from the Intent
        Intent intent = getIntent();
        UNum = intent.getStringExtra("PK_UNUM");

        // Log the received UNum
        Log.d("accountEditProfile", "Received UNum: " + UNum);

        if (UNum == null || UNum.isEmpty()) {
            Log.e("accountEditProfile", "UNum is null or empty. Cannot query database.");
            return;
        }

        // Query the database to get the user details
        getUserDetails(UNum);

        // Set up the save button click listener
        saveBtn.setOnClickListener(v -> {
            String newUserName = profileName.getText().toString().trim();

            if (!newUserName.isEmpty()) {
                // Save the updated name and image in the database
                String newImagePath = selectedImageUri != null ? selectedImageUri.toString() : null;
                updateUserDetails(UNum, newUserName, newImagePath);

                // Optionally, show a confirmation message
                Toast.makeText(accountEditProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                // After updating, navigate back to the account activity
                Intent intent1 = new Intent(accountEditProfile.this, account.class);
                intent1.putExtra("PK_UNUM", UNum); // Pass the UNum to the account activity
                startActivity(intent1);

                // Optionally, finish this activity to prevent users from coming back to this screen
                finish();
            } else {
                // Handle the case where the name is empty
                Toast.makeText(accountEditProfile.this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the change image button click listener
        changeImageBtn.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImage.setImageBitmap(bitmap); // Display the selected image
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserDetails(String UNum) {
        String query = "SELECT UName, UEmail, UImage FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.PK_UNUM + " = ?";

        Log.d("accountEditProfile", "Executing query: " + query + " with UNum: " + UNum);

        Cursor cursor = database.rawQuery(query, new String[]{UNum});
        if (cursor != null && cursor.moveToFirst()) {
            String userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNAME));
            String userEmail = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UEMAIL));
            String userImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UIMAGE));

            Log.d("accountEditProfile", "User details - UName: " + userName + ", UEmail: " + userEmail + ", UImage: " + userImage);

            profileName.setText(userName);
            if (userImage != null) {
                // Optionally load the profile image (e.g., using Glide or Picasso)
                // Glide.with(this).load(userImage).into(profileImage);
            }

            cursor.close();
        } else {
            Log.w("accountEditProfile", "No user found with UNum: " + UNum);
        }
    }

    private void updateUserDetails(String UNum, String newUserName, String newImagePath) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String updateQuery = "UPDATE " + DatabaseHelper.TABLE_USER +
                " SET " + DatabaseHelper.UNAME + " = ?, " + DatabaseHelper.UIMAGE + " = ?" +
                " WHERE " + DatabaseHelper.PK_UNUM + " = ?";

        database.execSQL(updateQuery, new Object[]{newUserName, newImagePath, UNum});
        Log.d("accountEditProfile", "User details updated with name: " + newUserName + " and image path: " + newImagePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
