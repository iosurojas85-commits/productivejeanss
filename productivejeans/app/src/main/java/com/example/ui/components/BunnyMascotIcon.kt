package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BunnyMascotIcon(
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(500),
        label = "bunny_mascot_tint"
    )

    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {
        val w = size.width
        val h = size.height

        // Path for the U-shaped pillow mascot face
        val mascotPath = Path().apply {
            // Start at top center notch between ears
            moveTo(w * 0.50f, h * 0.42f)
            // Curve up to top of left ear
            cubicTo(w * 0.38f, h * 0.38f, w * 0.32f, h * 0.04f, w * 0.20f, h * 0.04f)
            // Left ear top dome
            cubicTo(w * 0.08f, h * 0.04f, w * 0.04f, h * 0.22f, w * 0.05f, h * 0.45f)
            // Left outer cheek down to bottom chin
            cubicTo(w * 0.05f, h * 0.75f, w * 0.20f, h * 0.95f, w * 0.50f, h * 0.95f)
            // Right outer cheek up from bottom chin
            cubicTo(w * 0.80f, h * 0.95f, w * 0.95f, h * 0.75f, w * 0.95f, h * 0.45f)
            // Right ear top dome
            cubicTo(w * 0.96f, h * 0.22f, w * 0.92f, h * 0.04f, w * 0.80f, h * 0.04f)
            // Curve down to top center notch
            cubicTo(w * 0.68f, h * 0.04f, w * 0.62f, h * 0.38f, w * 0.50f, h * 0.42f)
            close()
        }

        // Fill body with member color + soft 3D highlight
        val bodyBrush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.75f),
                animatedColor,
                animatedColor.copy(alpha = 0.85f)
            ),
            center = Offset(w * 0.5f, h * 0.35f),
            radius = w * 0.75f
        )
        drawPath(path = mascotPath, brush = bodyBrush)

        // Dark outline for contrast
        drawPath(
            path = mascotPath,
            color = Color.Black.copy(alpha = 0.25f),
            style = Stroke(width = w * 0.035f)
        )

        // Two glossy dark eyes
        val eyeRadius = w * 0.085f
        val leftEyeCenter = Offset(w * 0.33f, h * 0.64f)
        val rightEyeCenter = Offset(w * 0.67f, h * 0.64f)
        val eyeColor = Color(0xFF222226)

        // Left eye
        drawCircle(
            color = eyeColor,
            radius = eyeRadius,
            center = leftEyeCenter
        )
        // Right eye
        drawCircle(
            color = eyeColor,
            radius = eyeRadius,
            center = rightEyeCenter
        )

        // Specular eye highlights
        val highlightRadius = eyeRadius * 0.35f
        val leftHighlightCenter = Offset(w * 0.31f, h * 0.62f)
        val rightHighlightCenter = Offset(w * 0.65f, h * 0.62f)

        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = highlightRadius,
            center = leftHighlightCenter
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = highlightRadius,
            center = rightHighlightCenter
        )
    }
}
