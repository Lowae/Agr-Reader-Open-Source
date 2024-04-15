package com.lowae.agrreader.ui.page.settings.color

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.BasicFontsPreference
import com.lowae.agrreader.data.model.preference.CustomPrimaryColorPreference
import com.lowae.agrreader.data.model.preference.DarkThemePreference
import com.lowae.agrreader.data.model.preference.DynamicColorPreference
import com.lowae.agrreader.data.model.preference.LocalBasicFonts
import com.lowae.agrreader.data.model.preference.LocalDarkTheme
import com.lowae.agrreader.data.model.preference.LocalDynamicColorTheme
import com.lowae.agrreader.data.model.preference.LocalFeedGroupExpandState
import com.lowae.agrreader.data.model.preference.LocalFeedLandscapeMode
import com.lowae.agrreader.data.model.preference.LocalPresetTheme
import com.lowae.agrreader.data.model.preference.LocalPrimaryColor
import com.lowae.agrreader.data.model.preference.ThemeIndexPreference
import com.lowae.agrreader.data.model.preference.not
import com.lowae.agrreader.ui.component.ColorPickerDialog
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.ui.component.base.RadioDialog
import com.lowae.agrreader.ui.component.base.RadioDialogOption
import com.lowae.agrreader.ui.component.base.Subtitle
import com.lowae.agrreader.ui.component.showProCheckDialog
import com.lowae.agrreader.ui.page.settings.SettingItem
import com.lowae.agrreader.ui.theme.palette.dynamic.BasicPalette
import com.lowae.agrreader.ui.theme.palette.dynamic.PresetColors
import com.lowae.agrreader.ui.theme.palette.dynamic.extractBasicPalettesV2
import com.lowae.agrreader.ui.theme.palette.toColorSchemeAdapter
import com.lowae.agrreader.utils.ext.ExternalFonts
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DisplayPage(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val darkTheme = LocalDarkTheme.current
    val isActivePro = false
    val dynamicColorTheme = LocalDynamicColorTheme.current
    val darkThemeNot = !darkTheme
    val localPresetColor = LocalPresetTheme.current
    val fonts = LocalBasicFonts.current
    val feedGroupExpandState = LocalFeedGroupExpandState.current
    val isLandscapeMode = LocalFeedLandscapeMode.current
    val scope = rememberCoroutineScope()

    val basicTonalPalettes = extractBasicPalettesV2()
    var darkThemeDialogVisible by remember { mutableStateOf(false) }
    var fontsDialogVisible by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.contentResolver.openInputStream(uri)?.use {
            ExternalFonts(context, ExternalFonts.FontType.BasicFont).copyToInternalStorage(it) {
                BasicFontsPreference.External.put(scope)
            }
        }
    }


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
                    Text(text = stringResource(R.string.display))
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
                    text = stringResource(R.string.preview),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Palettes(
                    palettes = basicTonalPalettes,
                    selected = { basicPalette -> localPresetColor == basicPalette.preset && dynamicColorTheme.value.not() },
                    onClick = { basicPalette ->
                        if (basicPalette.preset == PresetColors.CUSTOM) {
                            if (isActivePro.not()) {
                                showProCheckDialog(navController)
                            } else {
                                showColorPicker = showColorPicker.not()
                            }
                        } else {
                            if (basicPalette.preset == localPresetColor) return@Palettes
                            onThemeSelected(scope, basicPalette.preset)
                        }
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.appearance),
                )
                SettingItem(
                    title = stringResource(R.string.dynamic_color),
                    desc = stringResource(R.string.dynamic_color_summary),
                    icon = Icons.Outlined.ColorLens,
                    onClick = { dynamicColorTheme.not().put(scope) },
                ) {
                    RYSwitch(activated = dynamicColorTheme.value) {
                        dynamicColorTheme.not().put(scope)
                    }
                }

                SettingItem(
                    title = stringResource(R.string.dark_theme),
                    desc = darkTheme.toDesc(context),
                    icon = Icons.Outlined.ModeNight,
                    separatedActions = true,
                    onClick = {
                        darkThemeDialogVisible = true
                    },
                ) {
                    RYSwitch(
                        activated = darkTheme.isDarkTheme()
                    ) {
                        darkThemeNot.put(scope)
                    }
                }
                SettingItem(
                    title = stringResource(R.string.basic_fonts),
                    desc = fonts.toDesc(context),
                    icon = Icons.Outlined.FontDownload,
                    onClick = { fontsDialogVisible = true },
                ) {}
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "样式设置",
                )
                SettingItem(
                    title = "订阅列表页分组默认展开",
                    onClick = { feedGroupExpandState.not().put(scope) },
                ) {
                    RYSwitch(
                        activated = feedGroupExpandState.value
                    ) { feedGroupExpandState.not().put(scope) }
                }
                SettingItem(
                    title = "平板模式",
                    desc = "同时展示列表页与阅读页",
                    onClick = { isLandscapeMode.not().put(scope) },
                ) {
                    RYSwitch(
                        activated = isLandscapeMode.value
                    ) { isLandscapeMode.not().put(scope) }
                }

            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

    }

    RadioDialog(
        visible = darkThemeDialogVisible,
        title = stringResource(R.string.basic_fonts),
        options = DarkThemePreference.values.map {
            RadioDialogOption(
                text = it.toDesc(context),
                selected = it == darkTheme,
            ) {
                it.put(scope)
            }
        }
    ) {
        darkThemeDialogVisible = false
    }

    RadioDialog(
        visible = fontsDialogVisible,
        title = stringResource(R.string.basic_fonts),
        options = BasicFontsPreference.values.map {
            RadioDialogOption(
                text = it.toDesc(context),
                style = TextStyle(fontFamily = it.asFontFamily(context)),
                selected = it == fonts,
            ) {
                if (it.value == BasicFontsPreference.External.value) {
                    launcher.launch("*/*")
                } else {
                    it.put(scope)
                }
            }
        }
    ) {
        fontsDialogVisible = false
    }

    ColorPickerDialog(
        show = showColorPicker,
        initialColor = LocalPrimaryColor.current,
        onDismissRequest = { showColorPicker = false },
        type = ColorPickerType.Ring(showAlphaBar = false),
        onPickedColor = {
            onThemeSelected(scope, PresetColors.CUSTOM)
            CustomPrimaryColorPreference.put(scope, it.toArgb())
        },
    )
}

@Composable
fun Palettes(
    palettes: List<BasicPalette>,
    selected: (BasicPalette) -> Boolean,
    onClick: (BasicPalette) -> Unit,
) {
    if (palettes.isEmpty()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .clickable {},
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
                    stringResource(R.string.no_palettes)
                else stringResource(R.string.only_android_8_1_plus),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
        }
    } else {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            palettes.forEachIndexed { index, palette ->
                Column(
                    modifier = Modifier
                        .width(115.dp)
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppThemePreviewItem(
                        onClick = { onClick(palette) },
                        selected = selected(palette),
                        isCustom = palette.preset == PresetColors.CUSTOM,
                        colorScheme = palette.tonalPalettes.toColorSchemeAdapter(),
                    )
                }
            }
        }
    }
}

private fun onThemeSelected(scope: CoroutineScope, preset: PresetColors) {
    DynamicColorPreference.default.put(scope)
    ThemeIndexPreference.put(scope, preset)
}