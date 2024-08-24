package com.devik.homebarordermanager.ui.component.topappbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.devik.homebarordermanager.ui.theme.OrangeSoda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingWithTitleAppBar(title: String, settingIconOnClick:() -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title, fontSize = 24.sp, color = OrangeSoda)
        },
        actions = {
            IconButton(onClick =  settingIconOnClick ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "")
            }
        }
    )
}