package com.devik.homebarordermanager.data.model

data class OrderItem(
    val id: Int,
    val createAt: String,
    val order: List<CartMenuItem>,
    val orderUserMail: String,
    val tableNumber: String,
    var serviceComplete: Boolean = false
)
