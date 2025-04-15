package com.example.adminpanel

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val unit: String = "",
    val type: String = "",
    val mode: String = "", // NEW: organic or inorganic
    val publicId: String = ""
)
