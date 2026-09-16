package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LauncherThemeMode
import com.example.model.EssentialAppInfo

@Composable
fun LiquidGlassBackground(
    themeMode: LauncherThemeMode,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isOled = themeMode == LauncherThemeMode.OLED
    val isLight = themeMode == LauncherThemeMode.LIGHT

    val infiniteTransition = rememberInfiniteTransition(label = "LiquidBreathing")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val shiftX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX"
    )

    val shiftY by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                when (themeMode) {
                    LauncherThemeMode.OLED -> Color.Black
                    LauncherThemeMode.LIGHT -> Color(0xFFF3F5F9)
                    LauncherThemeMode.DUSK -> Color(0xFF090E17)
                    LauncherThemeMode.DARK -> Color(0xFF06070B)
                    LauncherThemeMode.SYSTEM -> MaterialTheme.colorScheme.background
                }
            )
    ) {
        if (!isOled) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                if (isLight) {
                    // Soft pastel liquid lights
                    drawCircle(
                        color = Color(0x30A0C4FF).copy(alpha = pulseAlpha * 0.4f),
                        radius = canvasWidth * 0.65f,
                        center = Offset(canvasWidth * 0.2f + shiftX, canvasHeight * 0.25f + shiftY)
                    )
                    drawCircle(
                        color = Color(0x28BDB2FF).copy(alpha = pulseAlpha * 0.3f),
                        radius = canvasWidth * 0.55f,
                        center = Offset(canvasWidth * 0.85f - shiftX, canvasHeight * 0.65f - shiftY)
                    )
                } else {
                    // Deep liquid glass ambiance
                    drawCircle(
                        color = Color(0xFF0A2239).copy(alpha = pulseAlpha * 0.65f),
                        radius = canvasWidth * 0.7f,
                        center = Offset(canvasWidth * 0.15f + shiftX, canvasHeight * 0.25f + shiftY)
                    )
                    drawCircle(
                        color = Color(0xFF1B1B3A).copy(alpha = pulseAlpha * 0.5f),
                        radius = canvasWidth * 0.6f,
                        center = Offset(canvasWidth * 0.85f - shiftX, canvasHeight * 0.7f - shiftY)
                    )
                    drawCircle(
                        color = Color(0xFF05384B).copy(alpha = pulseAlpha * 0.35f),
                        radius = canvasWidth * 0.45f,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.9f + shiftX)
                    )
                }
            }
        }

        content()
    }
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "cardScale"
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            borderColor.copy(alpha = 0.45f),
            borderColor.copy(alpha = 0.10f),
            borderColor.copy(alpha = 0.30f)
        )
    )

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.2.dp, borderBrush), shape)
            .then(clickableModifier)
    ) {
        // Subtle top specular rim highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun EssentialAppGlassCard(
    app: EssentialAppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "appCardScale"
    )

    val accentColor = Color(app.accentColorHex)

    val cardBorder = Brush.linearGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.15f),
            accentColor.copy(alpha = 0.25f)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("app_card_${app.type.name.lowercase()}"),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.2.dp, cardBorder),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Liquid Icon Container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.35f),
                                accentColor.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)),
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = app.iconVector,
                    contentDescription = app.title,
                    tint = if (app.isAvailable) accentColor else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Availability Dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (app.isAvailable) accentColor else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (app.isAvailable) app.subtitle else "Not Installed",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = if (app.isAvailable) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            Color.Gray
                        }
                    )
                }
            }

            // Glass forward arrow badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "Open ${app.title}",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun LiquidQuickTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean = false,
    activeColor: Color = Color(0xFF64D2FF),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "tileScale"
    )

    val backgroundBrush = if (isActive) {
        Brush.linearGradient(
            listOf(
                activeColor.copy(alpha = 0.35f),
                activeColor.copy(alpha = 0.15f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.04f)
            )
        )
    }

    val borderBrush = if (isActive) {
        Brush.linearGradient(
            listOf(
                activeColor.copy(alpha = 0.8f),
                activeColor.copy(alpha = 0.3f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundBrush)
            .border(BorderStroke(1.2.dp, borderBrush), RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
            .testTag("quick_tile_${title.lowercase().replace(" ", "_")}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) activeColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isActive) activeColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isActive) activeColor else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LiquidPillButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.10f),
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "pillScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            borderColor.copy(alpha = 0.4f),
                            borderColor.copy(alpha = 0.1f)
                        )
                    )
                ),
                RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}
