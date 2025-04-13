package com.example.latestproject  // Ensure the package name matches your directory

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Patterns

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Existing fields
        val nameEt = findViewById<EditText>(R.id.name)
        val emailEt = findViewById<EditText>(R.id.email)
        val passwordEt = findViewById<EditText>(R.id.password)
        val confirmPasswordEt = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn = findViewById<Button>(R.id.registerButton)

        // New fields for security question and answer
        val securityQuestionSpinner = findViewById<Spinner>(R.id.securityQuestionSpinner)
        val securityAnswerEt = findViewById<EditText>(R.id.securityAnswer)

        // Firebase Database Reference
        database = FirebaseDatabase.getInstance().getReference("users")

        // Populate the spinner with security questions
        val questions = listOf(
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What is your favorite color?"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        securityQuestionSpinner.adapter = adapter

        // Register Button OnClickListener
        registerBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()
            val confirmPassword = confirmPasswordEt.text.toString().trim()
            val selectedQuestion = securityQuestionSpinner.selectedItem.toString()
            val answer = securityAnswerEt.text.toString().trim()

            // Validate fields
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || answer.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password length validation
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password match validation
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email already exists in the database
            val key = email.replace(".", ",") // Using email as unique key
            database.child(key).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // If user already exists, show a toast message
                    Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show()
                } else {
                    // Create user object
                    val user = UserData(name, email, password, selectedQuestion, answer)

                    // Save the user data to Firebase Realtime Database
                    database.child(key).setValue(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            finish() // Close registration screen
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error accessing the database", Toast.LENGTH_SHORT).show()
            }
        }

        // Set OnClickListener for the login TextView
        val loginText = findViewById<TextView>(R.id.loginText)
        loginText.setOnClickListener {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: To finish RegisterActivity after navigating
        }
    }
}
