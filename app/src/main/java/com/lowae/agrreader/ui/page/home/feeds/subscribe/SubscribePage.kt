package com.lowae.agrreader.ui.page.home.feeds.subscribe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.TextFieldDialog
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.utils.ext.CurrentAccountType
import com.lowae.agrreader.utils.ext.collectAsStateValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribePage(
    navController: NavHostController,
    subscribeViewModel: SubscribeViewModel = hiltViewModel()
) {
    val subscribeUiState = subscribeViewModel.subscribeUiState.collectAsStateValue()
    val currentAccountType = CurrentAccountType
    LaunchedEffect(Unit) {
        subscribeViewModel.init()
    }
    AgrScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        if (navController.previousBackStackEntry == null) {
                            navController.navigate(RouteName.FEEDS) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (currentAccountType) {
                AccountType.FreshRSS.id, AccountType.GoogleReader.id -> {
                    FreshRssAccountTypeSubscribePage(subscribeUiState, subscribeViewModel, navController)
                }

                AccountType.Local.id -> {
                    LocalAccountTypeSubscribePage(subscribeUiState, subscribeViewModel, navController)
                }
            }
        }
    }

    TextFieldDialog(
        visible = subscribeUiState.newGroupDialogVisible,
        title = stringResource(R.string.create_new_group),
        icon = Icons.Outlined.CreateNewFolder,
        initialValue = "",
        placeholder = stringResource(R.string.name),
        onDismissRequest = {
            subscribeViewModel.hideNewGroupDialog()
        },
        onConfirm = {
            subscribeViewModel.addNewGroup(it)
        }
    )
}