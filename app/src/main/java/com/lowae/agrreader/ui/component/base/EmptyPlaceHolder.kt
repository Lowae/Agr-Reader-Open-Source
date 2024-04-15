package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.ui.svg.illustrations.Illustrations
import com.lowae.agrreader.ui.svg.illustrations.Reading

@Composable
fun EmptyPlaceHolder(modifier: Modifier = Modifier, tips: String? = null) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DynamicSVGImage(
            Modifier.width(356.dp),
            svgImageString = Illustrations.Reading
        )
        if (!tips.isNullOrEmpty()) {
            Text(text = tips, style = MaterialTheme.typography.labelLarge)
        }
    }
}