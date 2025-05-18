package com.sulav.chatgptclone.ui.voice.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class VoiceIndicatorMode {
    YARN_BALL,
    EQUALIZER
}

@Composable
fun VoiceIndicator(
    mode: VoiceIndicatorMode,
    isActive: Boolean = false,
    modifier: Modifier = Modifier.size(120.dp),
    lineColor: Color = Color(0xFF4285F4),
    lineCount: Int = 4,
    onModeChange: ((VoiceIndicatorMode) -> Unit)? = null
) {
    // Animation progress from YARN_BALL to EQUALIZER (0f to 1f)
    val transitionProgress = remember { Animatable(0f) }

    // Base rotation speed and pulse size
    val baseRotationSpeed = 20f // degrees per second
    val basePulseSize = 0.4f

    // Animation values
    val pulseFactor = remember { Animatable(basePulseSize) }
    val rotationSpeed = remember { Animatable(baseRotationSpeed) }

    // Equalizer bar heights (for when in EQUALIZER mode)
    val equalizerHeights = remember {
        List(lineCount) { Animatable(0.5f + Random.nextFloat() * 0.5f) }
    }

    // Handle click to switch modes
    LaunchedEffect(mode) {
        println("!! mode changed to $mode")
        when (mode) {
            VoiceIndicatorMode.YARN_BALL -> transitionProgress.animateTo(
                0f,
                animationSpec = tween(700, easing = EaseInOutCubic)
            )

            VoiceIndicatorMode.EQUALIZER -> transitionProgress.animateTo(
                1f,
                animationSpec = tween(700, easing = EaseInOutCubic)
            )
        }
        // Notify listener about mode change
        onModeChange?.invoke(mode)
    }

    // Active state animations
    LaunchedEffect(isActive) {
        println("!! active state changed to $isActive")
        if (isActive) {
            // Increase pulse when active in YARN_BALL mode
            if (mode == VoiceIndicatorMode.YARN_BALL) {
                pulseFactor.animateTo(
                    targetValue = 1.05f,
                    animationSpec = repeatable(
                        animation = tween(500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                        iterations = 100
                    )
                )
                println("!! yarn ball animation speed increased")
                rotationSpeed.animateTo(
                    targetValue = baseRotationSpeed * 2.5f,
                    animationSpec = tween(800)
                )
            }

            // Animate equalizer bars when active in EQUALIZER mode
            if (mode == VoiceIndicatorMode.EQUALIZER) {
                equalizerHeights.forEachIndexed { index, animatable ->
                    launch {
                        while (isActive && mode == VoiceIndicatorMode.EQUALIZER) {
                            // Randomize target heights with rhythm based on index
                            val targetHeight = 0.2f + Random.nextFloat() * 0.8f
                            animatable.animateTo(
                                targetValue = targetHeight,
                                animationSpec = tween(
                                    durationMillis = 300 + (index * 50),
                                    easing = FastOutSlowInEasing
                                )
                            )
                            delay(50L)
                        }
                    }
                }
            }
        } else {
            // Reset to base values
            pulseFactor.animateTo(basePulseSize)
            rotationSpeed.animateTo(baseRotationSpeed)

            // Reset equalizer bars to medium height when inactive
            equalizerHeights.forEach { animatable ->
                animatable.animateTo(0.5f, animationSpec = tween(500))
            }
        }
    }

    // Base rotation animates continuously
    val infiniteRotation = rememberInfiniteTransition()
    val angle by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = remember(rotationSpeed.value) {
                    (60000 / rotationSpeed.value).toInt()
                },
                easing = LinearEasing
            )
        )
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension / 2) * pulseFactor.value

            // Draw based on current mode with transition
            for (i in 0 until lineCount) {
                // Each line has a different base angle and orbit radius
                val lineBaseAngle = (360f / lineCount) * i
                val orbitRadius = radius * (0.7f + 0.3f * (i % 2))

                // YARN BALL MODE CALCULATIONS
                // Line endpoints for yarn ball mode
                val ballStartOffsetX =
                    cos(Math.toRadians((angle + lineBaseAngle).toDouble())).toFloat() * orbitRadius
                val ballStartOffsetY =
                    sin(Math.toRadians((angle + lineBaseAngle).toDouble())).toFloat() * orbitRadius

                val ballEndOffsetX =
                    cos(Math.toRadians((angle + lineBaseAngle + 120).toDouble())).toFloat() * orbitRadius
                val ballEndOffsetY =
                    sin(Math.toRadians((angle + lineBaseAngle + 120).toDouble())).toFloat() * orbitRadius

                // Calculate curve control point (to make lines curved)
                val ballControlOffsetX =
                    cos(Math.toRadians((angle + lineBaseAngle + 60).toDouble())).toFloat() * orbitRadius * 1.3f
                val ballControlOffsetY =
                    sin(Math.toRadians((angle + lineBaseAngle + 60).toDouble())).toFloat() * orbitRadius * 1.3f

                // EQUALIZER MODE CALCULATIONS
                // Equalizer line positions - horizontal across the width
                val equalizerHeight = size.height * equalizerHeights[i].value
                val equalizerY =
                    center.y - (equalizerHeight / 2) + (size.height * 0.1f) // Slightly move up from center
                val equalizerSegmentWidth = size.width / lineCount
                val equalizerStartX = i * equalizerSegmentWidth + equalizerSegmentWidth * 0.2f
                val equalizerEndX = equalizerStartX + equalizerSegmentWidth * 0.6f

                // TRANSITION BETWEEN MODES
                // Interpolate positions based on transition progress
                val lineStart = Offset(
                    x = lerp(
                        center.x + ballStartOffsetX,
                        equalizerStartX,
                        transitionProgress.value
                    ),
                    y = lerp(center.y + ballStartOffsetY, equalizerY, transitionProgress.value)
                )

                val lineControl = Offset(
                    x = lerp(
                        center.x + ballControlOffsetX,
                        (equalizerStartX + equalizerEndX) / 2,
                        transitionProgress.value
                    ),
                    y = lerp(center.y + ballControlOffsetY, equalizerY, transitionProgress.value)
                )

                val lineEnd = Offset(
                    x = lerp(center.x + ballEndOffsetX, equalizerEndX, transitionProgress.value),
                    y = lerp(center.y + ballEndOffsetY, equalizerY, transitionProgress.value)
                )

                // Opacity and width calculations
                val lineOpacity =
                    0.7f + 0.3f * sin(Math.toRadians((angle * 2 + lineBaseAngle * 3).toDouble())).toFloat()
                val lineWidth = 3.dp.toPx() * (0.8f + 0.2f * lineOpacity)

                // Draw the line with transition from curved to straight
                val curveFactor = 1f - transitionProgress.value

                if (curveFactor > 0) {
                    // Draw with some curve (for yarn ball mode or during transition)
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(lineStart.x, lineStart.y)
                            // Reduce the control point influence during transition
                            val adjustedControl = Offset(
                                x = lineStart.x + (lineControl.x - lineStart.x) * curveFactor,
                                y = lineStart.y + (lineControl.y - lineStart.y) * curveFactor
                            )
                            quadraticBezierTo(
                                adjustedControl.x,
                                adjustedControl.y,
                                lineEnd.x,
                                lineEnd.y
                            )
                        },
                        color = lineColor.copy(alpha = lineOpacity),
                        style = Stroke(width = lineWidth, cap = StrokeCap.Round)
                    )
                } else {
                    // Draw straight line (for equalizer mode)
                    drawLine(
                        color = lineColor.copy(alpha = lineOpacity),
                        start = lineStart,
                        end = lineEnd,
                        strokeWidth = lineWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

// Linear interpolation helper function
private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

@Preview(showBackground = true)
@Composable
fun VoiceIndicatorPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var isActive by remember { mutableStateOf(false) }
        var mode by remember { mutableStateOf(VoiceIndicatorMode.YARN_BALL) }

        // Toggle active state every 3 seconds for demo
        LaunchedEffect(Unit) {
            while (true) {
                isActive = !isActive
                if (mode == VoiceIndicatorMode.YARN_BALL) mode =
                    VoiceIndicatorMode.EQUALIZER else mode = VoiceIndicatorMode.YARN_BALL
                delay(3000)
            }
        }

        VoiceIndicator(
            isActive = isActive,
            modifier = Modifier.fillMaxWidth(0.8f),
            onModeChange = { mode = it },
            mode = VoiceIndicatorMode.YARN_BALL
        )
    }
}