package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RequestPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page);

        // Initialize header buttons
        Button donateButton = findViewById(R.id.donateButton);
        Button donationsButton = findViewById(R.id.donationsButton);
        Button requestsButton = findViewById(R.id.requestsButton);
        Button historyButton = findViewById(R.id.historyButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up click listeners for each button
        donateButton.setOnClickListener(v -> {
            Intent intent = new Intent(RequestPage.this, MainActivity.class);
            startActivity(intent);
        });

       // donationsButton.setOnClickListener(v -> {
          //  Intent intent = new Intent(RequestPage.this, my_donations_pg.class);
         //   startActivity(intent);
      //  });

        requestsButton.setOnClickListener(v -> {
            // Stay on the request page since we're already here
        });

       // historyButton.setOnClickListener(v -> {
        //    Intent intent = new Intent(RequestPage.this, HistoryPage.class);
       //     startActivity(intent);
      //  });

        logoutButton.setOnClickListener(v -> {
            // Handle logout logic here
            Intent intent = new Intent(RequestPage.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize Sent and Received buttons
        Button sentButton = findViewById(R.id.sentButton);
        Button receivedButton = findViewById(R.id.receivedButton);

        // Set up click listeners for sent and received buttons
        sentButton.setOnClickListener(v -> {
            // Logic to display sent requests
            // For example: loadSentRequests();
        });

        receivedButton.setOnClickListener(v -> {
            // Logic to display received requests
            // For example: loadReceivedRequests();
        });
    }
}
