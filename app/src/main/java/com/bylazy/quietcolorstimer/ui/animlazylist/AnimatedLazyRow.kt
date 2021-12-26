package com.bylazy.quietcolorstimer.ui.animlazylist

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers

@ExperimentalAnimationApi
@Composable
fun <T> AnimatedLazyRow(
    state: LazyListState,
    modifier: Modifier = Modifier,
    items: List<AnimatedLazyListItem<T>>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    animationDuration: Int = 400,
    initialEnter: EnterTransition = fadeIn(),
    enter: EnterTransition = fadeIn(
        animationSpec = tween(delayMillis = animationDuration / 3),
    ) + expandHorizontally(
        animationSpec = tween(durationMillis = animationDuration),
        expandFrom = if (reverseLayout) Alignment.End else Alignment.Start
    ),
    exit: ExitTransition = fadeOut() + shrinkHorizontally(
        animationSpec = tween(durationMillis = animationDuration),
        shrinkTowards = if (reverseLayout) Alignment.Start else Alignment.End
    ),
    finalExit: ExitTransition = exit
) {
    val scope = rememberCoroutineScope { Dispatchers.Main }
    val viewModel =
        remember { AnimatedLazyListViewModel<T>(scope, animationDuration, reverseLayout) }
    viewModel.updateList(items)
    val currentItems by viewModel.items.collectAsState(emptyList())

    LazyRow(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        content = animatedLazyListScope(
            currentItems = currentItems,
            initialEnter = initialEnter,
            enter = enter,
            exit = exit,
            finalExit = finalExit,
            isVertical = false,
            spacing = horizontalArrangement.spacing
        )
    )
}