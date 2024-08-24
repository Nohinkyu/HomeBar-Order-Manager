package com.devik.homebarordermanager.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("id")
    val id: Int,

    @SerialName("create_at")
    val createAt:String,

    @SerialName("order")
    val order: String,

    @SerialName("order_user_mail")
    val orderUserMail: String,

    @SerialName("table_number")
    val tableNumber: String,
)
