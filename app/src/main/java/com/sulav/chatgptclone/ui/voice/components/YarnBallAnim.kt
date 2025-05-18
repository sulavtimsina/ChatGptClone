package com.sulav.chatgptclone.ui.voice.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.cos
import kotlin.math.sin

/**
 * Orbiting “loose yarn-ball” : 4 elliptical rings that continuously rotate.
 * @param speed 1 = slow (idle), 2-3 = fast (user talking)
 */
@Composable
fun YarnBallAnim(speed: Float) {
    /* angle 0-360 keeps spinning; the *speed* scales rotation duration */
    val angle by rememberInfiniteTransition(label = "yarn")
        .animateFloat(
            0f,
            360f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = (6000 / speed).toInt(), easing = LinearEasing)
            ),
            label = "angleAnim"
        )

    /* pre-build 4 paths so we only rotate & draw each frame */
    val paths = remember {
        List(4) { idx ->
            Path().apply {
                val phase = idx * 45             // offset each line a bit
                for (deg in 0..360 step 4) {
                    val rad = Math.toRadians((deg + phase).toDouble())
                    val x = cos(rad) * 1.0               // unit circle
                    val y = sin(rad) * 0.6               // squashed ellipse
                    if (deg == 0) moveTo(x.toFloat(), y.toFloat())
                    else lineTo(x.toFloat(), y.toFloat())
                }
                close()
            }
        }
    }

    Canvas(Modifier.fillMaxSize()) {
        val r = size.minDimension / 3
        val stroke = Stroke(width = r * 0.04f)
        paths.forEachIndexed { idx, path ->
            /* draw each path rotated by live *angle* plus its own offset */
            rotate(degrees = angle + idx * 90f, pivot = center) {
                withTransform({
                    translate(center.x, center.y)
                    scale(r, r)               // scale unit path to radius
                }) {
                    drawPath(path, Color.White, style = stroke, alpha = 0.85f)
                }
            }
        }
    }
}

@Preview
@Composable
fun YarnBallAnimPreview() {
    YarnBallAnim(2f)
}
