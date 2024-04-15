package com.lowae.agrreader.ui.page.home.feeds

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RuleFolder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.general.Filter
import com.lowae.agrreader.data.model.general.iconFilled
import com.lowae.agrreader.data.model.general.iconOutline
import com.lowae.agrreader.data.model.general.title
import com.lowae.agrreader.data.model.preference.LocalNewVersionNumber
import com.lowae.agrreader.ui.component.base.DynamicSVGImage
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.LogoText
import com.lowae.agrreader.ui.component.showProCheckDialog
import com.lowae.agrreader.ui.page.common.FeedManagementRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.page.home.HomeViewModel
import com.lowae.agrreader.ui.page.home.feeds.accounts.AccountIconBox
import com.lowae.agrreader.ui.page.home.feeds.subscribe.SubscribeViewModel
import com.lowae.agrreader.ui.page.settings.accounts.AccountViewModel
import com.lowae.agrreader.ui.svg.illustrations.AgrPro
import com.lowae.agrreader.ui.svg.illustrations.Illustrations
import com.lowae.agrreader.utils.compat.PackageManagerCompat
import com.lowae.agrreader.utils.ext.CurrentAccountId
import com.lowae.agrreader.utils.ext.collectAsStateValue
import com.lowae.agrreader.utils.ext.findActivity
import com.lowae.agrreader.utils.ext.formatAtRecent
import com.lowae.agrreader.utils.tap
import com.lowae.component.base.ExpandableIcon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FeedsPage(
    navController: NavHostController,
    feedsViewModel: FeedsViewModel = hiltViewModel(),
    subscribeViewModel: SubscribeViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel,
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val filterUiState = feedsViewModel.filterUiState.collectAsStateValue()

    BackHandler(true) {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            context.findActivity()?.moveTaskToBack(false)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FeedsDrawerContent(
                navController,
                accountViewModel,
                filterUiState.filter,
                onProClick = {
                    navController.navigate(RouteName.PRO_PAY)
                },
                onFilterClick = { filter ->
                    feedsViewModel.changeFilter(filter)
                    scope.launch {
                        drawerState.close()
                    }
                },
                onRecentlyClick = {
                    navController.navigate(RouteName.RECENTLY_READ)
                }
            ) {
                navController.navigate(RouteName.SETTINGS) {
                    launchSingleTop = true
                }
            }
        },
        content = {
            FeedsContent(
                navController,
                feedsViewModel,
                subscribeViewModel,
                homeViewModel,
                drawerState
            )
        }
    )
}

@Composable
private fun FeedsDrawerContent(
    navController: NavHostController,
    accountViewModel: AccountViewModel,
    filter: Filter,
    onProClick: () -> Unit,
    onFilterClick: (Filter) -> Unit,
    onRecentlyClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val view = LocalView.current
    val isProActive = false
    val showNewVersionBadge =
        LocalNewVersionNumber.current.newVersionCode > PackageManagerCompat.versionCode
    val accountUiState = accountViewModel.accountUiState.collectAsStateValue()
    val accounts = accountUiState.accounts.collectAsStateValue(initial = emptyList())
    val currentAccount = accountUiState.currentAccount.collectAsStateValue()
    var accountsTabDialogVisible by remember { mutableStateOf(false) }
    ModalDrawerSheet(modifier = Modifier.padding(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoText(
                text = stringResource(id = R.string.app_name),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            FeedbackIconButton(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.Top),
                showBadge = showNewVersionBadge,
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.onSurface,
                onClick = onSettingsClick
            )
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                text = stringResource(R.string.accounts),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            currentAccount?.also {
                val accountList = if (isProActive.not()) emptyList() else accounts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            accountsTabDialogVisible = !accountsTabDialogVisible
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccountIconBox(Modifier.size(42.dp), account = currentAccount)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = currentAccount.name,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "上次同步：${currentAccount.updateAt?.formatAtRecent()}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    if (accountList.size > 1) {
                        ExpandableIcon({ accountsTabDialogVisible })
                    }
                }
                AnimatedVisibility(visible = accountsTabDialogVisible) {
                    AccountList(accountList) {
                        accountViewModel.switchAccount(it)
                    }
                }
            }

            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                text = stringResource(R.string.subscribe_feeds),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            listOf(Filter.All, Filter.Unread, Filter.Starred).forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (filter == item) {
                                item.iconFilled
                            } else {
                                item.iconOutline
                            },
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = filter == item,
                    onClick = {
                        view.tap()
                        onFilterClick(item)
                    },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                text = stringResource(R.string.other_options),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = stringResource(R.string.recently_read)
                    )
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.recently_read),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        DynamicSVGImage(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(20.dp),
                            svgImageString = Illustrations.AgrPro
                        )
                    }
                },
                selected = false,
                onClick = {
                    view.tap()
                    if (isProActive.not()) {
                        showProCheckDialog(navController)
                    } else {
                        onRecentlyClick()
                    }
                },
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.RuleFolder,
                        contentDescription = stringResource(R.string.feed_management)
                    )
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.feed_management),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                selected = false,
                onClick = {
                    view.tap()
                    FeedManagementRouter.navigate(navController)
                },
            )
        }

    }
}

@Composable
fun ModalDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val drawerMaxWidth = (screenWidthDp * 0.7F).roundToInt().coerceAtMost(280).dp
    Surface(
        modifier = Modifier
            .sizeIn(
                minWidth = 240.dp,
                maxWidth = drawerMaxWidth
            )
            .fillMaxHeight(),
        shape = drawerShape,
        color = drawerContainerColor,
        contentColor = drawerContentColor,
        tonalElevation = drawerTonalElevation
    ) {
        Column(
            modifier
                .sizeIn(
                    minWidth = 240.dp,
                    maxWidth = drawerMaxWidth
                )
                .windowInsetsPadding(windowInsets),
            content = content
        )
    }
}


@Composable
private fun AccountList(accounts: List<Account>, onAccountSwitch: (Account) -> Unit) {
    val currentAccountId = CurrentAccountId
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        accounts.filter { it.id != currentAccountId }.forEach { account ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onAccountSwitch(account) }
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccountIconBox(Modifier.size(32.dp), account = account)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "上次同步：${account.updateAt?.formatAtRecent()}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}