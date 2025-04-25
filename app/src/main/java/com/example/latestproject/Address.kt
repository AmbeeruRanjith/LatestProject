package com.example.demoapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.latestproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Address : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var addAddressButton: Button
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var databaseRef: DatabaseReference
    private val TAG = "AddressActivity"

    data class AddressModel(
        val id: String = "",
        val name: String = "",
        val line1: String = "",
        val line2: String = "",
        val city: String = "",
        val state: String = "",
        val zip: String = "",
        val country: String = "",
        val phone: String = "",
        var Default: Boolean = false
    )

    private val addresses = mutableListOf<AddressModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        recyclerView = findViewById(R.id.addressesRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)
        addAddressButton = findViewById(R.id.addAddressButton)

        // Initialize database reference
        // Use the current user's ID if authenticated, otherwise use a fixed ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user123"

        // Log to confirm path
        Log.d(TAG, "Current user ID: $userId")
        Log.d(TAG, "Database path: addresses/$userId")

        databaseRef = FirebaseDatabase.getInstance().getReference("addresses").child(userId)
        Log.d(TAG, "Full database path: ${databaseRef}")

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter
        addressAdapter = AddressAdapter(
            addresses,
            onEditClick = { address -> editAddress(address) },
            onSetDefaultClick = { address -> setDefaultAddress(address) },
            onDeleteClick = { address -> deleteAddress(address) }
        )

        recyclerView.adapter = addressAdapter

        addAddressButton.setOnClickListener {
            val intent = Intent(this, AddAddress::class.java)
            startActivity(intent)
        }

        // Load addresses when activity is created
        loadAddresses()
    }

    override fun onResume() {
        super.onResume()
        // Reload addresses when returning to this activity
        loadAddresses()
    }

    private fun loadAddresses() {
        Log.d(TAG, "Loading addresses...")

        // Use a ValueEventListener for real-time updates
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Data changed. Has children: ${snapshot.hasChildren()}")
                Log.d(TAG, "Raw snapshot: ${snapshot.getValue()}")

                addresses.clear()

                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (child in snapshot.children) {
                        try {
                            // Log the raw data for debugging
                            Log.d(TAG, "Raw data for child: ${child.key} = ${child.value}")

                            // Try to get each field individually for more detailed debugging
                            val id = child.child("id").getValue(String::class.java) ?: child.key ?: ""
                            val name = child.child("name").getValue(String::class.java) ?: ""
                            val line1 = child.child("line1").getValue(String::class.java) ?: ""
                            val line2 = child.child("line2").getValue(String::class.java) ?: ""
                            val city = child.child("city").getValue(String::class.java) ?: ""
                            val state = child.child("state").getValue(String::class.java) ?: ""
                            val zip = child.child("zip").getValue(String::class.java) ?: ""
                            val country = child.child("country").getValue(String::class.java) ?: ""
                            val phone = child.child("phone").getValue(String::class.java) ?: ""
                            val Default = child.child("Default").getValue(Boolean::class.java) ?: false

                            // Create the object manually
                            val address = AddressModel(
                                id = id,
                                name = name,
                                line1 = line1,
                                line2 = line2,
                                city = city,
                                state = state,
                                zip = zip,
                                country = country,
                                phone = phone,
                                Default = Default
                            )

                            Log.d(TAG, "Constructed address: ${address.name}, ID: ${address.id}")
                            addresses.add(address)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing address: ${e.message}", e)
                        }
                    }
                    Log.d(TAG, "Loaded ${addresses.size} addresses")
                } else {
                    Log.d(TAG, "No addresses found in database")
                }

                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                Toast.makeText(baseContext, "Failed to load addresses: ${error.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setDefaultAddress(address: AddressModel) {
        // First set all addresses to non-default
        for (addr in addresses) {
            val isNowDefault = addr.id == address.id
            Log.d(TAG, "Setting address ${addr.id} default status to $isNowDefault")
            databaseRef.child(addr.id).child("Default").setValue(isNowDefault)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated default status for ${addr.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update default status: ${e.message}")
                }
        }

        Toast.makeText(this, "${address.name} is now your default address", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAddress(address: AddressModel) {
        databaseRef.child(address.id).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show()
            // The ValueEventListener will trigger loadAddresses() automatically
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to delete address: ${e.message}")
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editAddress(address: AddressModel) {
        val intent = Intent(this, AddAddress::class.java).apply {
            putExtra("ADDRESS_ID", address.id)
            putExtra("ADDRESS_NAME", address.name)
            putExtra("ADDRESS_LINE1", address.line1)
            putExtra("ADDRESS_LINE2", address.line2)
            putExtra("ADDRESS_CITY", address.city)
            putExtra("ADDRESS_STATE", address.state)
            putExtra("ADDRESS_ZIP", address.zip)
            putExtra("ADDRESS_COUNTRY", address.country)
            putExtra("ADDRESS_PHONE", address.phone)
            putExtra("ADDRESS_IS_DEFAULT", address.Default)
        }
        startActivity(intent)
    }

    private fun updateUI() {
        Log.d(TAG, "Updating UI with ${addresses.size} addresses")

        // Debug the actual addresses content
        addresses.forEachIndexed { index, address ->
            Log.d(TAG, "Address $index: ${address.name}, Default: ${address.Default}")
        }

        if (addresses.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
            Log.d(TAG, "Showing empty state")
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateText.visibility = View.GONE
            addressAdapter.notifyDataSetChanged()
            Log.d(TAG, "Should be showing ${addresses.size} addresses in RecyclerView")
        }
    }

    inner class AddressAdapter(
        private val addresses: List<AddressModel>,
        private val onEditClick: (AddressModel) -> Unit,
        private val onSetDefaultClick: (AddressModel) -> Unit,
        private val onDeleteClick: (AddressModel) -> Unit
    ) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.addressName)
            val line1TextView: TextView = view.findViewById(R.id.addressLine1)
            val line2TextView: TextView = view.findViewById(R.id.addressLine2)
            val cityStateTextView: TextView = view.findViewById(R.id.addressCityState)
            val zipCountryTextView: TextView = view.findViewById(R.id.addressZipCountry)
            val phoneTextView: TextView = view.findViewById(R.id.addressPhone)
            val setDefaultButton: Button = view.findViewById(R.id.setDefaultButton)
            val editButton: Button = view.findViewById(R.id.editAddressButton)
            val deleteButton: ImageButton = view.findViewById(R.id.deleteAddressButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val address = addresses[position]
            Log.d(TAG, "Binding address at position $position: ${address.name}")

            holder.nameTextView.text = if (address.Default) "${address.name} (Default)" else address.name
            holder.line1TextView.text = address.line1
            holder.line2TextView.apply {
                text = address.line2
                visibility = if (address.line2.isNotEmpty()) View.VISIBLE else View.GONE
            }
            holder.cityStateTextView.text = "${address.city}, ${address.state}"
            holder.zipCountryTextView.text = "${address.zip}, ${address.country}"
            holder.phoneTextView.apply {
                text = address.phone
                visibility = if (address.phone.isNotEmpty()) View.VISIBLE else View.GONE
            }

            holder.setDefaultButton.apply {
                text = if (address.Default) "Default" else "Set as Default"
                isEnabled = !address.Default
                setOnClickListener { onSetDefaultClick(address) }
            }

            holder.editButton.setOnClickListener {
                onEditClick(address)
            }

            holder.deleteButton.setOnClickListener {
                onDeleteClick(address)
            }
        }

        override fun getItemCount(): Int {
            Log.d(TAG, "getItemCount(): ${addresses.size}")
            return addresses.size
        }
    }
}