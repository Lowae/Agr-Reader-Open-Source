package com.lowae.agrreader.ui.page.settings.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lowae.agrreader.BuildConfig
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.DynamicSVGImage
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.LogoText
import com.lowae.agrreader.ui.svg.illustrations.Illustrations
import com.lowae.agrreader.ui.svg.illustrations.icon
import com.lowae.agrreader.utils.ext.openInCustomTabs
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavHostController) {

    val context = LocalContext.current

    AgrScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicSVGImage(
                    modifier = Modifier
                        .size(256.dp)
                        .padding(start = 8.dp),
                    svgImageString = Illustrations.icon
                )
                LogoText(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Material3风格极简优美的RSS阅读器",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.version,
                        "${BuildConfig.VERSION_NAME}(${
                            SimpleDateFormat(
                                "yyyyMMdd",
                                Locale.getDefault()
                            ).format(BuildConfig.BUILD_TIME)
                        })-${BuildConfig.BUILD_TYPE}"
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )


                TextButton(onClick = { context.openInCustomTabs("https://www.agrreader.xyz/") }) {
                    Text(text = "官方网站")
                }

                Spacer(modifier = Modifier.height(8.dp))

                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyLarge
                ) {
                    Text(text = "开发者：Lowae")
                    Text(text = "邮箱地址：Lowae@agrreader.xyz")
                }
            }
        }
    }

}