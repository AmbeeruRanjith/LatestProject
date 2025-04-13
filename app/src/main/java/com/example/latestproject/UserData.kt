package com.example.latestproject  // Make sure the package name matches your directory

data class UserData(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var securityQuestion: String = "",
    var securityAnswer: String = ""
)