package com.lowae.agrreader.ui.page.home.flow

import android.view.SoundEffectConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowae.agrreader.data.model.general.Filter
import com.lowae.agrreader.data.model.general.iconFilled
import com.lowae.agrreader.data.model.general.iconOutline
import com.lowae.agrreader.data.model.general.title
import com.lowae.agrreader.ui.component.Border
import com.lowae.agrreader.ui.component.border
import com.lowae.agrreader.utils.ext.surfaceColorAtElevation
import com.lowae.component.constant.ElevationTokens
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowBottomBar(
    scrollBehavior: TopAppBarScrollBehavior,
    filter: Filter,
    filterOnClick: (Filter) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(BottomAppBarDefaults.windowInsets)
            .height(60.dp * (scrollBehavior.state.collapsedFraction - 1.0F).absoluteValue)
            .background(color = NavigationBarDefaults.containerColor)
            .border(
                top = Border(
                    1.dp,
                    MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level2.dp)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Filter.entries.forEach { item ->
                    FlowBottomItem(
                        item,
                        item == filter,
                        filterOnClick
                    )
                }
            }
        )
    }
}

@Composable
private fun FlowBottomItem(filter: Filter, isSelected: Boolean, onFilterClick: (Filter) -> Unit) {
    val view = LocalView.current
    val filterName = filter.title
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onFilterClick(filter)
            }
            .animateContentSize(),
        shape = RoundedCornerShape(32.dp),
        tonalElevation = if (isSelected) 2.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(horizontal = 4.dp),
                imageVector = if (isSelected) filter.iconFilled else filter.iconOutline,
                contentDescription = filterName
            )
            if (isSelected) {
                Text(
                    text = filterName.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}