package com.lowae.agrreader.ui.component.base

import RYExtensibleVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DisplayText(
    modifier: Modifier = Modifier,
    text: String,
    desc: String,
) {
    Column(
        modifier = modifier
            .padding(
                start = 24.dp,
                end = 24.dp,
            )
    ) {
        Text(
            modifier = Modifier
                .height(44.dp),
//                .animateContentSize(tween()),
            text = text,
            style = MaterialTheme.typography.displaySmall.copy(
                baselineShift = BaselineShift.Superscript
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        RYExtensibleVisibility(visible = desc.isNotEmpty()) {
            Text(
                modifier = Modifier.height(16.dp),
                text = desc,
                style = MaterialTheme.typography.labelMedium.copy(
                    baselineShift = BaselineShift.Superscript
                ),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun RYTopBarTitle(
    modifier: Modifier = Modifier,
    text: String,
    desc: String,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        RYExtensibleVisibility(visible = desc.isNotEmpty()) {
            Text(
                modifier = Modifier.height(16.dp),
                text = desc,
                style = MaterialTheme.typography.labelMedium.copy(
                    baselineShift = BaselineShift.Superscript
                ),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    DisplayText(text = "Read You", desc = "dadjskakdsnjkasd")
}