package com.lowae.agrreader.utils.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.roundClick(corner: Dp = 8.dp, onClick: () -> Unit = {}) = this
    .clip(RoundedCornerShape(corner))
    .clickable(onClick = onClick)