package com.example.myprofileapp.ui.theme.components.orders

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
fun Modifier.shimmerLoadingAnimation(
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000
): Modifier = composed {

    val transition = rememberInfiniteTransition(label = "shimmerTransition")

    val xShimmer = transition.animateFloat(
        initialValue = 0f,
        targetValue = widthOfShadowBrush.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis),
            repeatMode = RepeatMode.Restart
        ), label = "xShimmer"
    ).value

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(xShimmer - widthOfShadowBrush, xShimmer),
        end = Offset(xShimmer, xShimmer + widthOfShadowBrush),
        tileMode = TileMode.Clamp
    )

    this.background(shimmerBrush)
}

fun Modifier.shimmerPlaceholder(
    width: Float,
    height: Float,
    cornerRadius: RoundedCornerShape = RoundedCornerShape(4.dp)
): Modifier = this
    .background(Color.LightGray, cornerRadius)
    .shimmerLoadingAnimation()
