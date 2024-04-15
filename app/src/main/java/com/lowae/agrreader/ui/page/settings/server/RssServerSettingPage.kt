package com.lowae.agrreader.ui.page.settings.server

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.data.model.account.security.FeverSecurityKey
import com.lowae.agrreader.data.model.account.security.FreshRSSSecurityKey
import com.lowae.agrreader.data.model.account.security.GoogleReaderSecurityKey
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.Tips
import com.lowae.agrreader.ui.page.home.feeds.accounts.AccountIcon
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.agrreader.ui.page.settings.accounts.AccountViewModel
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssServerSettingPage(
    navController: NavHostController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = navController.context
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val accountUiState = accountViewModel.accountUiState.collectAsStateValue()
    val accounts = accountUiState.accounts.collectAsStateValue()
    var freshRssDialogVisible by remember { mutableStateOf(false) }
    var gReaderDialogVisible by remember { mutableStateOf(false) }
    var feverDialogVisible by remember { mutableStateOf(false) }
    var isServerLoading by remember { mutableStateOf(false) }
    var currentDeletedAccount by remember { mutableStateOf<Account?>(null) }

    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = "RSS账户列表")
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
                val freshRssAccount = accounts.find { a -> a.type == AccountType.FreshRSS }
                SettingItem(
                    title = stringResource(R.string.fresh_rss),
                    desc = "https://freshrss.org/",
                    iconPainter = painterResource(id = R.drawable.ic_freshrss),
                    iconTint = if (freshRssAccount != null) Color.Unspecified else LocalContentColor.current,
                    onClick = {
                        if (freshRssAccount == null) {
                            freshRssDialogVisible = true
                        }
                    },
                    onLongClick = {
                        currentDeletedAccount = freshRssAccount
                    }
                ) {
                    Text(text = if (freshRssAccount != null) stringResource(id = R.string.configured) else "")
                }
            }
            item {
                val gReaderRssAccount = accounts.find { a -> a.type == AccountType.GoogleReader }
                SettingItem(
                    title = stringResource(R.string.google_reader),
                    desc = stringResource(R.string.google_reader_desc),
                    iconPainter = if (gReaderRssAccount != null) painterResource(R.drawable.ic_google_reader) else painterResource(
                        R.drawable.ic_google_reader_disable
                    ),
                    iconTint = Color.Unspecified,
                    onClick = {
                        if (gReaderRssAccount == null) {
                            gReaderDialogVisible = true
                        }
                    },
                    onLongClick = {
                        currentDeletedAccount = gReaderRssAccount
                    }
                ) {
                    Text(text = if (gReaderRssAccount != null) stringResource(id = R.string.configured) else "")
                }
            }
            item {
                val feverRssAccount = accounts.find { a -> a.type == AccountType.Fever }
                SettingItem(
                    title = stringResource(R.string.fever),
                    desc = stringResource(R.string.fever_desc),
                    iconPainter = painterResource(R.drawable.ic_fever),
                    iconTint = if (feverRssAccount != null) Color.Unspecified else LocalContentColor.current,
                    onClick = {
                        if (feverRssAccount == null) {
                            feverDialogVisible = true
                        }
                    },
                    onLongClick = {
                        currentDeletedAccount = feverRssAccount
                    }
                ) {
                    Text(text = if (feverRssAccount != null) stringResource(id = R.string.configured) else "")
                }
            }
            item {
                Tips(text = "- MiniFlux、Tiny Tiny RSS等其他RSS服务可使用Google Reader Api或Fever Api\n- 同一RSS服务不推荐利用同API重复订阅\n- 非Pro版仅限一个当前账号，Pro版可支持多账户自由切换\n- 长按可选择是否删除该RSS账户")
            }
        }
    }

    FreshRSSConfigDialog(
        visible = freshRssDialogVisible,
        onDismiss = { freshRssDialogVisible = false },
        loading = isServerLoading,
    ) { serverUrl: String, username: String, password: String ->
        isServerLoading = true
        accountViewModel.addAccount(
            Account(
                type = AccountType.FreshRSS,
                name = AccountType.FreshRSS.toDesc(context),
                securityKey = FreshRSSSecurityKey(serverUrl, username, password).toString()
            )
        ) {
            isServerLoading = false
            if (it == null) {
                toast("无效凭证")
            } else {
                toast("登录成功")
                freshRssDialogVisible = false
            }
        }
    }
    GReaderConfigDialog(
        visible = gReaderDialogVisible,
        onDismiss = { gReaderDialogVisible = false },
        loading = isServerLoading,
    ) { serverUrl: String, username: String, password: String ->
        isServerLoading = true
        val compactUrl =
            if (serverUrl.last() == '/') serverUrl.substring(0, serverUrl.length - 1) else serverUrl
        accountViewModel.addAccount(
            Account(
                type = AccountType.GoogleReader,
                name = AccountType.GoogleReader.toDesc(context),
                securityKey = GoogleReaderSecurityKey(compactUrl, username, password).toString()
            )
        ) {
            isServerLoading = false
            if (it == null) {
                toast("无效凭证")
            } else {
                toast("登录成功")
                gReaderDialogVisible = false
            }
        }
    }
    FeverConfigDialog(
        visible = feverDialogVisible,
        onDismiss = { feverDialogVisible = false },
        loading = isServerLoading,
    ) { serverUrl: String, username: String, password: String ->
        isServerLoading = true
        accountViewModel.addAccount(
            Account(
                type = AccountType.Fever,
                name = AccountType.Fever.toDesc(context),
                securityKey = FeverSecurityKey(serverUrl, username, password).toString()
            )
        ) {
            isServerLoading = false
            if (it == null) {
                toast("无效凭证")
            } else {
                toast("登录成功")
                feverDialogVisible = false
            }
        }
    }
    RssServerDeleteDialog(currentDeletedAccount, { currentDeletedAccount = null }) { account ->
        account.id?.also { accountId ->
            accountViewModel.delete(accountId) {
                currentDeletedAccount = null
                toast("账户删除成功")
            }
        }
    }
}

@Composable
private fun RssServerDeleteDialog(
    account: Account?,
    onDismiss: () -> Unit,
    onConfirm: (Account) -> Unit
) {
    if (account != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                AccountIcon(account.type)
            },
            title = {
                Text(text = account.type.toDesc())
            },
            text = {
                Text(text = "是否确认移除当前'${account.type.toDesc()}'账户")
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(account) }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        )
    }
}