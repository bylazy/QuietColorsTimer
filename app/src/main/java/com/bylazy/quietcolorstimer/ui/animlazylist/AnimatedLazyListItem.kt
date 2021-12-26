package com.bylazy.quietcolorstimer.ui.animlazylist

import androidx.compose.runtime.Composable

data class AnimatedLazyListItem<out T>(
    val key: String,
    val value: T? = null,
    val composable: @Composable () -> Unit
)