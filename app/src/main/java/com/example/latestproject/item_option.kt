package com.example.demoapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class item_option : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.item_option)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val optionIds = listOf(
////            R.id.option_cart,
//            R.id.option_wishlist,
//            R.id.option_orders,
//            R.id.option_settings,
//            R.id.option_addresses,
//            R.id.option_edit_profile
//        )
//
//        val optionTitles = listOf(
////            "Cart",
//            "Wishlist",
//            "Orders",
//            "Account Settings",
//            "Addresses",
//            "Edit Profile"
//        )
//
//        val optionIcons = listOf(
////            R.drawable.ic_cart,         // Replace with your cart icon
//            R.drawable.ic_wishlist,     // Replace with your wishlist icon
//            R.drawable.ic_orders,       // Replace with your orders icon
//            R.drawable.ic_settings,     // Replace with your settings icon
//            R.drawable.ic_location,     // Replace with your location icon
//            R.drawable.ic_edit_profile  // Replace with your edit icon
//        )
//
//        for (i in optionIds.indices) {
//            val itemView = findViewById<View>(optionIds[i])
//            val iconView = itemView.findViewById<ImageView>(R.id.optionIcon)
//            val titleView = itemView.findViewById<TextView>(R.id.optionTitle)
//
//            iconView.setImageResource(optionIcons[i])
//            titleView.text = optionTitles[i]
//        }

    }
}