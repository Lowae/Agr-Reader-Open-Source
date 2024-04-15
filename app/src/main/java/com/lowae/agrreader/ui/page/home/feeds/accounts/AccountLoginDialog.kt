//package com.lowae.agrreader.ui.page.home.feeds.accounts
//
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.AlertDialogDefaults
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.DialogProperties
//import com.lowae.agrreader.ui.component.base.AgrDialog
//import com.lowae.agrreader.ui.component.base.DynamicSVGImage
//import com.lowae.agrreader.ui.svg.illustrations.Illustrations
//import com.lowae.agrreader.ui.svg.illustrations.icon
//import com.lowae.agrreader.utils.ext.showToastLong
//import com.lowae.component.constant.ElevationTokens
//
//@Composable
//fun AccountLoginDialog(shown: Boolean, onDismissRequest: () -> Unit) {
//    val context = LocalContext.current
//    val screenWidthDp = LocalConfiguration.current.screenWidthDp
//    var username by remember(shown) { mutableStateOf("") }
//    var email by remember(shown) { mutableStateOf("") }
//    var password by remember(shown) { mutableStateOf("") }
//    var confirmPassword by remember(shown) { mutableStateOf("") }
//    var code by remember(shown) { mutableStateOf("") }
//    var isRegister by remember(shown) { mutableStateOf(false) }
//    var isSendCode by remember(shown, isRegister) { mutableStateOf(false) }
//
//    AgrDialog(
//        visible = shown, onDismissRequest = onDismissRequest,
//        modifier = Modifier.width((screenWidthDp * 0.8).dp),
//        properties = DialogProperties(usePlatformDefaultWidth = false),
//    ) {
//
//        Surface(
//            shape = AlertDialogDefaults.shape,
//            color = AlertDialogDefaults.containerColor,
//            tonalElevation = ElevationTokens.Level3.dp,
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(24.dp)
//            ) {
//                DynamicSVGImage(
//                    svgImageString = Illustrations.icon,
//                    modifier = Modifier.size(72.dp),
//                    contentDescription = ""
//                )
//
//                AnimatedContent(targetState = isRegister, label = "") { register ->
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.padding(24.dp)
//                    ) {
//                        if (register) {
//                            LoginTextField("用户名(可选)", username) { username = it }
//                            LoginTextField(
//                                "邮箱",
//                                value = email,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
//                            ) { email = it }
//                            LoginTextField(
//                                "密码",
//                                password,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                                visualTransformation = PasswordVisualTransformation()
//                            ) { password = it }
//                            LoginTextField(
//                                "确认密码",
//                                confirmPassword,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                                visualTransformation = PasswordVisualTransformation()
//                            ) { confirmPassword = it }
//                            LoginTextField(
//                                "验证码", code,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                            ) { code = it }
//                            TextButton(
//                                colors = ButtonDefaults.filledTonalButtonColors(),
//                                elevation = ButtonDefaults.buttonElevation(),
//                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 24.dp, vertical = 6.dp),
//                                onClick = {
//                                    if (isSendCode.not()) {
//                                        isSendCode = true
//                                        context.showToastLong("验证码已经发送至您的邮箱，请你查收并填写对应验证码")
//                                    }
//                                }
//                            ) {
//                                Text(
//                                    modifier = Modifier.padding(horizontal = 6.dp),
//                                    text = if (isSendCode) "完成注册" else "发送验证码",
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }
//                        } else {
//                            LoginTextField(
//                                "邮箱",
//                                value = email,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
//                            ) { email = it }
//                            LoginTextField(
//                                "密码",
//                                password,
//                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                                visualTransformation = PasswordVisualTransformation()
//                            ) { password = it }
//                            TextButton(
//                                colors = ButtonDefaults.filledTonalButtonColors(),
//                                elevation = ButtonDefaults.buttonElevation(),
//                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 24.dp, vertical = 6.dp),
//                                onClick = {
//
//                                }
//                            ) {
//                                Text(
//                                    modifier = Modifier.padding(horizontal = 6.dp),
//                                    text = "登陆",
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }
//
//                            TextButton(
//                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
//                                onClick = { isRegister = true }
//                            ) {
//                                Text(
//                                    modifier = Modifier.padding(horizontal = 6.dp),
//                                    text = "去注册->",
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun LoginTextField(
//    label: String,
//    value: String,
//    visualTransformation: VisualTransformation = VisualTransformation.None,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    onValueChange: (String) -> Unit
//) {
//    OutlinedTextField(
//        modifier = Modifier.padding(vertical = 4.dp),
//        singleLine = true,
//        value = value,
//        onValueChange = onValueChange,
//        label = {
//            Text(text = label)
//        },
//        visualTransformation = visualTransformation,
//        keyboardActions = keyboardActions,
//        keyboardOptions = keyboardOptions,
//    )
//}