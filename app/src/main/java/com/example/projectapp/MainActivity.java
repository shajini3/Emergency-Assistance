package com.example.projectapp;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button LoginButton;
    private Button signUpButton;

    private static final String USERNAME_ADMIN = "admin";
    private static final String PASSWORD_ADMIN = "password"; // Replace with a secure hash

    private DBHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Make sure this matches your layout file name for the main activity

        // Initialize UI components
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextText2);
        LoginButton = findViewById(R.id.button);
        signUpButton = findViewById(R.id.button2);

        // Initialize the database helper
        dbHelper = new DBHandler(this);

        // Setup the login button click listener
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Setup the sign-up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to navigate to the SignUpActivity
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);  // Start the SignupActivity
            }
        });
    }

    private void attemptLogin() {
        String username = getUsernameInput();
        String password = getPasswordInput();

        if (!isValidInput(username, password)) {
            showInvalidInputToast();
            return;
        }

        if (authenticate(username, password)) {
            showLoginSuccessToast();
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        } else {
            showInvalidCredentialsToast();
        }
    }

    private String getUsernameInput() {
        return usernameEditText.getText().toString();
    }

    private String getPasswordInput() {
        return passwordEditText.getText().toString();
    }

    private boolean isValidInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private boolean authenticate(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"username", "password"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);

        int numberOfRows = cursor.getCount();
        cursor.close();

        return numberOfRows > 0;
    }

    private void showInvalidInputToast() {
        Toast.makeText(this, "Please enter a valid username and password", Toast.LENGTH_SHORT).show();
    }

    private void showLoginSuccessToast() {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
    }

    private void showInvalidCredentialsToast() {
        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
    }
}