package com.lowae.agrreader.ui.page.home.feeds.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.data.model.account.Account
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.ui.theme.palette.alwaysLight
import com.lowae.agrreader.utils.ext.CurrentAccountId

@Composable
fun AccountIconBox(modifier: Modifier, account: Account) {
    val isCurrent = account.id == CurrentAccountId
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (isCurrent) {
                    MaterialTheme.colorScheme.primaryContainer alwaysLight true
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer alwaysLight true
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        val icon = account.type.toIcon().takeIf { it is ImageVector }?.let { it as ImageVector }
        val iconPainter = account.type.toIcon().takeIf { it is Painter }?.let { it as Painter }
        if (icon != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = account.name,
                tint = MaterialTheme.colorScheme.onSurface alwaysLight true
            )
        } else {
            iconPainter?.let {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = it,
                    contentDescription = account.name,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun AccountIcon(accountType: AccountType, modifier: Modifier = Modifier) {
    val icon = accountType.toIcon().takeIf { it is ImageVector }?.let { it as ImageVector }
    val iconPainter = accountType.toIcon().takeIf { it is Painter }?.let { it as Painter }
    if (icon != null) {
        Icon(
            modifier = modifier,
            imageVector = icon,
            contentDescription = accountType.toDesc(AgrReaderApp.application),
            tint = Color.Unspecified
        )
    } else {
        iconPainter?.let {
            Icon(
                modifier = modifier,
                painter = it,
                contentDescription = accountType.toDesc(AgrReaderApp.application),
                tint = Color.Unspecified
            )
        }
    }

}
