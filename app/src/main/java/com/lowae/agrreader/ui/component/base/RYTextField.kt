package com.lowae.agrreader.ui.component.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.lowae.agrreader.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RYTextField(
    readOnly: Boolean,
    value: TextFieldValue,
    label: String = "",
    singleLine: Boolean = true,
    onValueChange: (TextFieldValue) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    placeholder: String = "",
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    val clipboardManager = LocalClipboardManager.current
    val focusRequester = remember { FocusRequester() }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)  // ???
        focusRequester.requestFocus()
    }

    TextField(
        modifier = Modifier.focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
        ),
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        enabled = !readOnly,
        value = value,
        label = if (label.isEmpty()) null else {
            { Text(label) }
        },
        onValueChange = {
            if (!readOnly) {
                onValueChange(it)
            }
        },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else visualTransformation,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        isError = errorMessage.isNotEmpty(),
        supportingText = {
            if (errorMessage.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = singleLine,
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                IconButton(onClick = {
                    if (isPassword) {
                        showPassword = !showPassword
                    } else if (!readOnly) {
                        onValueChange(TextFieldValue())
                    }
                }) {
                    Icon(
                        imageVector = if (isPassword) {
                            if (showPassword) Icons.Rounded.Visibility
                            else Icons.Rounded.VisibilityOff
                        } else Icons.Rounded.Close,
                        contentDescription = if (isPassword) stringResource(R.string.password) else stringResource(
                            R.string.clear
                        ),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                }
            } else {
                IconButton(onClick = {
                    val clipText = clipboardManager.getText()?.text ?: ""
                    onValueChange(TextFieldValue(clipText, TextRange(clipText.length)))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ContentPaste,
                        contentDescription = stringResource(R.string.paste),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
fun RYTextField(
    readOnly: Boolean,
    value: String,
    label: String = "",
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    placeholder: String = "",
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    RYTextField(
        readOnly,
        textFieldValue,
        label,
        singleLine,
        {
            textFieldValue = it
            onValueChange(it.text)
        },
        visualTransformation,
        isPassword,
        placeholder,
        errorMessage, keyboardOptions, keyboardActions
    )
}
