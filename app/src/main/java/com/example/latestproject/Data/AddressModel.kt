package com.example.demoapplication

data class AddressModel(
    val id: String = "",
    val name: String = "",
    val line1: String = "",
    val line2: String? = null,
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "",
    val phone: String? = null,
    var isDefault: Boolean = false
)
