package com.example.adminpanel

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class UpdateProductActivity : AppCompatActivity() {

    private lateinit var productId: EditText
    private lateinit var fieldToUpdate: Spinner
    private lateinit var newValue: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)

        productId = findViewById(R.id.productIdEditText)
        fieldToUpdate = findViewById(R.id.fieldSpinner)
        newValue = findViewById(R.id.newValueEditText)
        updateButton = findViewById(R.id.updateButton)

        val fields = arrayOf("name", "description", "price", "quantity", "unit", "imageUrl", "type")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fields)
        fieldToUpdate.adapter = adapter

        updateButton.setOnClickListener {
            val id = productId.text.toString()
            val field = fieldToUpdate.selectedItem.toString()
            val value = newValue.text.toString()

            if (id.isNotBlank() && value.isNotBlank()) {
                FirebaseDatabase.getInstance().getReference("products")
                    .child(id)
                    .child(field)
                    .setValue(value)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
