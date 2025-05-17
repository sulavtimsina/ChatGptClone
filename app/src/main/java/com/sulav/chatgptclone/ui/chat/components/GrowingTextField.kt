package com.sulav.chatgptclone.ui.chat.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun GrowingTextField(
    text: String,
    onTextChange: (String) -> Unit,
    maxLines: Int = 10,
    focusRequester: FocusRequester
) {
    val scrollState = rememberScrollState()
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurface),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .verticalScroll(scrollState),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        maxLines = maxLines
    )
}
