package com.sulav.chatgptclone.voice.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.min

@Composable
fun WaveformAnim(amplitude: Float) {
    val amp = remember { Animatable(0f) }
    LaunchedEffect(amplitude) {
        amp.animateTo(amplitude, animationSpec = tween(80))
    }
    Canvas(Modifier.fillMaxSize()) {
        val midY = center.y
        val width = size.width
        val step = width / 40
        var x = 0f
        while (x < width) {
            val h = min(amp.value / 10f, 1f) * size.height / 2
            drawLine(
                Color.White,
                start = Offset(x, midY - h),
                end = Offset(x, midY + h),
                strokeWidth = 6f
            )
            x += step
        }
    }
}
