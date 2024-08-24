package com.devik.homebarordermanager.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.devik.homebarordermanager.R
import com.devik.homebarordermanager.data.model.CartMenuItem
import com.devik.homebarordermanager.data.model.OrderItem
import com.devik.homebarordermanager.ui.component.dialog.DeleteInProgressDialog
import com.devik.homebarordermanager.ui.component.dialog.ResultDialog
import com.devik.homebarordermanager.ui.component.dialog.YesOrNoDialog
import com.devik.homebarordermanager.ui.component.navigation.NavigationRoute
import com.devik.homebarordermanager.ui.component.topappbar.SettingWithTitleAppBar
import com.devik.homebarordermanager.ui.theme.LightBlue
import com.devik.homebarordermanager.ui.theme.LightGray
import com.devik.homebarordermanager.ui.theme.LightPurple
import com.devik.homebarordermanager.ui.theme.MintGreen
import com.devik.homebarordermanager.ui.theme.OrangeSoda
import com.devik.homebarordermanager.util.DateFormatUtil
import com.devik.homebarordermanager.util.TextFormatUtil

@Composable
fun OrderScreen(navController: NavController) {

    CompositionLocalProvider(
        androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current,
    ) {

        val viewModel: OrderViewModel = hiltViewModel()
        val orderList by viewModel.orderList.collectAsStateWithLifecycle()
        val revenueState by viewModel.revenueState.collectAsStateWithLifecycle()
        val networkErrorState by viewModel.networkErrorState.collectAsStateWithLifecycle()
        val allOrderDeleteInProgressState by viewModel.allOrderDeleteInProgressState.collectAsStateWithLifecycle()
        val allOrderDeleteCheckDialogState by viewModel.allOrderDeleteCheckDialogState.collectAsStateWithLifecycle()
        val allOrderDeleteSuccess by viewModel.allOrderDeleteSuccess.collectAsStateWithLifecycle()
        val gridState = rememberLazyGridState()
        val itemColors = listOf(LightBlue, MintGreen, LightPurple)

        LaunchedEffect(Unit) {
            viewModel.getOrderList()
        }
        LaunchedEffect(orderList.size) {
            if (orderList.isNotEmpty()) {
                gridState.animateScrollToItem(orderList.size - 1)
            }
        }
        if (networkErrorState) {
            ResultDialog(
                onDismissRequest = { viewModel.closeNetworkErrorDialog() },
                resultMessageTitle = stringResource(R.string.network_error_dialog_title),
                resultMessageBody = stringResource(R.string.network_error_dialog_body).trimMargin()
            )
        }
        if (allOrderDeleteCheckDialogState) {
            YesOrNoDialog(
                body = stringResource(R.string.all_order_delete_check_dialog_body),
                yesButtonText = stringResource(R.string.all_order_delete_check_dialog_yes_button),
                onYesClickRequest = {
                    viewModel.allDeleteOrder()
                    viewModel.closeAllOrderDeleteCheckDialog()
                },
                onDismissRequest = { viewModel.closeAllOrderDeleteCheckDialog() })

        }
        if (allOrderDeleteInProgressState) {
            DeleteInProgressDialog()
        }
        if (allOrderDeleteSuccess) {
            ResultDialog(
                onDismissRequest = { viewModel.closeAllOrderDeleteSuccessDialog() },
                resultMessageTitle = stringResource(R.string.all_order_delete_success_dialog_title),
                resultMessageBody = stringResource(R.string.all_order_delete_success_dialog_body)
            )
        }

        Scaffold(
            topBar = {
                SettingWithTitleAppBar(title = stringResource(R.string.order_screen_appbar_title),
                    settingIconOnClick = { navController.navigate(NavigationRoute.SETTING_SCREEN) })
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Column(modifier = Modifier.padding(it)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                    ) {
                        itemsIndexed(orderList) { index, orderItem ->
                            val backgroundColor = itemColors[index % itemColors.size]
                            OrderItem(
                                orderItem = orderItem,
                                orderColor = backgroundColor,
                                onDeleteClick = { viewModel.deleteOrder(orderItem.id) })
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .background(color = LightGray, shape = RoundedCornerShape(5.dp)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterStart),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.order_screen_revenue),
                                    modifier = Modifier.padding(start = 16.dp),
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = TextFormatUtil.priceTextFormat(
                                        revenueState,
                                        stringResource(id = R.string.price)
                                    ),
                                    modifier = Modifier.padding(end = 16.dp),
                                    fontSize = 18.sp,
                                    color = OrangeSoda
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Button(
                            onClick = { viewModel.openAllOrderDeleteCheckDialog() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeSoda)
                        ) {
                            Text(
                                text = stringResource(R.string.order_screen_all_delete),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItem(orderItem: OrderItem, orderColor: Color, onDeleteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight(0.35f),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = orderColor),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${orderItem.tableNumber}",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateFormatUtil.getTime(orderItem.createAt),
                        color = Color.White,
                        modifier = Modifier.padding(end = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.order_screen_menu_name),
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 18.sp,
                    )
                    Text(
                        text = stringResource(R.string.order_screen_menu_count),
                        modifier = Modifier.padding(end = 12.dp),
                        fontSize = 18.sp,
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = LightGray)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = orderItem.order) { cartMenuItem ->
                        OrderMenuItem(cartMenuItem = cartMenuItem)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, color = LightGray),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${orderItem.order.sumOf { it.menuCount }}",
                            modifier = Modifier.padding(start = 16.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightBlue
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = stringResource(R.string.order_screen_menu_product),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = TextFormatUtil.priceTextFormat(
                            orderItem.order.sumOf { it.menuPrice * it.menuCount },
                            stringResource(id = R.string.price)
                        ),
                        modifier = Modifier.padding(end = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .align(
                    Alignment.TopEnd
                )
                .clickable { onDeleteClick() }
        )
    }
}

@Composable
private fun OrderMenuItem(cartMenuItem: CartMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cartMenuItem.menuName,
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 18.dp),
            maxLines = 1
        )
        Text(
            text = cartMenuItem.menuCount.toString(),
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}