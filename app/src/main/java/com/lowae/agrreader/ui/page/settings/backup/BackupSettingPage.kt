package com.lowae.agrreader.ui.page.settings.backup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.repository.webdav.WebDavConfiguration
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.MultipleItemsDialog
import com.lowae.agrreader.ui.component.base.Subtitle
import com.lowae.agrreader.ui.component.base.Tips
import com.lowae.agrreader.ui.component.base.TipsLeft
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.toast
import com.thegrizzlylabs.sardineandroid.DavResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingPage(
    navController: NavHostController,
    backupRestoreViewModel: BackupRestoreViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState = backupRestoreViewModel.backupRestoreUiState.collectAsStateValue()

    var configDialogVisible by remember { mutableStateOf(false) }
    var uploadConfigDialogVisible by remember { mutableStateOf(false) }
    var backupFileListDialogVisible by remember { mutableStateOf(false) }
    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(R.string.backup_settings_title))
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
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.config),
                )
                SettingItem(
                    title = stringResource(R.string.backup_setting_webdav_configuration),
                    icon = Icons.Outlined.CloudQueue,
                    onClick = {
                        configDialogVisible = true
                    },
                ) {
                    Text(
                        text = if (uiState.webDavConfiguration?.isValid == true) stringResource(R.string.configured) else stringResource(
                            R.string.not_configured
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SettingItem(
                    title = stringResource(R.string.backup_setting_backup_data),
                    icon = Icons.Outlined.CloudUpload,
                    desc = stringResource(R.string.backup_setting_backup_data_desc),
                    onClick = {
                        if (uiState.webDavConfiguration?.isValid == true) {
                            uploadConfigDialogVisible = true
                        } else {
                            toast(R.string.backup_setting_not_configured_toast)
                        }
                    },
                )

                SettingItem(
                    title = stringResource(R.string.backup_setting_restore_data),
                    desc = stringResource(R.string.backup_setting_restore_data_desc),
                    icon = Icons.Outlined.CloudDownload,
                    onClick = {
                        if (uiState.webDavConfiguration?.isValid == true) {
                            backupRestoreViewModel.listBackupFiles()
                            backupFileListDialogVisible = true
                        } else {
                            toast(R.string.backup_setting_not_configured_toast)
                        }
                    },
                )
                Tips(text = "目前可支持的备份数据内容包括：\n1.所有的RSS订阅源；\n2.收藏的文章")
            }
        }
    }
    WebdavConfigDialog(
        uiState.webDavConfiguration, configDialogVisible, { configDialogVisible = false }) {
        backupRestoreViewModel.putConfigIfSuccess(it) { success ->
            if (success) {
                configDialogVisible = false
                toast(R.string.backup_webdav_configuration_success_toast)
            } else {
                toast(R.string.backup_webdav_configuration_failure_toast)
            }
        }
    }
    AgrDialog(
        visible = uploadConfigDialogVisible,
        icon = {
            Icon(imageVector = Icons.Rounded.CloudUpload, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.backup_setting_backup_data))
        },
        text = {
            Text(text = stringResource(R.string.backup_upload_dialog_desc))
        },
        onDismiss = {
            uploadConfigDialogVisible = false
        },
        onConfirm = {
            backupRestoreViewModel.backup { success ->
                if (success) {
                    toast(R.string.backup_upload_dialog_success_toast)
                } else {
                    toast(R.string.backup_upload_dialog_failure_toast)
                }
                uploadConfigDialogVisible = false
            }
        },
    )
    BackupFileListDialog(
        backupFileListDialogVisible,
        { backupFileListDialogVisible = false },
        uiState.backupFiles,
    ) {
        backupFileListDialogVisible = false
        if (it.path.isNullOrEmpty()) {
            toast(R.string.backup_restore_failure_path_error)
        } else {
            backupRestoreViewModel.restore(it.path) { success ->
                if (success) {
                    toast(R.string.backup_restore_dialog_success_toast)
                } else {
                    toast(R.string.backup_restore_dialog_failure_toast)
                }
            }
        }
    }
}

@Composable
private fun WebdavConfigDialog(
    currentConfiguration: WebDavConfiguration?,
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (WebDavConfiguration) -> Unit
) {
    var host by remember { mutableStateOf(currentConfiguration?.host.orEmpty()) }
    var username by remember { mutableStateOf(currentConfiguration?.username.orEmpty()) }
    var password by remember { mutableStateOf(currentConfiguration?.password.orEmpty()) }
    AgrDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(imageVector = Icons.Rounded.Cloud, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.backup_webdav_dialog_title))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.backup_webdav_dialog_desc),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = host, onValueChange = { host = it }, label = {
                    Text(text = stringResource(R.string.backup_webdav_configuration_server_address))
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = username, onValueChange = { username = it }, label = {
                    Text(text = stringResource(R.string.backup_webdav_configuration_username))
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = {
                    Text(text = stringResource(R.string.backup_webdav_configuration_password))
                }, visualTransformation = PasswordVisualTransformation())
                TipsLeft(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(R.string.backup_webdav_dialog_tips)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(WebDavConfiguration(host, username, password)) }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        })
}

@Composable
private fun BackupFileListDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    backupFiles: List<DavResource>,
    onItemSelect: (DavResource) -> Unit
) {
    var selected by remember { mutableStateOf<DavResource?>(null) }

    MultipleItemsDialog(
        visible = visible,
        title = "恢复数据",
        onDismissRequest = onDismiss,
        options = backupFiles,
        confirmButton = {
            TextButton(onClick = {
                if (selected != null) {
                    onItemSelect(selected!!)
                }
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    ) { option ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selected = option
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(selected = option.href == selected?.href, onClick = {
                selected = option
            })
            Text(
                modifier = Modifier,
                text = option.displayName ?: option.name ?: option.href.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(baselineShift = BaselineShift.None),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

}