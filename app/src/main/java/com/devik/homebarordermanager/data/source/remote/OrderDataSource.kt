package com.devik.homebarordermanager.data.source.remote

import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem

interface OrderDataSource {

    suspend fun getOrderList(): List<OrderItem>
    suspend fun deleteOrder(id: Int)
    suspend fun allDeleteOrder(orderList: List<OrderItem>)
    fun toOrderItem(order: Order): OrderItem
}