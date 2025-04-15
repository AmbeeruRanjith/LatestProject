package com.example.adminpanel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UploadProductActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectedImageUri: Uri
    private var imageSelected = false

    private lateinit var productName: EditText
    private lateinit var productDesc: EditText
    private lateinit var productPrice: EditText
    private lateinit var productQty: EditText
    private lateinit var unitSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var natureSpinner: Spinner // NEW

    private lateinit var selectBtn: Button
    private lateinit var uploadBtn: Button

    private lateinit var progressBar: ProgressBar
    private lateinit var percentageText: TextView

    private val PICK_IMAGE_REQUEST = 1
    private val cloudName = "dybfgpc5i"
    private val uploadPreset = "LatestProject"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_product)

        imageView = findViewById(R.id.imageView)
        productName = findViewById(R.id.productName)
        productDesc = findViewById(R.id.productDesc)
        productPrice = findViewById(R.id.productPrice)
        productQty = findViewById(R.id.productQty)
        unitSpinner = findViewById(R.id.unitSpinner)
        typeSpinner = findViewById(R.id.typeSpinner)
        natureSpinner = findViewById(R.id.natureSpinner) // NEW

        selectBtn = findViewById(R.id.selectImageButton)
        uploadBtn = findViewById(R.id.uploadButton)
        progressBar = findViewById(R.id.uploadProgressBar)
        percentageText = findViewById(R.id.percentageText)

        setupUnitSpinner()
        setupTypeSpinner()
        setupNatureSpinner() // NEW
        setupValidation()

        selectBtn.setOnClickListener { openGallery() }

        uploadBtn.setOnClickListener {
            if (validateInputs()) {
                if (imageSelected) {
                    uploadImageToCloudinary()
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUnitSpinner() {
        val units = arrayOf("kg", "g", "quintal", "tonne", "litre", "ml", "dozen", "piece", "pack", "sack", "bushel")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        unitSpinner.adapter = adapter
    }

    private fun setupTypeSpinner() {
        val types = arrayOf(
            "Vegetables", "Fruits", "Cereals", "Pulses", "Spices", "Rice", "Wheat", "Maize",
            "Sugarcane", "Cotton", "Tea", "Coffee", "Jute", "Oilseeds", "Honey", "Flowers",
            "Millets", "Dry Fruits", "Tubers", "Leafy Greens"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        typeSpinner.adapter = adapter
    }

    private fun setupNatureSpinner() {
        val natureOptions = arrayOf("Organic", "Inorganic")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, natureOptions)
        natureSpinner.adapter = adapter
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        productName.addTextChangedListener(watcher)
        productDesc.addTextChangedListener(watcher)
        productPrice.addTextChangedListener(watcher)
        productQty.addTextChangedListener(watcher)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (productName.text.isBlank()) {
            productName.error = "Enter product name"
            isValid = false
        }

        if (productDesc.text.isBlank()) {
            productDesc.error = "Enter product description"
            isValid = false
        }

        val price = productPrice.text.toString().toDoubleOrNull()
        if (price == null || price <= 0) {
            productPrice.error = "Enter valid price"
            isValid = false
        }

        val qty = productQty.text.toString().toIntOrNull()
        if (qty == null || qty <= 0) {
            productQty.error = "Enter valid quantity"
            isValid = false
        }

        return isValid
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            imageSelected = true
            Glide.with(this).load(selectedImageUri).into(imageView)
        }
    }

    private fun uploadImageToCloudinary() {
        progressBar.visibility = View.VISIBLE
        percentageText.visibility = View.VISIBLE
        percentageText.text = "0%"

        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
        val inputStream = contentResolver.openInputStream(selectedImageUri)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val requestBody = ProgressRequestBody(tempFile, "image/*", object : ProgressRequestBody.UploadCallback {
            override fun onProgressUpdate(percentage: Int) {
                runOnUiThread {
                    progressBar.progress = percentage
                    percentageText.text = "$percentage%"
                }
            }
        })

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "product.jpg", requestBody)
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(multipart)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UploadProductActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    percentageText.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) {
                    runOnUiThread {
                        Toast.makeText(this@UploadProductActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        percentageText.visibility = View.GONE
                    }
                    return
                }

                val json = JSONObject(responseBody)
                val imageUrl = json.getString("secure_url")
                val publicId = json.getString("public_id")
                saveProductToFirebase(imageUrl, publicId)
            }
        })
    }

    private fun saveProductToFirebase(imageUrl: String, publicId: String) {
        val name = productName.text.toString()
        val desc = productDesc.text.toString()
        val price = productPrice.text.toString().toDouble()
        val qty = productQty.text.toString().toInt()
        val unit = unitSpinner.selectedItem.toString()
        val type = typeSpinner.selectedItem.toString()
        val nature = natureSpinner.selectedItem.toString() // NEW
        val id = UUID.randomUUID().toString()

        val product = Product(id, name, desc, imageUrl, price, qty, unit, type, nature, publicId)

        FirebaseDatabase.getInstance().getReference("products").child(id).setValue(product)
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "Product uploaded", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    percentageText.visibility = View.GONE
                    finish()
                }
            }
            .addOnFailureListener {
                runOnUiThread {
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    percentageText.visibility = View.GONE
                }
            }
    }
}
