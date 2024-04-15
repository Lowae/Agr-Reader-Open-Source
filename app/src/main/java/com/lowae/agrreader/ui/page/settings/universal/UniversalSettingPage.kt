package com.lowae.agrreader.ui.page.settings.universal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.SyncLock
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.emptyPreferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.KeepArchivedPreference
import com.lowae.agrreader.data.model.preference.LocalKeepArchived
import com.lowae.agrreader.data.model.preference.LocalSyncInterval
import com.lowae.agrreader.data.model.preference.LocalSyncOnStart
import com.lowae.agrreader.data.model.preference.LocalSyncOnlyOnWiFi
import com.lowae.agrreader.data.model.preference.LocalSyncOnlyWhenCharging
import com.lowae.agrreader.data.model.preference.SyncIntervalPreference
import com.lowae.agrreader.data.model.preference.datastore.FeedSyncLimitCountPreference
import com.lowae.agrreader.data.model.preference.datastore.SingleDataStore
import com.lowae.agrreader.data.model.preference.not
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.ui.component.base.RadioDialog
import com.lowae.agrreader.ui.component.base.RadioDialogOption
import com.lowae.agrreader.ui.component.base.Subtitle
import com.lowae.agrreader.ui.page.common.RssServerSettingRouter
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.agrreader.ui.page.settings.accounts.AccountViewModel
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.showToastLong
import com.lowae.component.base.popup.SimpleTextDropMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSettingPage(
    navController: NavHostController,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val syncInterval = LocalSyncInterval.current
    val syncOnStart = LocalSyncOnStart.current
    val syncOnlyOnWiFi = LocalSyncOnlyOnWiFi.current
    val syncOnlyWhenCharging = LocalSyncOnlyWhenCharging.current
    val keepArchived = LocalKeepArchived.current
    val uiState = viewModel.accountUiState.collectAsStateValue()
    val selectedAccount = uiState.currentAccount.collectAsStateValue()
    val singleStorePreferences =
        SingleDataStore.store.data.collectAsStateValue(initial = emptyPreferences())

    val scope = rememberCoroutineScope()

    var syncIntervalDialogVisible by remember { mutableStateOf(false) }
    var keepArchivedDialogVisible by remember { mutableStateOf(false) }
    var feedSyncLimitCountDialogVisible by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument()
    ) { result ->
        viewModel.exportAsOPML(selectedAccount!!.id!!) { string ->
            result?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(string.toByteArray())
                }
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.universal),
                    )
                },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        navController.popBackStack()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "RSS账户",
                )

                SettingItem(
                    title = "账户管理",
                    desc = selectedAccount?.type?.toDesc(context),
                    icon = Icons.Outlined.ManageAccounts,
                    onClick = { RssServerSettingRouter.navigate(navController) },
                )
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.synchronous),
                )
                SettingItem(
                    title = stringResource(R.string.sync_interval),
                    desc = syncInterval.toDesc(context),
                    icon = Icons.Outlined.Sync,
                    onClick = { syncIntervalDialogVisible = true },
                ) {}
                SettingItem(
                    title = stringResource(R.string.sync_once_on_start),
                    icon = Icons.Outlined.SyncLock,
                    onClick = {
                        syncOnStart.not().put(scope)
                    },
                ) {
                    RYSwitch(activated = syncOnStart.value) {
                        syncOnStart.not().put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.only_on_wifi),
                    icon = Icons.Outlined.Wifi,
                    onClick = {
                        syncOnlyOnWiFi.not().put(scope)
                    },
                ) {
                    RYSwitch(activated = syncOnlyOnWiFi.value) {
                        syncOnlyOnWiFi.not().put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.only_when_charging),
                    icon = Icons.Outlined.BatteryChargingFull,
                    onClick = {
                        syncOnlyWhenCharging.not().put(scope)
                    },
                ) {
                    RYSwitch(activated = syncOnlyWhenCharging.value) {
                        syncOnlyWhenCharging.not().put(scope)

                    }
                }
                SettingItem(
                    title = stringResource(R.string.keep_archived_articles),
                    desc = keepArchived.toDesc(context),
                    icon = Icons.Outlined.Archive,
                    onClick = { keepArchivedDialogVisible = true },
                ) {}
                SettingItem(
                    title = stringResource(R.string.feed_sync_limit_count_title),
                    desc = FeedSyncLimitCountPreference.fromPreferences(singleStorePreferences)
                        .toDesc(),
                    onClick = { feedSyncLimitCountDialogVisible = true }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.advanced),
                )
                val exportFileName = "${stringResource(R.string.app_name)}.opml"
                SettingItem(
                    title = stringResource(R.string.export_as_opml),
                    icon = Icons.Outlined.ImportExport,
                    onClick = {
                        launcher.launch(exportFileName)
                    },
                ) {}
                SettingItem(
                    icon = Icons.Outlined.ClearAll,
                    title = stringResource(R.string.clear_all_articles),
                    onClick = { viewModel.showClearDialog() },
                ) {}
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    RadioDialog(
        visible = syncIntervalDialogVisible,
        title = stringResource(R.string.sync_interval),
        options = SyncIntervalPreference.values.map {
            RadioDialogOption(
                text = it.toDesc(context),
                selected = it == syncInterval,
            ) {
                it.put(scope)
            }
        }
    ) {
        syncIntervalDialogVisible = false
    }

    RadioDialog(
        visible = keepArchivedDialogVisible,
        title = stringResource(R.string.keep_archived_articles),
        options = KeepArchivedPreference.values.map {
            RadioDialogOption(
                text = it.toDesc(context),
                selected = it == keepArchived,
            ) {
                it.put(scope)
            }
        }
    ) {
        keepArchivedDialogVisible = false
    }

    AgrDialog(
        visible = uiState.clearDialogVisible,
        onDismissRequest = {
            viewModel.hideClearDialog()
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.DeleteSweep,
                contentDescription = stringResource(R.string.clear_all_articles),
            )
        },
        title = {
            Text(text = stringResource(R.string.clear_all_articles))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedAccount?.let {
                        viewModel.clear(it) {
                            viewModel.hideClearDialog()
                            context.showToastLong(context.getString(R.string.clear_all_articles_toast))
                        }
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.clear),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.hideClearDialog()
                }
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                )
            }
        },
    )

    SimpleTextDropMenu(
        visible = feedSyncLimitCountDialogVisible,
        data = FeedSyncLimitCountPreference.values,
        onText = { it.toDesc() },
        onDismiss = { feedSyncLimitCountDialogVisible = false }
    ) {
        it.put(scope)
        feedSyncLimitCountDialogVisible = false
    }

}