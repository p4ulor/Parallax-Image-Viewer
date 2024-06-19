package com.vluk4.parallax.model

import androidx.compose.runtime.Composable
import com.vluk4.example.parallax.model.ContentSettings

data class Content(
    val settings: ContentSettings = ContentSettings(),
    val composableContent: @Composable (() -> Unit)? = null
) {
    val composableContentOrNothing get() = composableContent ?: {}
}
