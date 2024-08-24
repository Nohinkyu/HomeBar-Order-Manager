package com.devik.homebarordermanager.ui.screen.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.devik.homebarordermanager.BuildConfig
import com.devik.homebarordermanager.R
import com.devik.homebarordermanager.ui.component.dialog.YesOrNoDialog
import com.devik.homebarordermanager.ui.component.navigation.NavigationRoute
import com.devik.homebarordermanager.ui.theme.LightGray
import com.devik.homebarordermanager.ui.theme.OrangeSoda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    CompositionLocalProvider(
        androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current,
    ) {
        val viewModel: SettingViewModel = hiltViewModel()
        val signDialogState by viewModel.signOutDialogState.collectAsStateWithLifecycle()
        val userMail by viewModel.userMail.collectAsStateWithLifecycle()
        val context = LocalContext.current

        if (signDialogState) {
            YesOrNoDialog(body = stringResource(R.string.sign_out_dialog_message).trimMargin(),
                yesButtonText = stringResource(R.string.sign_out_dialog_button_sign_out),
                onDismissRequest = { viewModel.closeSignOutDialog() },
                onYesClickRequest = {
                    viewModel.closeSignOutDialog()
                    viewModel.signOut()
                    navController.navigate(NavigationRoute.SIGN_IN_SCREEN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.top_appbar_title_setting),
                            fontSize = 24.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                )
            },
            modifier = Modifier.padding(top = 8.dp)
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                HorizontalDivider(thickness = 1.dp, color = LightGray)

                Spacer(modifier = Modifier.size(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.email),
                        modifier = Modifier
                            .padding(16.dp),
                        fontSize = 24.sp,
                        color = OrangeSoda
                    )
                    Text(
                        text = userMail,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterEnd),
                        fontSize = 24.sp
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = LightGray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GOOGLE_FORMS_URL))
                            context.startActivity(intent)
                        }
                ) {
                    Text(
                        text = stringResource(R.string.setting_screen_text_google_forms),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontSize = 24.sp
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = LightGray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openSignOutDialog() }) {
                    Text(
                        text = stringResource(R.string.setting_screen_text_sign_out),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontSize = 24.sp
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = LightGray)
            }
        }
    }
}