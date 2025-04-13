package com.example.latestproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEt = findViewById<EditText>(R.id.email)
        val securityQuestionEt = findViewById<EditText>(R.id.securityQuestionAnswer)
        val newPasswordEt = findViewById<EditText>(R.id.newPassword)
        val resetPasswordBtn = findViewById<Button>(R.id.resetPasswordButton)

        database = FirebaseDatabase.getInstance().getReference("users")

        resetPasswordBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val answer = securityQuestionEt.text.toString().trim()
            val newPassword = newPasswordEt.text.toString().trim()

            // Validate fields
            if (email.isEmpty() || answer.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Replace dots in email to comply with Firebase keys
            val key = email.replace(".", ",")

            // Query the database for the user
            database.child(key).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserData::class.java)
                    if (user?.securityAnswer == answer) {
                        // Update password securely (hash it before storing in a real app)
                        val updatedUser = user.copy(password = newPassword)

                        // Save the updated user data back to the database
                        database.child(key).setValue(updatedUser).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                            } else {
                                Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Incorrect answer to the security question", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No such user found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error accessing the database", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
