package com.example.demoapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNotificationSettings()
        setupPrivacySettings()
        setupApplicationSettings()
    }

    private fun setupNotificationSettings() {
        val emailSwitch = findViewById<SwitchCompat>(R.id.emailNotificationsSwitch)
        val pushSwitch = findViewById<SwitchCompat>(R.id.pushNotificationsSwitch)
        val smsSwitch = findViewById<SwitchCompat>(R.id.smsNotificationsSwitch)

        // Load saved preferences
        val sharedPrefs = getSharedPreferences("notification_settings", MODE_PRIVATE)
        emailSwitch.isChecked = sharedPrefs.getBoolean("email_notifications", true)
        pushSwitch.isChecked = sharedPrefs.getBoolean("push_notifications", true)
        smsSwitch.isChecked = sharedPrefs.getBoolean("sms_notifications", false)

        emailSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save setting to SharedPreferences
            sharedPrefs.edit().putBoolean("email_notifications", isChecked).apply()
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Email notifications $status", Toast.LENGTH_SHORT).show()
        }

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save setting to SharedPreferences
            sharedPrefs.edit().putBoolean("push_notifications", isChecked).apply()
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Push notifications $status", Toast.LENGTH_SHORT).show()
        }

        smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save setting to SharedPreferences
            sharedPrefs.edit().putBoolean("sms_notifications", isChecked).apply()
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "SMS notifications $status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPrivacySettings() {
        val changePasswordLayout = findViewById<LinearLayout>(R.id.changePasswordLayout)
        val privacyPolicyLayout = findViewById<LinearLayout>(R.id.privacyPolicyLayout)

        changePasswordLayout.setOnClickListener {
            // Keep original toast message as requested
            Toast.makeText(this, "Change password feature coming soon", Toast.LENGTH_SHORT).show()
        }

        privacyPolicyLayout.setOnClickListener {
            // Show privacy policy dialog or activity
            showPrivacyPolicy()
        }
    }

    private fun showPrivacyPolicy() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage("This app respects your privacy. We do not collect or share personal data without your consent.")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun setupApplicationSettings() {
        val darkModeSwitch = findViewById<SwitchCompat>(R.id.darkModeSwitch)
        val clearCacheLayout = findViewById<LinearLayout>(R.id.clearCacheLayout)
//        val logoutLayout = findViewById<LinearLayout>(R.id.logoutLayout)

        // Load saved dark mode preference
        val sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        darkModeSwitch.isChecked = sharedPrefs.getBoolean("dark_mode", false)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save setting to SharedPreferences
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()

            // Implement dark mode toggle
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val mode = if (isChecked) "enabled" else "disabled"
            Toast.makeText(this, "Dark mode $mode", Toast.LENGTH_SHORT).show()
        }

        clearCacheLayout.setOnClickListener {
            // Clear app cache
            clearAppCache()
            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
        }

//        logoutLayout.setOnClickListener {
//            // Show confirmation dialog before logout
//            showLogoutConfirmation()
//        }
    }

    private fun clearAppCache() {
        try {
            cacheDir.deleteRecursively()
            externalCacheDir?.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLogoutConfirmation() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                // Clear user data from SharedPreferences
                getSharedPreferences("user_data", MODE_PRIVATE).edit().clear().apply()

                Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show()

                // In a real app, you would redirect to login screen
                // val intent = Intent(this, LoginActivity::class.java)
                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // startActivity(intent)

                // For now, just finish this activity
                finish()
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}