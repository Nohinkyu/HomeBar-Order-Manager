package com.devik.homebarordermanager.data.source.remote

import com.devik.homebarordermanager.data.model.CartMenuItem
import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.util.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val supabase: SupabaseClient,
    private val preferenceManager: PreferenceManager,
    private val moshi: Moshi
) : OrderDataSource {

    private fun toOrderList(json: String): List<CartMenuItem> {
        val listType = Types.newParameterizedType(List::class.java, CartMenuItem::class.java)
        val adapter: JsonAdapter<List<CartMenuItem>> = moshi.adapter(listType)
        return adapter.fromJson(json) ?: emptyList()
    }

    private fun toOrderItemList(orderList: List<Order>): List<OrderItem> {
        val result = mutableListOf<OrderItem>()
        for (item in orderList) {
            result.add(
                OrderItem(
                    id = item.id,
                    createAt = item.createAt,
                    orderUserMail = item.orderUserMail,
                    order = toOrderList(item.order),
                    tableNumber = item.tableNumber
                )
            )
        }
        return result.toList()
    }

    override fun toOrderItem(order: Order): OrderItem {
        return OrderItem(
            id = order.id,
            createAt = order.createAt,
            orderUserMail = order.orderUserMail,
            order = toOrderList(order.order),
            tableNumber = order.tableNumber
        )
    }

    override suspend fun getOrderList(): List<OrderItem> {
        val list = supabase.from(Constants.SUPABASE_DB_TABLE_NAME)
            .select(
                columns = Columns.list(
                    Constants.SUPABASE_DB_COLUMN_ID,
                    Constants.SUPABSE_DB_COLUMN_CREATE_AT,
                    Constants.SUPABSE_DB_COLUMN_ORDER,
                    Constants.SUPABASE_DB_COLUMN_ORDER_USER_MAIL,
                    Constants.SUPABSE_DB_COLUMN_TABLE_NUMBER
                )
            ) {
                filter {
                    eq(
                        Constants.SUPABASE_DB_COLUMN_ORDER_USER_MAIL,
                        preferenceManager.getString(Constants.KEY_MAIL_ADDRESS, "")
                    )
                }
            }.decodeList<Order>()
        return toOrderItemList(list)
    }

    override suspend fun deleteOrder(id: Int) {
        supabase.from(Constants.SUPABASE_DB_TABLE_NAME).delete {
            filter {
                eq(Constants.SUPABASE_DB_COLUMN_ID, id)
            }
        }
    }

    override suspend fun allDeleteOrder(orderList: List<OrderItem>) {
        for (order in orderList) {
            deleteOrder(order.id)
        }
    }
}