package com.example.adminpanel

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class DeleteProductActivity : AppCompatActivity() {

    private lateinit var productIdEditText: EditText
    private lateinit var deleteButton: Button
    private val database = FirebaseDatabase.getInstance().getReference("products")

    // Cloudinary credentials
    private val cloudName = "dybfgpc5i"
    private val apiKey = "547753211681526"
    private val apiSecret = "sN3-xBysY9o2QMZ0XPAuBFaOXt4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_product)

        productIdEditText = findViewById(R.id.productIdEditText)
        deleteButton = findViewById(R.id.deleteButton)

        deleteButton.setOnClickListener {
            val productId = productIdEditText.text.toString()
            if (productId.isNotBlank()) {
                deleteProductWithImage(productId)
            } else {
                Toast.makeText(this, "Please enter Product ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProductWithImage(productId: String) {
        database.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val publicId = snapshot.child("publicId").value as? String

                if (publicId != null && publicId.isNotBlank()) {
                    deleteImageFromCloudinary(publicId)
                }

                database.child(productId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this@DeleteProductActivity, "Product deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@DeleteProductActivity, "Deletion failed", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteProductActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteImageFromCloudinary(publicId: String) {
        val timestamp = System.currentTimeMillis() / 1000
        val signature = generateSignature(publicId, timestamp)

        Log.d("CloudinaryDebug", "Deleting image with publicId=$publicId")
        Log.d("CloudinaryDebug", "Timestamp=$timestamp")
        Log.d("CloudinaryDebug", "Signature=$signature")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val api = retrofit.create(CloudinaryApi::class.java)
        val call = api.deleteImage(publicId, apiKey, timestamp, signature)

        call.enqueue(object : Callback<CloudinaryResponse> {
            override fun onResponse(call: Call<CloudinaryResponse>, response: Response<CloudinaryResponse>) {
                Log.d("CloudinaryDebug", "Cloudinary response code: ${response.code()}")
                Log.d("CloudinaryDebug", "Cloudinary response body: ${response.body()}")
                Log.d("CloudinaryDebug", "Cloudinary error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.result == "ok") {
                    Toast.makeText(this@DeleteProductActivity, "Image deleted from Cloudinary", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DeleteProductActivity, "Failed to delete image (Cloudinary)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CloudinaryResponse>, t: Throwable) {
                Log.d("CloudinaryDebug", "Cloudinary API call failed: ${t.message}")
                Toast.makeText(this@DeleteProductActivity, "Cloudinary error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun generateSignature(publicId: String, timestamp: Long): String {
        val params = "public_id=$publicId&timestamp=$timestamp"
        val toSign = "$params$apiSecret"

        val mac = Mac.getInstance("HmacSHA1")
        val keySpec = SecretKeySpec(apiSecret.toByteArray(Charsets.UTF_8), "HmacSHA1")
        mac.init(keySpec)
        val result = mac.doFinal(toSign.toByteArray(Charsets.UTF_8))
        return result.joinToString("") { "%02x".format(it) }
    }
}
