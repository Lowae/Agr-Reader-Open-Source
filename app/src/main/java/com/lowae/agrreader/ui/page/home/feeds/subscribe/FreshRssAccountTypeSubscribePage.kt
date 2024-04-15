package com.lowae.agrreader.ui.page.home.feeds.subscribe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.account.AccountType
import com.lowae.agrreader.ui.component.base.RYTextField

@Composable
fun FreshRssAccountTypeSubscribePage(
    subscribeUiState: SubscribeUiState,
    subscribeViewModel: SubscribeViewModel,
    navController: NavHostController
) {
    Icon(
        modifier = Modifier.size(32.dp),
        imageVector = Icons.Rounded.RssFeed,
        contentDescription = stringResource(R.string.subscribe),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        modifier = Modifier,
        text = stringResource(id = R.string.subscribe),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    RYTextField(
        readOnly = subscribeUiState.lockLinkInput,
        value = subscribeUiState.linkContent,
        singleLine = true,
        onValueChange = { subscribeViewModel.inputLink(it) },
        placeholder = stringResource(R.string.feed_or_site_url),
        errorMessage = subscribeUiState.errorMessage,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )

    TextButton(
        colors = ButtonDefaults.filledTonalButtonColors(),
        elevation = ButtonDefaults.buttonElevation(),
        enabled = subscribeUiState.lockLinkInput.not(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        onClick = {
            subscribeViewModel.subscribe(AccountType.FreshRSS)
            navController.popBackStack()
        }
    ) {
        Icon(
            modifier = Modifier
                .size(20.dp),
            imageVector = Icons.Outlined.RssFeed,
            contentDescription = stringResource(R.string.subscribe),
        )
        Text(
            modifier = Modifier.padding(horizontal = 6.dp),
            text = stringResource(R.string.subscribe),
            style = MaterialTheme.typography.titleMedium
        )
    }
}