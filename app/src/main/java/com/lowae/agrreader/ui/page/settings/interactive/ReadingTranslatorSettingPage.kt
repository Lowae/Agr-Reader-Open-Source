package com.lowae.agrreader.ui.page.settings.interactive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.data.model.preference.LocalTranslationOptions
import com.lowae.agrreader.data.model.preference.TranslationOption
import com.lowae.agrreader.data.model.preference.TranslationOptionsPreference
import com.lowae.agrreader.ui.component.base.AgrScaffold
import com.lowae.agrreader.ui.component.base.FeedbackIconButton
import com.lowae.agrreader.ui.component.showProCheckDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingTranslatorSettingPage(navController: NavHostController) {
    val translationOption = LocalTranslationOptions.current.value
    val isActivePro = false
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    AgrScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(R.string.interactive_translation_setting_title))
                },
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(TranslationOption.entries) { item ->
                TranslatorItem(item, translationOption) {
                    if (isActivePro.not() && item != TranslationOption.GOOGLE_FREE) {
                        showProCheckDialog(navController)
                    } else {
                        TranslationOptionsPreference(item).put(scope)
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslatorItem(
    item: TranslationOption,
    translationOption: TranslationOption,
    onClick: (TranslationOption) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(if (item == translationOption) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
            .clickable {
                onClick(translationOption)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = translationOption == item,
            onClick = {
                onClick(translationOption)
            })
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = item.title,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}