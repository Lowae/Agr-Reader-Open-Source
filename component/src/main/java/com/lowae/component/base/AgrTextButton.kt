package com.lowae.component.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun AgrTextButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .then(modifier),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp)
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(text = text, style = textStyle)
    }
}