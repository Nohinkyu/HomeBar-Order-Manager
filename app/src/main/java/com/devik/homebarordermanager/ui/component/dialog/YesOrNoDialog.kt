package com.devik.homebarordermanager.ui.component.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.devik.homebarordermanager.R
import com.devik.homebarordermanager.ui.theme.LightGray
import com.devik.homebarordermanager.ui.theme.OrangeSoda

@Composable
fun YesOrNoDialog(
    body: String,
    yesButtonText: String,
    onDismissRequest: () -> Unit,
    onYesClickRequest: () -> Unit
) {

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.heightIn(8.dp))

                Image(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = stringResource(
                        R.string.content_description_info_icon
                    )
                )

                Spacer(modifier = Modifier.heightIn(8.dp))

                Text(
                    text = body,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.heightIn(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.5f)
                            .padding(start = 16.dp, end = 16.dp)
                            .heightIn(48.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LightGray),
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_button_cancel),
                            color = Color.Black
                        )
                    }
                    Button(
                        onClick = onYesClickRequest,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxWidth(0.5f)
                            .padding(start = 16.dp, end = 16.dp)
                            .heightIn(48.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeSoda)
                    ) {
                        Text(text = yesButtonText, color = Color.White)
                    }
                }
            }
        }
    }
}