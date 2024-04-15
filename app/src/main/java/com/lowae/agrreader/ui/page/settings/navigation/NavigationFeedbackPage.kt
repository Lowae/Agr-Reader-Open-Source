package com.lowae.agrreader.ui.page.settings.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Recommend
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.lowae.agrreader.CrashHandler
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.page.settings.SelectableSettingGroupItem
import com.lowae.agrreader.utils.compat.PackageManagerCompat
import com.lowae.agrreader.utils.ext.openInCustomTabs
import com.lowae.agrreader.utils.ext.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAndFeedbackPage(navController: NavHostController) {
    val context = LocalContext.current
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(R.string.agr_settings_navigation_and_feedback))
                },
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
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                SelectableSettingGroupItem(
                    title = "订阅推荐和反馈",
                    desc = "RSS推荐和提交任何反馈信息",
                    icon = Icons.Rounded.Recommend,
                ) {
                    context.openInCustomTabs("https://www.agrreader.xyz/navigation")
                }
            }
            item {
                SelectableSettingGroupItem(
                    title = "发送日志",
                    desc = "通过邮件等方式分享App崩溃日志",
                    icon = Icons.Rounded.Feedback,
                ) {
                    val file = CrashHandler.getCrashFile(context)
                    if (file.exists()) {
                        val fileUri = FileProvider.getUriForFile(
                            context,
                            "com.lowae.agrreader.fileprovider",
                            CrashHandler.getCrashFile(context)
                        )
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("Lowae@agrreader.xyz"))
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "AgrReader-${PackageManagerCompat.versionCode}-${System.currentTimeMillis()}"
                            )
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                        }, context.getString(R.string.share)))
                    } else {
                        toast("暂无需要反馈的日志")
                    }
                }
            }
        }
    }
}