package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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


public class my_donations_pg extends AppCompatActivity {

    // Declare UI elements
    private ImageButton searchButton,editButton, deleteButton;
    private Button  addButton,donationsButton;
    private EditText searchInput;
    private int userId; // Declare a variable to store the userId
    private TextView userNameTextView;
    private LinearLayout donationsLayout; // The parent layout that will contain the CardViews


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_donations_page); // Set the donations page layout

        // Initialize UI elements using findViewById

        addButton = findViewById(R.id.addButton);
        searchButton = findViewById(R.id.searchButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        donationsButton = findViewById(R.id.donationsButton);

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

        // Fetch the donations for the logged-in user and display them in CardViews
        displayUserDonations(userId);

        donationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open donation_pg
                Intent intent = new Intent(my_donations_pg.this, Donation_pg.class);
                intent.putExtra("user_id", userId);  // Pass userId to donation_pg

                // Pass the username to donation_pg
                String userName = userNameTextView.getText().toString();
                intent.putExtra("userName", userName);  // Pass the username to donation_pg

                startActivity(intent); // Start donation_pg
            }
        });

        // OnClickListener to handle add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the MainActivity
                Intent intent = new Intent(my_donations_pg.this, MainActivity.class); // MainActivity represents activity_main.xml
                intent.putExtra("user_id", userId);  // Pass userId to MainActivity
                // Pass the username to MainActivity
                String userName = userNameTextView.getText().toString();  // Get the username displayed in the TextView
                intent.putExtra("userName", userName);  // Pass the username to MainActivity

                startActivity(intent); // Start MainActivity
            }
        });
    }

    // Method to fetch donations for the logged-in user and display them in the CardViews
    private void displayUserDonations(int userId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        Cursor cursor = dbHelper.getUserDonations(userId); // Fetch donations for the user

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve donation details from the cursor
                final int donationId = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_DONATION_ID));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_NAME));
                String itemCategory = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_CATEGORY));
                String itemDonateTo = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_DONATETO));
                String itemImage = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ITEM_IMAGE));

                // Inflate the cardviewdonation_item layout for each donation
                View donationView = LayoutInflater.from(this).inflate(R.layout.cardview_mydonation_item, donationsLayout, false);

                // Initialize the UI elements in the cardviewdonation_item layout
                ImageView imageView = donationView.findViewById(R.id.uploadImage);
                TextView nameTextView = donationView.findViewById(R.id.itemNameTextView);
                TextView categoryTextView = donationView.findViewById(R.id.itemCategoryTextView);
                TextView donateToTextView = donationView.findViewById(R.id.itemDonateToTextView);
                ImageButton deleteButton = donationView.findViewById(R.id.deleteButton);
                ImageButton editButton = donationView.findViewById(R.id.editButton);

                // Populate data into the UI elements
                nameTextView.setText(itemName);
                categoryTextView.setText(itemCategory);
                donateToTextView.setText(itemDonateTo);

                // Set image if available
                if (itemImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(itemImage);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.default_image); // Default image if none is available
                }

                // Handle delete button click
                deleteButton.setOnClickListener(v -> {
                    // Delete from database
                    boolean isDeleted = dbHelper.deleteDonation(donationId);

                    if (isDeleted) {
                        // Remove the donationView from the layout
                        donationsLayout.removeView(donationView);
                    } else {
                        Toast.makeText(this, "Error deleting donation", Toast.LENGTH_SHORT).show();
                    }
                });

                // Edit button functionality: Pass donation data to MainActivity
                editButton.setOnClickListener(v -> {
                    Intent intent = new Intent(my_donations_pg.this, MainActivity.class);
                    intent.putExtra("donation_id", donationId);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("userName", userNameTextView.getText().toString());
                    startActivity(intent);
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

