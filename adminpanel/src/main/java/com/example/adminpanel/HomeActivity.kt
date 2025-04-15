package com.example.adminpanel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var btnUploadProduct: Button
    private lateinit var btnUpdateProduct: Button
    private lateinit var btnDeleteProduct: Button
    private lateinit var btnDeleteUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize buttons
        btnUploadProduct = findViewById(R.id.btnUploadProduct)
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)
        btnDeleteUser = findViewById(R.id.btnDeleteUser)

        // Set click listeners
        btnUploadProduct.setOnClickListener {
            startActivity(Intent(this, UploadProductActivity::class.java))
        }

        btnUpdateProduct.setOnClickListener {
            startActivity(Intent(this, UpdateProductActivity::class.java))
        }

        btnDeleteProduct.setOnClickListener {
            startActivity(Intent(this, DeleteProductActivity::class.java))
        }

        btnDeleteUser.setOnClickListener {
            startActivity(Intent(this, DeleteUserActivity::class.java))
        }
    }
}
