package com.devik.homebarordermanager.ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.ui.screen.order.OrderScreen
import com.devik.homebarordermanager.ui.screen.setting.SettingScreen
import com.devik.homebarordermanager.ui.screen.signin.SignInScreen
import com.devik.homebarordermanager.util.Constants

@Composable
fun ScreenNavigation(navController: NavHostController) {
    val preferenceManager = PreferenceManager(LocalContext.current)
    val userMail = preferenceManager.getString(Constants.KEY_MAIL_ADDRESS, "")
    val userImage = preferenceManager.getString(Constants.KEY_PROFILE_IMAGE, "")

    val startDestination: String = if (userMail.isBlank() && userImage.isBlank()) {
        NavigationRoute.SIGN_IN_SCREEN
    } else {
        NavigationRoute.ORDER_SCREEN
    }


    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavigationRoute.SIGN_IN_SCREEN) {
            SignInScreen(navController)
        }
        composable(NavigationRoute.ORDER_SCREEN) {
            OrderScreen(navController = navController)
        }
        composable(NavigationRoute.SETTING_SCREEN) {
            SettingScreen(navController = navController)
        }
    }
}

object NavigationRoute {
    const val SIGN_IN_SCREEN = "sign_in_screen"
    const val SETTING_SCREEN = "setting_screen"
    const val ORDER_SCREEN = "order_screen"
}