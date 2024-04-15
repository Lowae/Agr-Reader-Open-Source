package com.lowae.agrreader.ui.page.home.flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.data.model.preference.ArticleItemStylePreference
import com.lowae.agrreader.data.model.preference.FeedArticleSortByOldestPreference
import com.lowae.agrreader.data.model.preference.FeedArticleSortByOldestPreference.Companion.not
import com.lowae.agrreader.data.model.preference.LocalArticleItemStyle
import com.lowae.agrreader.data.model.preference.LocalArticleSortByOldest
import com.lowae.agrreader.ui.component.base.LWBottomSheet
import com.lowae.agrreader.ui.component.base.RYSwitch
import com.lowae.agrreader.ui.page.settings.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowPageStyleBottomDrawer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onArticleSortClick: (FeedArticleSortByOldestPreference) -> Unit
) {
    val itemStyle = LocalArticleItemStyle.current
    val sortByOldest = LocalArticleSortByOldest.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(true)

    var styleDropMenuVisible by remember {
        mutableStateOf(false)
    }
    LWBottomSheet(
        visible,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Text(
                text = "功能设置",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
            )
            SettingItem(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                title = "文章列表以时间倒序排列",
                titleStyle = MaterialTheme.typography.titleMedium,
            ) {
                RYSwitch(activated = sortByOldest.value) {
                    onArticleSortClick(sortByOldest.not())
                }
            }

            Text(
                text = "样式设置",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
            )

            SettingItem(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                title = "文章列表样式",
                titleStyle = MaterialTheme.typography.titleMedium,
                onClick = {
                    styleDropMenuVisible = true
                }
            ) {
                DropdownMenu(
                    expanded = styleDropMenuVisible,
                    onDismissRequest = { styleDropMenuVisible = false },
                    offset = DpOffset(0.dp, (-24).dp),
                ) {
                    ArticleItemStylePreference.values.forEach {
                        DropdownMenuItem(text = {
                            Text(text = it.toDesc())
                        }, onClick = {
                            it.put(scope)
                            styleDropMenuVisible = false
                        })
                    }
                }
                Row {
                    Text(text = itemStyle.toDesc())
                    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
                }
            }
        }
    }


}