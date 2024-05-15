package com.example.projectapp;



import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button signUpButton;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); // Ensure the layout file is named correctly

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        emailEditText = findViewById(R.id.editTextEmail);
        signUpButton = findViewById(R.id.buttonSignUp);

        dbHandler = new DBHandler(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignup();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void performSignup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        } else {
            if (isValidEmail(email) && isStrongPassword(password)) {
                dbHandler.addUser(username, password, email); // Adjusted to match DatabaseHandler method
                Toast.makeText(this, "Signup successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please ensure your email is valid and your password meets the strength requirements.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8;
    }


}

