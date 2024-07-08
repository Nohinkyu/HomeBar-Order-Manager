package com.devik.homebarordermanager.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.devik.homebarordermanager.R
import com.devik.homebarordermanager.ui.theme.LightGray
import com.devik.homebarordermanager.ui.theme.OrangeSoda

@Composable
fun ResultDialog(
    onDismissRequest: () -> Unit,
    resultMessageTitle: String,
    resultMessageBody: String,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(5.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = resultMessageTitle,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Divider(thickness = 2.dp, color = LightGray, modifier = Modifier.padding(top = 16.dp))
            Text(
                text = resultMessageBody,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 32.dp),
                lineHeight = 28.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(18.dp))

            Spacer(modifier = Modifier.size(18.dp))
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeSoda),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_button_yes),
                    fontSize = 24.sp
                )
            }
        }
    }
}