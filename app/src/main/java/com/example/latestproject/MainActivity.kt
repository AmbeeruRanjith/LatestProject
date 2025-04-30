import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.latestproject.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var wishlistCountTextView: TextView
    private lateinit var ordersCountTextView: TextView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var sharedPref: SharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences and get user ID
        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        userId = sharedPref.getString("user_id", "") ?: ""

        // Check if user is logged in
        if (userId.isEmpty()) {
            // User is not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize views
        userNameTextView = findViewById(R.id.userName)
        userEmailTextView = findViewById(R.id.userEmail)
        wishlistCountTextView = findViewById(R.id.wishlistCount)
        ordersCountTextView = findViewById(R.id.ordersCount)
        profileImage = findViewById(R.id.profileImage)

        val contactUsButton = findViewById<LinearLayout>(R.id.contactUsButton)
        val faqButton = findViewById<LinearLayout>(R.id.faqButton)
        val signOutButton = findViewById<MaterialButton>(R.id.signOutButton)
        val editProfileButton = findViewById<MaterialButton>(R.id.editProfileButton)

        // Set profile image
        profileImage.setImageResource(R.drawable.ic_profile_placeholder)

        // Initialize Firebase reference with the user ID from preferences
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("users").child(userId)

        // Load user data
        loadUserData()

        // Load stats
        loadUserStats()

        // Set up option items
        setupOptionItems()

        // Set up buttons
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        contactUsButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("mrajayvasan@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Agribazzar issue!")
                putExtra(Intent.EXTRA_TEXT, "Hello I have an issue in your app ")
            }

            val chooser = Intent.createChooser(emailIntent, "Send Email Using")

            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        faqButton.setOnClickListener {
            // Handle FAQs
            // val intent = Intent(this, FAQActivity::class.java)
            // startActivity(intent)
        }

        signOutButton.setOnClickListener {
            // Clear user session data
            sharedPref.edit().clear().apply()

            // Redirect to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserData() {
        // First try to load from SharedPreferences for quick display
        val prefName = sharedPref.getString("user_name", null)
        val prefEmail = sharedPref.getString("user_email", null)

        if (!prefName.isNullOrEmpty()) {
            userNameTextView.text = prefName
        }

        if (!prefEmail.isNullOrEmpty()) {
            userEmailTextView.text = prefEmail
        }

        // Then load from Firebase to get the most up-to-date data
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: prefName ?: "User"
                val email = snapshot.child("email").getValue(String::class.java)?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: prefEmail ?: "user@example.com"

                userNameTextView.text = name
                userEmailTextView.text = email

                // Update SharedPreferences with the latest data
                with(sharedPref.edit()) {
                    putString("user_name", name)
                    putString("user_email", email)
                    apply()
                }

                // Load profile image if available
                // val profileImageUrl = snapshot.child("profileImage").getValue(String::class.java)
                // if (!profileImageUrl.isNullOrEmpty()) {
                //     Glide.with(this@MainActivity)
                //         .load(profileImageUrl)
                //         .placeholder(R.drawable.ic_profile_placeholder)
                //         .error(R.drawable.ic_profile_placeholder)
                //         .circleCrop()
                //         .into(profileImage)
                // }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUserStats() {
        // Load wishlist count
        database.getReference("wishlists").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount.toInt()
                    wishlistCountTextView.text = count.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    wishlistCountTextView.text = "0"
                }
            })

        // Load orders count
        database.getReference("orders").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount.toInt()
                    ordersCountTextView.text = count.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    ordersCountTextView.text = "0"
                }
            })
    }

    // Rest of your code remains the same
    private fun setupOptionItems() {
        // Your existing implementation
    }
}