package com.devik.homebarordermanager.ui.screen.order

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devik.homebarordermanager.data.model.Order
import com.devik.homebarordermanager.data.model.OrderItem
import com.devik.homebarordermanager.data.repository.OrderRepository
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.util.Constants
import com.devik.homebarordermanager.util.DateFormatUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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

    private val _networkErrorState = MutableStateFlow<Boolean>(false)
    val networkErrorState: StateFlow<Boolean> = _networkErrorState

    private val _allOrderDeleteInProgressState = MutableStateFlow<Boolean>(false)
    val allOrderDeleteInProgressState: StateFlow<Boolean> = _allOrderDeleteInProgressState

    private val _allOrderDeleteSuccess = MutableStateFlow<Boolean>(false)
    val allOrderDeleteSuccess: StateFlow<Boolean> = _allOrderDeleteSuccess

    private val _allOrderDeleteCheckDialogState = MutableStateFlow<Boolean>(false)
    val allOrderDeleteCheckDialogState: StateFlow<Boolean> = _allOrderDeleteCheckDialogState

    private val _orderDeleteCheckDialogState = MutableStateFlow<Boolean>(false)
    val orderDeleteCheckDialogState: StateFlow<Boolean> = _orderDeleteCheckDialogState

    private val _deleteTargetOrderId = MutableStateFlow<Int>(0)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channel = supabase.channel(Constants.SUPABASE_CHANNEL_ID) {

                }
                val changeFlow =
                    channel.postgresChangeFlow<PostgresAction>(schema = Constants.SUPABASE_CHANNEL_SCHEMA) {
                        table = Constants.SUPABASE_DB_TABLE_NAME
                        filter(
                            Constants.SUPABASE_DB_COLUMN_ORDER_USER_MAIL,
                            FilterOperator.EQ,
                            preferenceManager.getString(Constants.KEY_MAIL_ADDRESS, "")
                        )
                    }
                channel.subscribe()
                changeFlow.collect {
                    when (it) {
                        is PostgresAction.Insert -> {
                            val order: Order = Json.decodeFromString(it.record.toString())
                            _orderList.value = _orderList.value + orderRepository.toOrderItem(order)
                        }

                        is PostgresAction.Delete -> {
                            val deleteOrder =
                                it.oldRecord[Constants.SUPABASE_DB_COLUMN_ID].toString().toInt()
                            _orderList.value = _orderList.value.filter { it.id != deleteOrder }
                        }

                        else -> {

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                _networkErrorState.value = true
            }
        }
        viewModelScope.launch {
            _orderList.collect { order ->
                val totalRevenue =
                    order.sumOf { it.order.sumOf { menu -> menu.menuPrice * menu.menuCount } }
                _revenueState.value = totalRevenue
            }
        }
    }

    fun getOrderList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _orderList.value =
                    orderRepository.getOrderList().sortedBy { parseDateString(it.createAt) }
            } catch (e: java.lang.Exception) {
                _networkErrorState.value = true
            }
        }
    }

    private fun parseDateString(dateString: String): Date? {
        return SimpleDateFormat(
            DateFormatUtil.DATE_YEAR_MONTH_DAY_TIME_PATTERN,
            Locale.getDefault()
        ).parse(dateString)
    }

    fun deleteOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                orderRepository.deleteOrder(_deleteTargetOrderId.value)
            } catch (e: java.lang.Exception) {
                _networkErrorState.value = true
            }
            _deleteTargetOrderId.value = 0
            _orderDeleteCheckDialogState.value = false
        }
    }

    fun allDeleteOrder() {
        if (_orderList.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                _allOrderDeleteInProgressState.value = true
                try {
                    async { orderRepository.allDeleteOrder(_orderList.value) }.await()
                    _allOrderDeleteInProgressState.value = false
                    _allOrderDeleteSuccess.value = true
                } catch (e: java.lang.Exception) {
                    _networkErrorState.value = true
                }
            }
        }
    }

    fun closeNetworkErrorDialog() {
        _networkErrorState.value = false
    }

    fun closeAllOrderDeleteSuccessDialog() {
        _allOrderDeleteSuccess.value = false
    }

    fun openAllOrderDeleteCheckDialog() {
        if (_orderList.value.isNotEmpty()) {
            _allOrderDeleteCheckDialogState.value = true
        }
    }

    fun closeAllOrderDeleteCheckDialog() {
        _allOrderDeleteCheckDialogState.value = false
    }

    fun openOrderDeleteDialog(deleteId: Int) {
        _deleteTargetOrderId.value = deleteId
        _orderDeleteCheckDialogState.value = true
    }

    fun closeOrderDeleteDialog() {
        _orderDeleteCheckDialogState.value = false
        _deleteTargetOrderId.value = 0
    }

    fun serviceComplete(id: Int) {
        _orderList.value = _orderList.value.map { order ->
            if (order.id == id) {
                order.copy(serviceComplete = true)
            } else {
                order
            }
        }
    }
}