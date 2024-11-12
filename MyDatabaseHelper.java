package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database Name & Version
    private static final String DATABASE_NAME = "Regive.db";
    private static final int DATABASE_VERSION = 5;

    // Users Table
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_PASSWORD = "user_password";
    public static final String COLUMN_USER_TYPE = "user_type";

    // Donations Table
    public static final String TABLE_DONATIONS = "Donations";
    public static final String COLUMN_DONATION_ID = "donation_id";
    public static final String COLUMN_DONATION_USER_ID = "user_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_CATEGORY = "item_category";
    public static final String COLUMN_ITEM_DESCRIPTION = "item_description";
    public static final String COLUMN_ITEM_IMAGE = "item_image";
    public static final String COLUMN_ITEM_STATUS = "item_status";
    public static final String COLUMN_ITEM_DONATETO = "item_donateto";
    public static final String COLUMN_DONOR_CONTACT = "donor_contact"; // New column for donor contact


    // Requests Table
    public static final String TABLE_REQUESTS = "Requests";
    public static final String COLUMN_REQUEST_ID = "request_id";
    public static final String COLUMN_REQUEST_DONATION_ID = "donation_id";
    public static final String COLUMN_REQUEST_SENDER_ID = "sender_id";
    public static final String COLUMN_REQUEST_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_REQUEST_REASON = "request_reason";
    public static final String COLUMN_REQUEST_STATUS = "request_status";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_TYPE + " TEXT NOT NULL);";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Donations Table
        String CREATE_DONATIONS_TABLE = "CREATE TABLE " + TABLE_DONATIONS + " (" +
                COLUMN_DONATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DONATION_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                COLUMN_ITEM_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_ITEM_IMAGE + " TEXT, " +
                COLUMN_ITEM_STATUS + " TEXT NOT NULL DEFAULT 'Available', " +
                COLUMN_ITEM_DONATETO + " TEXT, " +
                COLUMN_DONOR_CONTACT + " TEXT, " + // New column added here
                "FOREIGN KEY(" + COLUMN_DONATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";
        db.execSQL(CREATE_DONATIONS_TABLE);

        // Create Requests Table
        String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + " (" +
                COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REQUEST_DONATION_ID + " INTEGER NOT NULL, " +
                COLUMN_REQUEST_SENDER_ID + " INTEGER NOT NULL, " +
                COLUMN_REQUEST_RECEIVER_ID + " INTEGER NOT NULL, " +
                COLUMN_REQUEST_REASON + " TEXT NOT NULL, " +
                COLUMN_REQUEST_STATUS + " TEXT NOT NULL DEFAULT 'Pending', " +
                "FOREIGN KEY(" + COLUMN_REQUEST_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_REQUEST_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_REQUEST_DONATION_ID + ") REFERENCES " + TABLE_DONATIONS + "(" + COLUMN_DONATION_ID + "));";
        db.execSQL(CREATE_REQUESTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) { // Upgrade condition for version 3
            // Drop the old Requests table if it exists
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);

            // Create the new Requests table without the 'request_reason' column
            String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + " (" +
                    COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REQUEST_DONATION_ID + " INTEGER NOT NULL, " +
                    COLUMN_REQUEST_SENDER_ID + " INTEGER NOT NULL, " +
                    COLUMN_REQUEST_RECEIVER_ID + " INTEGER NOT NULL, " +
                    COLUMN_REQUEST_STATUS + " TEXT NOT NULL DEFAULT 'Pending', " +
                    "FOREIGN KEY(" + COLUMN_REQUEST_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_REQUEST_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_REQUEST_DONATION_ID + ") REFERENCES " + TABLE_DONATIONS + "(" + COLUMN_DONATION_ID + "));";
            db.execSQL(CREATE_REQUESTS_TABLE);
        }
    }

    // Method to add a new user to the Users table
    public boolean addUser(String name, String email, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_TYPE, userType);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Method to check user credentials for login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /// Method to add a donation to the Donations table with donor_contact
    public boolean addDonation(int userId, String itemName, String itemCategory, String itemDescription,
                               String itemImage, String itemStatus, String itemDonateto, String donorContact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DONATION_USER_ID, userId);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_CATEGORY, itemCategory);
        values.put(COLUMN_ITEM_DESCRIPTION, itemDescription);
        values.put(COLUMN_ITEM_IMAGE, itemImage);
        values.put(COLUMN_ITEM_STATUS, itemStatus);
        values.put(COLUMN_ITEM_DONATETO, itemDonateto);
        values.put(COLUMN_DONOR_CONTACT, donorContact); // Include donor contact in values

        long result = db.insert(TABLE_DONATIONS, null, values);
        db.close();
        Log.d("DB_INSERT", "Donation insert result: " + result);
        return result != -1;
    }

    // Updated updateDonation method to include donor_contact
    public boolean updateDonation(int donationId, int userId, String itemName, String itemCategory,
                                  String description, String itemImage, String itemStatus,
                                  String itemDonateTo, String donorContact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_CATEGORY, itemCategory);
        values.put(COLUMN_ITEM_DESCRIPTION, description);
        values.put(COLUMN_ITEM_IMAGE, itemImage);
        values.put(COLUMN_ITEM_STATUS, itemStatus);
        values.put(COLUMN_ITEM_DONATETO, itemDonateTo);
        values.put(COLUMN_DONOR_CONTACT, donorContact); // Include donor contact in update values

        int result = db.update(TABLE_DONATIONS, values, COLUMN_DONATION_ID + "=?", new String[]{String.valueOf(donationId)});
        return result > 0;
    }

    // Method to retrieve user_id by email
    public int getUserIdByEmail(String email) {
        int userId = -1; // Default value if user not found
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query to select the user_id based on the email
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        cursor.close();
        db.close();

        return userId;
    }

    // New method to retrieve user_name by user_id
    public String getUserNameById(int userId) {
        String userName = null; // Default value if user not found
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query to select the user_name based on the user_id
        String query = "SELECT " + COLUMN_USER_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
        }
        cursor.close();
        db.close();

        return userName;
    }

    public Cursor getUserDonations(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get donations for the specific user
        String query = "SELECT * FROM " + TABLE_DONATIONS + " WHERE " + COLUMN_DONATION_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        return db.rawQuery(query, selectionArgs);
    }

    public Cursor getDonationById(int donationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DONATIONS, null, COLUMN_DONATION_ID + "=?",
                new String[]{String.valueOf(donationId)}, null, null, null);
    }


    // Method to delete a donation from the database
    public boolean deleteDonation(int donationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_DONATIONS, COLUMN_DONATION_ID + " = ?", new String[]{String.valueOf(donationId)});
        db.close();
        return rowsDeleted > 0; // Return true if deletion was successful
    }

    public Cursor getAllDonations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DONATIONS, null, null, null, null, null, null);
    }

    
    public boolean saveDonationIdOnly(int donationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_DONATION_ID, donationId);

        long result = db.insert(TABLE_REQUESTS, null, values);
        db.close();

        // Check if the insertion was successful
        return result != -1;
    }


}





