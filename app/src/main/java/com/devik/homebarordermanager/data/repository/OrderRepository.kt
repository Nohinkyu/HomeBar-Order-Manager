package com.devik.homebarordermanager.data.repository

import com.devik.homebarordermanager.data.model.CartMenuItem
import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val moshi: Moshi,
) {
    private fun toOrderList(json: String): List<CartMenuItem> {
        val listType = Types.newParameterizedType(List::class.java, CartMenuItem::class.java)
        val adapter: JsonAdapter<List<CartMenuItem>> = moshi.adapter(listType)
        return adapter.fromJson(json) ?: emptyList()
    }

    fun toOrderItemList(orderList: List<Order>): List<OrderItem> {
        val result = mutableListOf<OrderItem>()
        for (item in orderList) {
            result.add(OrderItem(
                id = item.id,
                createAt = item.createAt,
                orderUserMail = item.orderUserMail,
                order = toOrderList(item.order),
            ))
        }
        return result.toList()
    }
}