package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowae.agrreader.ui.theme.palette.LocalTonalPalettes

// TODO: ripple & swipe
@Composable
fun RYSwitch(
    modifier: Modifier = Modifier,
    activated: Boolean,
    enable: Boolean = true,
    onClick: ((Boolean) -> Unit)? = null,
) {
    Switch(modifier = modifier, enabled = enable, checked = activated, onCheckedChange = {
        onClick?.invoke(activated)
    })
}

// TODO: inactivated colors
@Composable
fun SwitchHeadline(
    activated: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
) {
    val tonalPalettes = LocalTonalPalettes.current

    Surface(
        modifier = modifier,
        color = Color.Unspecified,
        contentColor = tonalPalettes neutral 10,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(tonalPalettes primary 90)
                .clickable { onClick() }
                .padding(20.dp, 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                )
            }
            Box(Modifier.padding(start = 20.dp)) {
                RYSwitch(activated = activated)
            }
        }
    }
}
