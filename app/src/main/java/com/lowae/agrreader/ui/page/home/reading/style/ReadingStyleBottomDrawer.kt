package com.lowae.agrreader.ui.page.home.reading.style

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignJustify
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowae.agrreader.data.model.preference.LocalReadingLetterSpacing
import com.lowae.agrreader.data.model.preference.LocalReadingLineHeight
import com.lowae.agrreader.data.model.preference.LocalReadingLinkConfirm
import com.lowae.agrreader.data.model.preference.LocalReadingTextAlign
import com.lowae.agrreader.data.model.preference.LocalReadingTextFontSize
import com.lowae.agrreader.data.model.preference.LocalReadingTextFontWeight
import com.lowae.agrreader.data.model.preference.LocalReadingToolbarAutoHide
import com.lowae.agrreader.data.model.preference.ReadingLetterSpacingPreference
import com.lowae.agrreader.data.model.preference.ReadingLineHeightPreference
import com.lowae.agrreader.data.model.preference.ReadingLinkConfirmPreference.Companion.not
import com.lowae.agrreader.data.model.preference.ReadingTextAlignPreference
import com.lowae.agrreader.data.model.preference.ReadingTextFontSizePreference
import com.lowae.agrreader.data.model.preference.ReadingTextFontWeightPreference
import com.lowae.agrreader.data.model.preference.ReadingToolbarAutoHidePreference.Companion.not
import com.lowae.agrreader.ui.component.base.LWBottomSheet
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.utils.ext.roundClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingStyleBottomDrawer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val barrierWidth = (LocalConfiguration.current.screenWidthDp * 0.3f).toInt()
    val sheetState = rememberModalBottomSheetState(true)

    val autoHideToolbar = LocalReadingToolbarAutoHide.current
    val linkConfirm = LocalReadingLinkConfirm.current
    val textAlign = LocalReadingTextAlign.current.toTextAlign()
    val fontSize = LocalReadingTextFontSize.current
    val fontWeight = LocalReadingTextFontWeight.current
    val letterSpacing = LocalReadingLetterSpacing.current
    val lineHeight = LocalReadingLineHeight.current

    LWBottomSheet(
        visible,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {

            Text(
                text = "阅读设置",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
            )

            TitleSwitchOption(title = "滚动时是否隐藏操作栏", active = autoHideToolbar.value) {
                autoHideToolbar.not().put(scope)
            }

            TitleSwitchOption(title = "访问外部链接前进行确认", active = linkConfirm.value) {
                linkConfirm.not().put(scope)
            }

            Text(
                text = "文字设置",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OptionText(text = "文字对齐", modifier = Modifier.width(barrierWidth.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ReadingTextAlignPreference.values
                        .map(ReadingTextAlignPreference::toTextAlign)
                        .forEach { align ->
                            Icon(
                                imageVector = when (align) {
                                    TextAlign.Center -> Icons.Rounded.FormatAlignCenter
                                    TextAlign.End -> Icons.AutoMirrored.Rounded.FormatAlignRight
                                    TextAlign.Justify -> Icons.Rounded.FormatAlignJustify
                                    else -> Icons.AutoMirrored.Rounded.FormatAlignLeft
                                },
                                contentDescription = align.toString(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .roundClick(8.dp) {
                                        ReadingTextAlignPreference.put(scope, align)
                                    },
                                tint = if (textAlign == align) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                }
            }

            TextStyleSliderOption(
                title = "字号",
                gapWidthDp = barrierWidth,
                startingValue = fontSize.toFloat(),
                valueRange = 12f..30f,
                steps = 17,
                valueFormatter = { it.toInt().toString() }
            ) {
                ReadingTextFontSizePreference.put(scope, it.toInt())
            }

            TextStyleSliderOption(
                title = "字重",
                gapWidthDp = barrierWidth,
                startingValue = fontWeight.toFloat(),
                valueRange = 100f..900f,
                steps = 7,
                valueFormatter = { it.toInt().toString() }
            ) {
                ReadingTextFontWeightPreference.put(scope, it.toInt())
            }

            TextStyleSliderOption(
                title = "字距",
                gapWidthDp = barrierWidth,
                startingValue = letterSpacing.toFloat(),
                valueRange = 0f..6f,
                steps = 29,
                valueFormatter = { String.format("%.1f", it) }
            ) {
                ReadingLetterSpacingPreference.put(scope, it.toDouble())
            }

            TextStyleSliderOption(
                title = "行距",
                gapWidthDp = barrierWidth,
                startingValue = lineHeight.toFloat(),
                valueRange = 1f..5f,
                steps = 7
            ) {
                ReadingLineHeightPreference.put(scope, it.toDouble())
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    ReadingTextAlignPreference.default.put(scope)
                    ReadingTextFontSizePreference.put(scope, ReadingTextFontSizePreference.default)
                    ReadingTextFontWeightPreference.put(
                        scope,
                        ReadingTextFontWeightPreference.default
                    )
                    ReadingLetterSpacingPreference.put(
                        scope,
                        ReadingLetterSpacingPreference.default
                    )
                    ReadingLineHeightPreference.put(scope, ReadingLineHeightPreference.default)
                }) {
                Text(text = "重置为默认设置")
            }
        }
    }
}

@Composable
private fun OptionText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TextStyleSliderOption(
    title: String,
    gapWidthDp: Int,
    startingValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueFormatter: ((Float) -> String)? = null,
    onValueChange: (Float) -> Unit
) {
    var value by remember(startingValue) { mutableFloatStateOf(startingValue) }
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OptionText(text = title, modifier = Modifier.width(gapWidthDp.dp))
        Slider(
            modifier = Modifier.weight(1f),
            value = value, valueRange = valueRange, steps = steps,
            onValueChange = {
                value = it
                onValueChange(value)
            },
        )
        AnimatedContent(targetState = value, label = title, transitionSpec = {
            if (targetState > initialState) {
                slideInVertically(initialOffsetY = { it }) + fadeIn() with slideOutVertically(
                    targetOffsetY = { -it }) + fadeOut()
            } else {
                slideInVertically(initialOffsetY = { -it }) + fadeIn() with slideOutVertically(
                    targetOffsetY = { it }) + fadeOut()
            }.using(SizeTransform(clip = false))
        }) {
            Text(
                text = valueFormatter?.invoke(value) ?: value.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TitleSwitchOption(title: String, active: Boolean, onSwitchChange: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(modifier = Modifier.weight(1f), text = title)
        RYSwitch(activated = active, onClick = onSwitchChange)
    }
}