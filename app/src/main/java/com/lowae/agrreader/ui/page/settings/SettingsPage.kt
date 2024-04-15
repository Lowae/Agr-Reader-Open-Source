package com.lowae.agrreader.ui.page.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.rounded.AddReaction
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwipeVertical
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.lowae.agrreader.BuildConfig
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.LocalNewVersionNumber
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.Banner
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.page.common.BackupSettingRouter
import com.lowae.agrreader.ui.page.common.InteractiveSettingRouter
import com.lowae.agrreader.ui.page.common.NavigationAndFeedBackRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.utils.compat.PackageManagerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavHostController) {
    val newVersionInfo = LocalNewVersionNumber.current
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var upgradeDialogVisible by rememberSaveable { mutableStateOf(false) }

    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
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
            if (newVersionInfo.newVersionCode > PackageManagerCompat.versionCode) {
                item {
                    Banner(
                        modifier = Modifier.zIndex(1f),
                        title = stringResource(
                            R.string.get_new_updates_desc,
                            versionCodeToName(newVersionInfo.newVersionCode)
                        ),
                        icon = Icons.Outlined.RocketLaunch,
                    ) {
                        upgradeDialogVisible = true
                    }
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.universal),
                    desc = stringResource(R.string.universal_desc),
                    icon = Icons.Rounded.Settings,
                ) {
                    navController.navigate(RouteName.UNIVERSAL_SETTINGS) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.display),
                    desc = stringResource(R.string.display_summary),
                    icon = Icons.Rounded.ColorLens,
                ) {
                    navController.navigate(RouteName.DISPLAY_SETTINGS) {
                        launchSingleTop = true
                    }
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.interaction),
                    desc = stringResource(R.string.interaction_desc),
                    icon = Icons.Rounded.SwipeVertical,
                ) {
                    InteractiveSettingRouter.navigate(navController)
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.backup_and_restore),
                    desc = stringResource(R.string.backup_and_restore_desc),
                    icon = Icons.Rounded.Backup,
                ) {
                    BackupSettingRouter.navigate(navController)
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.agr_settings_navigation_and_feedback),
                    desc = stringResource(R.string.agr_settings_navigation_and_feedback_desc),
                    icon = Icons.Rounded.Navigation,
                ) {
                    NavigationAndFeedBackRouter.navigate(navController)
                }
            }

            item {
                SelectableSettingGroupItem(
                    title = stringResource(R.string.about),
                    desc = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                    icon = Icons.Rounded.AddReaction,
                ) {
                    navController.navigate(RouteName.ABOUT_SETTINGS)
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }

    UpgradeVersionDialog(visible = upgradeDialogVisible) {
        upgradeDialogVisible = false
    }
}

private fun versionCodeToName(versionCode: Long): String {
    val major = versionCode / 1000000 % 1000
    val minor = versionCode / 1000 % 1000
    val revision = versionCode % 1000
    return String.format("%d.%d.%d", major, minor, revision)
}
