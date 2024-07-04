package com.devik.homebarordermanager.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem
import com.devik.homebarordermanager.data.repository.OrderRepository
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(SupabaseExperimental::class)
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val preferenceManager: PreferenceManager,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderList = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderList: StateFlow<List<OrderItem>> = _orderList

    private val _revenueState = MutableStateFlow<Int>(0)
    val revenueState: StateFlow<Int> = _revenueState

    init {
        viewModelScope.launch {
            getOrderList()
            _orderList.collect { order ->
                val totalRevenue =
                    order.sumOf { it.order.sumOf { menu -> menu.menuPrice * menu.menuCount } }
                _revenueState.value = totalRevenue
            }
        }
    }

    private fun getOrderList() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow: Flow<List<Order>> =
                supabase.from(Constants.SUPABASE_DB_TABLE_NAME).selectAsFlow(
                    Order::orderUserMail,
                    filter = FilterOperation(
                        Constants.SUPABASE_DB_COLUMN_ORDER_USER_MAIL,
                        FilterOperator.EQ,
                        preferenceManager.getString(Constants.KEY_MAIL_ADDRESS, "")
                    )
                )
            flow.collect {
                _orderList.value = _orderList.value + orderRepository.toOrderItemList(it)
            }
        }
    }
}