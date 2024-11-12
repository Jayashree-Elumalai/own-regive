package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;


public class Donation_pg extends AppCompatActivity {

    // Declare UI elements
    private ImageButton searchButton;
    private Button getButton;
    private EditText searchInput;
    private int userId; // Declare a variable to store the userId
    private TextView userNameTextView;
    private LinearLayout donationsLayout; // The parent layout that will contain the CardViews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_donationpg); // Set the all_donations page layout

        // Initialize UI elements using findViewById
        searchButton = findViewById(R.id.searchButton);

        searchInput = findViewById(R.id.searchInput);
        userNameTextView = findViewById(R.id.userName);
        donationsLayout = findViewById(R.id.donationsLayout); // Initialize the layout for dynamic CardViews

        // Retrieve the userId passed from LoginActivity
        userId = getIntent().getIntExtra("user_id", -1);

        // Fetch the username from the database and set it to the TextView
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this); // Create an instance of the database helper
        String userName = dbHelper.getUserNameById(userId); // Fetch the username using the userId
        if (userName != null) {
            userNameTextView.setText(userName); // Set the fetched username in the TextView
        }
        displayAllDonations();

    }

    // Method to fetch all donations and display them in the CardViews
    private void displayAllDonations() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        Cursor cursor = dbHelper.getAllDonations(); // Fetch all donations from the database

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve donor's user ID from Donations table
                int donorUserId = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_DONATION_USER_ID));
                // Skip the current donation if it belongs to the logged-in user
                if (donorUserId == userId) {
                    continue; // Skip this donation and move to the next
                }

                // Retrieve donation details from the cursor
                int donationId = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_DONATION_ID));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_NAME));
                String itemCategory = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_CATEGORY));
                String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_DESCRIPTION));
                String itemImage = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_IMAGE));
                String donorContact = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_DONOR_CONTACT));

                // Fetch the donor's username
                String donorUsername = dbHelper.getUserNameById(donorUserId);
                Log.d("DonationInfo", "Donor Username: " + donorUsername + ", Item: " + itemName);
                // Inflate the cardview_alldonation layout for each donation
                View donationView = LayoutInflater.from(this).inflate(R.layout.cardview_alldonation, donationsLayout, false);

                // Initialize the UI elements in the cardview_alldonation layout
                ImageView imageView = donationView.findViewById(R.id.uploadImage);
                TextView nameTextView = donationView.findViewById(R.id.itemNameTextView);
                TextView categoryTextView = donationView.findViewById(R.id.itemCategoryTextView);
                TextView descriptionTextView = donationView.findViewById(R.id.itemDescriptionTextView);
                TextView donorNameTextView = donationView.findViewById(R.id.donorName);
                TextView donorContactTextView = donationView.findViewById(R.id.donorContactTextView);
                Button getButton = donationView.findViewById(R.id.getButton);

                // Populate data into the UI elements
                nameTextView.setText(itemName);
                categoryTextView.setText(itemCategory);
                descriptionTextView.setText(itemDescription);
                donorNameTextView.setText(donorUsername);
                donorContactTextView.setText(donorContact); // Display donor contact information


                // Set image if available
                if (itemImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(itemImage);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.default_image); // Default image if none is available
                }

                // Set up the getButton click listener after UI components are initialized
                getButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Log to check if the listener is triggered
                        Log.d("Donation_pg", "getButton clicked for donation ID: " + donationId);
                        // Show Toast to confirm the button click
                        Toast.makeText(Donation_pg.this, "Request Item", Toast.LENGTH_SHORT).show();

                        // Try to save the donationId only in the database
                        boolean success = dbHelper.saveDonationIdOnly(donationId);
                        if (success) {
                            Toast.makeText(Donation_pg.this, "Donation ID saved successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Donation_pg.this, "Failed to save Donation ID.", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(Donation_pg.this, RequestPage.class);
                        startActivity(intent);
                    }
                });
                // Add the donationView to the donationsLayout
                donationsLayout.addView(donationView);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}