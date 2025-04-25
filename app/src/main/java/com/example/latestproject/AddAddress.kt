package com.example.demoapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.latestproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddAddress : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var line1EditText: EditText
    private lateinit var line2EditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var zipEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var defaultCheckBox: CheckBox
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private val TAG = "AddAddressActivity"

    private var addressId: String? = null
    private var isEditing = false
    private var isDefault = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText)
        line1EditText = findViewById(R.id.line1EditText)
        line2EditText = findViewById(R.id.line2EditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        zipEditText = findViewById(R.id.zipEditText)
        countryEditText = findViewById(R.id.countryEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        defaultCheckBox = findViewById(R.id.defaultCheckBox)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Check if we're editing an existing address
        addressId = intent.getStringExtra("ADDRESS_ID")
        isEditing = addressId != null

        if (isEditing) {
            // Populate fields with existing address data
            nameEditText.setText(intent.getStringExtra("ADDRESS_NAME"))
            line1EditText.setText(intent.getStringExtra("ADDRESS_LINE1"))
            line2EditText.setText(intent.getStringExtra("ADDRESS_LINE2"))
            cityEditText.setText(intent.getStringExtra("ADDRESS_CITY"))
            stateEditText.setText(intent.getStringExtra("ADDRESS_STATE"))
            zipEditText.setText(intent.getStringExtra("ADDRESS_ZIP"))
            countryEditText.setText(intent.getStringExtra("ADDRESS_COUNTRY"))
            phoneEditText.setText(intent.getStringExtra("ADDRESS_PHONE"))
            isDefault = intent.getBooleanExtra("ADDRESS_IS_DEFAULT", false)
            defaultCheckBox.isChecked = isDefault

            // Update title
            setTitle("Edit Address")
            saveButton.text = "Update Address"
        } else {
            setTitle("Add New Address")
            saveButton.text = "Save Address"
        }

        // Set up button click listeners
        saveButton.setOnClickListener {
            saveAddress()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveAddress() {
        // Validate fields
        val name = nameEditText.text.toString().trim()
        val line1 = line1EditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val state = stateEditText.text.toString().trim()
        val zip = zipEditText.text.toString().trim()
        val country = countryEditText.text.toString().trim()

        if (name.isEmpty() || line1.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the database
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user123"
        val databaseRef = FirebaseDatabase.getInstance().getReference("addresses").child(userId)

        // Create address object
        val newAddressId = addressId ?: databaseRef.push().key ?: return
        val isDefaultAddress = defaultCheckBox.isChecked

        val address = mapOf(
            "id" to newAddressId,
            "name" to name,
            "line1" to line1,
            "line2" to line2EditText.text.toString().trim(),
            "city" to city,
            "state" to state,
            "zip" to zip,
            "country" to country,
            "phone" to phoneEditText.text.toString().trim(),
            "Default" to isDefaultAddress
        )

        Log.d(TAG, "Saving address: $address")

        // If this is the default address, we need to update other addresses
        if (isDefaultAddress) {
            // First, set all addresses to non-default
            databaseRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        if (child.key != newAddressId) {
                            Log.d(TAG, "Setting address ${child.key} to non-default")
                            databaseRef.child(child.key!!).child("Default").setValue(false)
                        }
                    }
                }
            }
        }

        // Save the address
        databaseRef.child(newAddressId).setValue(address)
            .addOnSuccessListener {
                Log.d(TAG, "Address saved successfully")
                Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving address: ${e.message}")
                Toast.makeText(this, "Failed to save address: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}