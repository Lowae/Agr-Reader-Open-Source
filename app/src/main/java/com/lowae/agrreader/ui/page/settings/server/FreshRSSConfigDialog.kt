package com.lowae.agrreader.ui.page.settings.server

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.TipsLeft

@Composable
fun FreshRSSConfigDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    loading: Boolean = false,
    onConfirm: (serverUrl: String, username: String, password: String) -> Unit
) {

    var serverUrl by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AgrDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_freshrss),
                contentDescription = null,
                tint = Color.Unspecified
            )
        },
        title = {
            Text(text = "FreshRSS")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "目前仅支持Google Reader API访问FreshRSS",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = serverUrl, onValueChange = { serverUrl = it }, label = {
                    Text(text = "域名")
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = username, onValueChange = { username = it }, label = {
                    Text(text = "用户名")
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = {
                    Text(text = "密码")
                }, visualTransformation = PasswordVisualTransformation())
                TipsLeft(
                    Modifier.padding(vertical = 6.dp),
                    text = "密码请填写个人菜单中API密码而非账户密码。域名为API密码下方的链接打开后使用Google Reader compatible API下的地址"
                )
            }
        },
        confirmButton = {
            if (loading.not()) {
                TextButton(onClick = {
                    onConfirm(serverUrl, username, password)
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        })
}