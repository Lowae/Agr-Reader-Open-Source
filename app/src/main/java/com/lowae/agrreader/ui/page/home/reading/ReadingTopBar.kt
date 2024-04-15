package com.lowae.agrreader.ui.page.home.reading

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.utils.ext.openURL
import com.lowae.agrreader.utils.ext.share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    link: String = "",
    onClose: () -> Unit = {},
    onStyleSettingClick: () -> Unit = {}
) {
    var menuVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            FeedbackIconButton(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface
            ) {
                onClose()
            }
        },
        actions = {
            FeedbackIconButton(
                modifier = Modifier.size(22.dp),
                imageVector = Icons.Rounded.FormatSize,
                contentDescription = stringResource(R.string.style),
                tint = MaterialTheme.colorScheme.onSurface,
                onClick = onStyleSettingClick
            )
            FeedbackIconButton(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(R.string.more),
                tint = MaterialTheme.colorScheme.onSurface,
            ) {
                menuVisible = true
            }
            DropdownMenu(
                expanded = menuVisible,
                onDismissRequest = { menuVisible = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        menuVisible = false
                        context.share(title.takeIf { it.isNotBlank() } ?: link, link)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share),
                        )
                    },
                    text = {
                        Text(stringResource(id = R.string.share))
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        menuVisible = false
                        context.openURL(link)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.OpenInBrowser,
                            contentDescription = stringResource(R.string.open_in_browser),
                        )
                    },
                    text = {
                        Text(stringResource(R.string.open_in_browser))
                    },
                )
            }
        }
    )
}
