package com.example.budgetsnap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class accountEditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText profileName;
    private Button saveBtn;
    private ImageView profileImage;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private String UNum;
    private Bitmap selectedBitmap;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_profile);

        profileName = findViewById(R.id.profileName);
        saveBtn = findViewById(R.id.savebtn);
        profileImage = findViewById(R.id.profilePicture);
        userEmailTextView = findViewById(R.id.email);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        Intent intent = getIntent();
        UNum = intent.getStringExtra("PK_UNUM");

        Log.d("AccountEditProfile", "Received UNum: " + UNum);

        if (UNum == null || UNum.isEmpty()) {
            Log.e("AccountEditProfile", "UNum is null or empty. Cannot query database.");
            return;
        }

        getUserDetails(UNum);

        profileImage.setOnClickListener(v -> openGallery());

        saveBtn.setOnClickListener(v -> {
            String newUserName = profileName.getText().toString().trim();

            if (!newUserName.isEmpty()) {
                updateUserDetails(UNum, newUserName, selectedBitmap);

                Intent intent1 = new Intent(accountEditProfile.this, account.class);
                intent1.putExtra("PK_UNUM", UNum);
                startActivity(intent1);

                finish();
            } else {
                Toast.makeText(accountEditProfile.this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                Log.e("AccountEditProfile", "Error loading image", e);
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void getUserDetails(String UNum) {
        String query = "SELECT " + DatabaseHelper.UNAME + ", " + DatabaseHelper.UIMAGE + ", " + DatabaseHelper.UEMAIL +
                " FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.PK_UNUM + " = ?";
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, new String[]{UNum});
            if (cursor != null && cursor.moveToFirst()) {
                String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UNAME));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.UEMAIL));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.UIMAGE));

                profileName.setText(userName);
                userEmailTextView.setText(userEmail);

                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            } else {
                Log.w("AccountEditProfile", "No user found with UNum: " + UNum);
            }
        } catch (Exception e) {
            Log.e("AccountEditProfile", "Error querying user details", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateUserDetails(String UNum, String newUserName, Bitmap profileImageBitmap) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        byte[] imageBytes = profileImageBitmap != null ? bitmapToByteArray(profileImageBitmap) : null;

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.UNAME, newUserName);
        if (imageBytes != null) {
            values.put(DatabaseHelper.UIMAGE, imageBytes);
        }

        int rowsUpdated = database.update(DatabaseHelper.TABLE_USER, values, DatabaseHelper.PK_UNUM + " = ?", new String[]{UNum});

        if (rowsUpdated > 0) {
            Log.d("UpdateUserDetails", "User details updated locally in SQLite.");
        } else {
            Log.e("UpdateUserDetails", "Failed to update user details in SQLite.");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("USER").document(UNum);

        String imageString = null;
        if (profileImageBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("UName", newUserName);
        if (imageString != null) {
            userUpdates.put("UImage", imageString);  // Store the Base64 encoded image string in Firebase
        }

        // Update Firebase Firestore
        userRef.update(userUpdates)
                .addOnSuccessListener(aVoid -> Log.d("UpdateUserDetails", "User details updated in Firebase."))
                .addOnFailureListener(e -> Log.e("UpdateUserDetails", "Error updating user details in Firebase", e));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
