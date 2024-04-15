package com.lowae.agrreader.ui.page.home.feeds.management

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.group.Group
import com.lowae.agrreader.data.model.group.GroupWithFeed
import com.lowae.agrreader.ui.component.base.AgrDialog
import com.lowae.agrreader.ui.theme.Shape20
import com.lowae.component.base.DraggableItem
import com.lowae.component.base.dragContainer
import com.lowae.component.base.rememberDragDropState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedManagementGroupSortDialog(
    visible: Boolean,
    groupWithFeeds: List<GroupWithFeed>,
    onDismiss: () -> Unit,
    onConfirm: (List<Group>) -> Unit,
) {
    val groups = remember(visible, groupWithFeeds) {
        groupWithFeeds.mapTo(SnapshotStateList()) { it.group }
    }
    AgrDialog(
        visible = visible,
        title = {
            Text(
                text = stringResource(id = R.string.group_sort),
            )
        },
        text = {

            val listState = rememberSaveable(groups, saver = LazyListState.Saver) {
                LazyListState(0, 0)
            }
            val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
                groups.add(toIndex, groups.removeAt(fromIndex))
            }
            LazyColumn(
                modifier = Modifier.dragContainer(dragDropState),
                state = listState,
            ) {
                itemsIndexed(
                    groups,
                    key = { index, item -> item.key }
                ) { index, item ->
                    DraggableItem(dragDropState, index) { isDragging ->
                        val elevation by animateDpAsState(
                            if (isDragging) 16.dp else 0.dp,
                            label = "FeedManagementGroupSortDialog"
                        )
                        Surface(
                            tonalElevation = elevation,
                            shadowElevation = elevation,
                            modifier = Modifier.clip(Shape20)
                        ) {
                            DraggableGroupItem(item.name)
                        }
                    }
                }
            }
        },
        onConfirm = { onConfirm(groups) },
        onDismiss = onDismiss,
    )
}

@Composable
private fun DraggableGroupItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            text = name,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Rounded.DragHandle, contentDescription = null)
    }
}