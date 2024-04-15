package com.lowae.agrreader.ui.page.home.feeds.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.TextFieldDialog
import com.lowae.agrreader.ui.component.base.TipsLeft
import com.lowae.agrreader.ui.page.home.feeds.OptionPresetView
import com.lowae.agrreader.ui.page.home.feeds.drawer.group.GroupOptionViewModel
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoPage(
    navController: NavHostController,
    groupOptionViewModel: GroupOptionViewModel = hiltViewModel()
) {

    val groupOptionUiState = groupOptionViewModel.groupOptionUiState.collectAsStateValue()
    val group = groupOptionUiState.group

    AgrScaffold(topBar = {
        TopAppBar(
            title = { },
            navigationIcon = {
                FeedbackIconButton(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onSurface
                ) {
                    navController.popBackStack()
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Rounded.Folder,
                contentDescription = stringResource(R.string.groups)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier,
                text = group?.name.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            OptionPresetView(
                selectedAllowNotificationPreset = groupOptionUiState.allAllowNotification,
                selectedContentSourceTypePreset = groupOptionUiState.allSourceType,
                allowNotificationPresetOnClick = { groupOptionViewModel.changeAllAllowNotification() },
                contentSourceTypePresetOnClick = { groupOptionViewModel.changeAllSourceType(it) },
                interceptionResourceLoad = groupOptionUiState.allAllowInterceptedResource,
                interceptionResourceLoadClick = { groupOptionViewModel.changeAllInterceptResource() }
            )

            TipsLeft(modifier = Modifier.padding(12.dp), text = "以上操作作用于分组内所有订阅源")
        }
    }

    TextFieldDialog(
        visible = groupOptionUiState.renameDialogVisible,
        title = stringResource(R.string.rename),
        icon = Icons.Outlined.Edit,
        initialValue = group?.name.orEmpty(),
        onDismissRequest = {
            groupOptionViewModel.hideRenameDialog()
        },
        onConfirm = { newName ->
            groupOptionViewModel.rename(newName)
            toast(R.string.rename_toast, newName)
        }
    )
}