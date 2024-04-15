package com.lowae.agrreader.ui.page.settings

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Rocket
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.datastore.SingleDataStore
import com.lowae.agrreader.data.model.service.VersionInfo
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.utils.GsonUtils
import com.lowae.agrreader.utils.ext.openURL

@Composable
fun UpgradeVersionDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val versionInfo = remember {
        GsonUtils.fromJson<VersionInfo>(
            SingleDataStore.get(SingleDataStore.Keys.VERSION_INFO, "")
        ) ?: VersionInfo()
    }

    AgrDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Rocket,
                contentDescription = stringResource(R.string.get_new_updates),
            )
        },
        title = {
            Text(
                text = stringResource(R.string.get_new_updates_desc, versionInfo.versionName)
            )
        },
        text = {
            Text(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                text = htmlToAnnotatedString(versionInfo.changelog),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = {
                AgrReaderApp.application.openURL(versionInfo.url)
            }) {
                Text(text = "跳转浏览器下载")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )

}