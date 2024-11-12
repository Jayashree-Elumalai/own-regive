package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText nameInput, contactInput, itemInput, othersInput, descriptionInput;
    private Button saveButton;
    Button charityButton, everyoneButton;
    Button clothesButton, booksButton, furnitureButton, appliancesButton, shoesButton, kitchenwareButton, toiletriesButton, beddingButton, stationeryButton;

    private boolean isCategorySelected = false;
    private boolean isRecipientSelected = false;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadImage;
    private Uri imageUri;
    private TextView userNameTextView;
    private int userId;
    private String recipientType = "";
    private String selectedCategory = "";
    private int donationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the user_id and donation_id passed from my_donations_pg
        userId = getIntent().getIntExtra("user_id", -1);
        donationId = getIntent().getIntExtra("donation_id", -1);

        // Initialize UI elements
        nameInput = findViewById(R.id.nameInput);
        contactInput = findViewById(R.id.contactInput);
        itemInput = findViewById(R.id.itemInput);
        othersInput = findViewById(R.id.othersInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        charityButton = findViewById(R.id.charityButton);
        everyoneButton = findViewById(R.id.everyoneButton);

        clothesButton = findViewById(R.id.clothesButton);
        booksButton = findViewById(R.id.booksButton);
        furnitureButton = findViewById(R.id.furnitureButton);
        appliancesButton = findViewById(R.id.appliancesButton);
        shoesButton = findViewById(R.id.shoesButton);
        kitchenwareButton = findViewById(R.id.kitchenwareButton);
        toiletriesButton = findViewById(R.id.toiletriesButton);
        beddingButton = findViewById(R.id.beddingButton);
        stationeryButton = findViewById(R.id.stationeryButton);

        saveButton = findViewById(R.id.saveButton);
        uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setOnClickListener(v -> openImagePicker());
        userNameTextView = findViewById(R.id.userName);

        // Get the user's name passed from LoginActivity or  my_donations_pg
        String userName = getIntent().getStringExtra("userName");
        if (userName != null) {
            userNameTextView.setText(userName); // Display the user's name
        }

        if (donationId != -1) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
            Cursor cursor = dbHelper.getDonationById(donationId);
            if (cursor != null && cursor.moveToFirst()) {
                itemInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("item_name")));
                descriptionInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                contactInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("donor_contact")));

                String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("item_image"));
                if (imageUriString != null) {
                    imageUri = Uri.parse(imageUriString);
                    uploadImage.setImageURI(imageUri);
                }
                cursor.close();
            }
        }
        // Save button action
        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String contact = contactInput.getText().toString();
            String item = itemInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String otherCategory = othersInput.getText().toString();
            String image = imageUri != null ? imageUri.toString() : null; // Save image URI as a string

            // Check if category is selected or Others input is filled
            if (selectedCategory.isEmpty() && othersInput.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select a category or specify 'Others'", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate input and store the donation data
            if (name.isEmpty() || contact.isEmpty() || item.isEmpty() || description.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
            // Check if image is selected
            else if (imageUri == null) {
                Toast.makeText(MainActivity.this, "Please select an image to upload", Toast.LENGTH_SHORT).show();
            }

            // Validate "Charity" or "Everyone" button
            else if (recipientType.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please choose to donate to 'Charity' or 'Everyone'", Toast.LENGTH_SHORT).show();
            } else {
                // All conditions met; proceed with saving the data
                Toast.makeText(MainActivity.this, "Donation saved", Toast.LENGTH_SHORT).show();

                // If "Others" input is filled, set the category from the input, otherwise use the selected category
                String categoryToSave = !othersInput.getText().toString().isEmpty() ? othersInput.getText().toString() : selectedCategory;

                // Save the data into the database
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this);
                boolean isSaved;
                // check donationid to see if adding new item or editing item
                if (donationId == -1) {
                    isSaved = dbHelper.addDonation(userId, item, selectedCategory, description, image, "Available", recipientType, contact);
                } else {
                    isSaved = dbHelper.updateDonation(donationId, userId, item, selectedCategory, description, image, "Available", recipientType, contact);
                }
                if (isSaved) {
                    // Start my_donations_pg activity
                    Intent intent = new Intent(MainActivity.this, my_donations_pg.class);
                    intent.putExtra("user_id", userId);  // Pass userId back to my_donations_pg
                    startActivity(intent); // Navigate to my_donations_pg activity

                } else {
                    Toast.makeText(MainActivity.this, "Error saving donation", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // choose donate to
        charityButton.setOnClickListener(v -> {
            recipientType = "Charity";  // <-- Set recipient type to "Charity"
            isRecipientSelected = true; // <-- You can still track selection
        });

        everyoneButton.setOnClickListener(v -> {
            recipientType = "Everyone";  // <-- Set recipient type to "Everyone"
            isRecipientSelected = true; // <-- You can still track selection
        });

        //choose category
        View.OnClickListener categoryClickListener = v -> {
            selectedCategory = ((Button) v).getText().toString(); // <-- Set selected category based on the button clicked
            isCategorySelected = true; // <-- Mark category as selected
        };

        clothesButton.setOnClickListener(categoryClickListener);
        booksButton.setOnClickListener(categoryClickListener);
        furnitureButton.setOnClickListener(categoryClickListener);
        appliancesButton.setOnClickListener(categoryClickListener);
        shoesButton.setOnClickListener(categoryClickListener);
        kitchenwareButton.setOnClickListener(categoryClickListener);
        toiletriesButton.setOnClickListener(categoryClickListener);
        beddingButton.setOnClickListener(categoryClickListener);
        stationeryButton.setOnClickListener(categoryClickListener);
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage.setImageURI(imageUri); // Set the selected image in the ImageView
        }
    }
}





