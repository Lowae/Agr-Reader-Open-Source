package com.lowae.agrreader.ui.component.base

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.utils.tap

@Composable
fun <T : FloatingActionButtonItem> ExpandableFloatingActionButton(
    state: ExpandableFloatingActionButtonState = rememberExpandableFloatingActionButtonState(),
    items: List<T>,
    onStateChange: () -> Boolean,
    expandContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit
) {
    val view = LocalView.current
    Column(
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
            visible = state.currentState == SpeedDialState.EXPANDED,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(horizontalAlignment = Alignment.End) {
                items.forEach {
                    it.content(it, state.currentState)
                }
            }
        }
        FloatingActionButton(onClick = {
            view.tap()
            if (onStateChange().not()) {
                state.stateChange()
            }
        }) {
            AnimatedContent(targetState = state.currentState, label = "") {
                if (it == SpeedDialState.EXPANDED) {
                    expandContent()
                } else {
                    collapsedContent()
                }
            }
        }
    }
}

@Composable
fun rememberExpandableFloatingActionButtonState(): ExpandableFloatingActionButtonState {
    val state = remember { ExpandableFloatingActionButtonState() }
    state.transition = updateTransition(
        targetState = state.currentState,
        label = "ExpandableFloatingActionButtonState"
    )
    return state
}

class ExpandableFloatingActionButtonState {
    var currentState by mutableStateOf(SpeedDialState.COLLAPSED)

    var transition: Transition<SpeedDialState>? = null

    val stateChange: () -> Unit = {
        currentState = if (transition?.currentState == SpeedDialState.EXPANDED ||
            transition?.isRunning == true && transition?.targetState == SpeedDialState.EXPANDED
        ) {
            SpeedDialState.COLLAPSED
        } else {
            SpeedDialState.EXPANDED
        }
    }
}

enum class SpeedDialState {
    COLLAPSED,
    EXPANDED
}

open class FloatingActionButtonItem(
    val icon: ImageVector,
    val label: String,
    val onFabItemClicked: () -> Unit,
    val content: @Composable (FloatingActionButtonItem, SpeedDialState) -> Unit = { floatingActionButtonItem: FloatingActionButtonItem, speedDialState: SpeedDialState ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.0.dp),
                shadowElevation = 2.dp,
                onClick = { floatingActionButtonItem.onFabItemClicked() }
            ) {
                Text(
                    color = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
                    text = floatingActionButtonItem.label,
                    modifier = Modifier
                        .padding(
                            start = 6.dp,
                            end = 6.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                )
            }
            SmallFloatingActionButton(
                modifier = Modifier
                    .padding(4.dp),

                onClick = { floatingActionButtonItem.onFabItemClicked() },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    hoveredElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = floatingActionButtonItem.icon,
                    contentDescription = floatingActionButtonItem.label
                )
            }
        }
    }
)

