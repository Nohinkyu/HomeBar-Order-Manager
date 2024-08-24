package com.devik.homebarordermanager.data.repository

import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem
import com.devik.homebarordermanager.data.source.remote.OrderDataSource
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDataSource: OrderDataSource
) {

    fun toOrderItem(order: Order): OrderItem {
        return orderDataSource.toOrderItem(order)
    }

    suspend fun getOrderList(): List<OrderItem> {
        return orderDataSource.getOrderList()
    }

    suspend fun deleteOrder(id: Int) {
        orderDataSource.deleteOrder(id)
    }

    suspend fun allDeleteOrder(orderList: List<OrderItem>) {
        orderDataSource.allDeleteOrder(orderList)
    }
}