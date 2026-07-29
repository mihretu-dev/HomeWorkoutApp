package com.base.androidstartertemplate.ui.components

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.base.androidstartertemplate.data.model.Exercise
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ExerciseLottiePlayer(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    height: Dp = 220.dp,
    isCompact: Boolean = false
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var isLoadError by remember { mutableStateOf(false) }

    val containerModifier = if (isCompact) {
        modifier.size(56.dp)
    } else {
        modifier
            .fillMaxWidth()
            .height(height)
    }

    Surface(
        modifier = containerModifier,
        shape = RoundedCornerShape(if (isCompact) 12.dp else 18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            if (isCompact) 1.dp else 1.5.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!isLoadError) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(exercise.getGifAssetPath())
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(if (isCompact) 12.dp else 18.dp)),
                    onError = {
                        isLoadError = true
                    },
                    loading = {
                        ExerciseComposeAnimator(
                            exerciseId = exercise.id,
                            isPlaying = isPlaying,
                            speed = speed,
                            isCompact = isCompact,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (isCompact) 4.dp else 12.dp)
                        )
                    }
                )
            } else {
                ExerciseComposeAnimator(
                    exerciseId = exercise.id,
                    isPlaying = isPlaying,
                    speed = speed,
                    isCompact = isCompact,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isCompact) 4.dp else 12.dp)
                )
            }

            if (!isCompact) {
                // Interactive Control Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        // Play/Pause Toggle Button
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause Animation" else "Play Animation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Speed Selector Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    speed = when (speed) {
                                        0.5f -> 1.0f
                                        1.0f -> 1.5f
                                        else -> 0.5f
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${speed}x",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseComposeAnimator(
    exerciseId: String,
    isPlaying: Boolean,
    speed: Float,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val duration = (1200 / speed).toInt().coerceAtLeast(300)
    val infiniteTransition = rememberInfiniteTransition(label = "ExerciseAnimation")
    
    val rawProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Progress"
    )

    val progress = if (isPlaying) rawProgress else 0.5f

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val scaleFactor = if (isCompact) 0.55f else 1.0f
        val strokeWidth = 10f * scaleFactor
        val headRadius = 18f * scaleFactor

        when {
            // Push-Ups Variations
            exerciseId.contains("push_up") -> {
                val bodyY = cy + (progress - 0.5f) * (40f * scaleFactor)
                val floorY = cy + (40f * scaleFactor)

                // Floor Mat
                drawRoundRect(
                    color = surfaceColor,
                    topLeft = Offset(cx - w * 0.45f, floorY),
                    size = androidx.compose.ui.geometry.Size(w * 0.9f, 8f * scaleFactor),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                )

                // Athlete Head
                drawCircle(color = primaryColor, radius = headRadius, center = Offset(cx - w * 0.28f, bodyY - headRadius * 0.5f))
                
                // Torso & Upper Chest
                drawLine(
                    color = primaryColor,
                    start = Offset(cx - w * 0.25f, bodyY),
                    end = Offset(cx + w * 0.18f, bodyY + 10f * scaleFactor),
                    strokeWidth = strokeWidth * 1.6f,
                    cap = StrokeCap.Round
                )

                // Flexed Arms / Forearms to floor
                drawLine(
                    color = secondaryColor,
                    start = Offset(cx - w * 0.16f, bodyY),
                    end = Offset(cx - w * 0.16f, floorY),
                    strokeWidth = strokeWidth * 1.3f,
                    cap = StrokeCap.Round
                )

                // Legs & Feet
                drawLine(
                    color = primaryColor,
                    start = Offset(cx + w * 0.18f, bodyY + 10f * scaleFactor),
                    end = Offset(cx + w * 0.35f, floorY),
                    strokeWidth = strokeWidth * 1.4f,
                    cap = StrokeCap.Round
                )
            }

            // Squats (Back Squat, Goblet Squat, Front Squat)
            exerciseId.contains("squat") -> {
                val squatDepth = (progress - 0.5f) * (50f * scaleFactor)
                val bodyY = cy + squatDepth
                val floorY = cy + (50f * scaleFactor)

                // Floor Platform
                drawLine(
                    color = surfaceColor,
                    start = Offset(cx - w * 0.35f, floorY),
                    end = Offset(cx + w * 0.35f, floorY),
                    strokeWidth = 6f * scaleFactor,
                    cap = StrokeCap.Round
                )

                // Head
                drawCircle(color = primaryColor, radius = headRadius, center = Offset(cx, bodyY - 55f * scaleFactor))
                
                // Barbell across shoulders
                val barY = bodyY - 45f * scaleFactor
                drawLine(
                    color = secondaryColor,
                    start = Offset(cx - 70f * scaleFactor, barY),
                    end = Offset(cx + 70f * scaleFactor, barY),
                    strokeWidth = 10f * scaleFactor,
                    cap = StrokeCap.Round
                )
                // Barbell Weight Plates
                drawRoundRect(color = primaryColor, topLeft = Offset(cx - 75f * scaleFactor, barY - 20f * scaleFactor), size = androidx.compose.ui.geometry.Size(10f * scaleFactor, 40f * scaleFactor), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
                drawRoundRect(color = primaryColor, topLeft = Offset(cx + 65f * scaleFactor, barY - 20f * scaleFactor), size = androidx.compose.ui.geometry.Size(10f * scaleFactor, 40f * scaleFactor), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))

                // Muscular Torso
                drawLine(
                    color = primaryColor,
                    start = Offset(cx, bodyY - 45f * scaleFactor),
                    end = Offset(cx, bodyY),
                    strokeWidth = strokeWidth * 1.8f,
                    cap = StrokeCap.Round
                )

                // Bent Thighs & Shin Legs
                val kneeX = cx - 35f * scaleFactor
                val kneeY = bodyY + 30f * scaleFactor
                drawLine(color = secondaryColor, start = Offset(cx, bodyY), end = Offset(kneeX, kneeY), strokeWidth = strokeWidth * 1.4f, cap = StrokeCap.Round)
                drawLine(color = secondaryColor, start = Offset(kneeX, kneeY), end = Offset(cx - 20f * scaleFactor, floorY), strokeWidth = strokeWidth * 1.4f, cap = StrokeCap.Round)
            }

            // Pull-Ups & Chin-Ups
            exerciseId.contains("pull_up") || exerciseId.contains("chin_up") -> {
                val barY = cy - 60f * scaleFactor
                val pullY = barY + (1f - progress) * (70f * scaleFactor)

                // Overhead Pull-Up Rig Structure
                drawLine(color = surfaceColor, start = Offset(cx - w * 0.4f, barY), end = Offset(cx + w * 0.4f, barY), strokeWidth = 12f * scaleFactor, cap = StrokeCap.Round)
                // Rig Rubber Grips
                drawCircle(color = secondaryColor, radius = 10f * scaleFactor, center = Offset(cx - 50f * scaleFactor, barY))
                drawCircle(color = secondaryColor, radius = 10f * scaleFactor, center = Offset(cx + 50f * scaleFactor, barY))

                // Head
                drawCircle(color = primaryColor, radius = headRadius, center = Offset(cx, pullY - 30f * scaleFactor))

                // Arms Gripping Barbell
                drawLine(color = secondaryColor, start = Offset(cx - 50f * scaleFactor, barY), end = Offset(cx - 20f * scaleFactor, pullY - 20f * scaleFactor), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
                drawLine(color = secondaryColor, start = Offset(cx + 50f * scaleFactor, barY), end = Offset(cx + 20f * scaleFactor, pullY - 20f * scaleFactor), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)

                // V-Taper Lat Torso
                drawLine(color = primaryColor, start = Offset(cx, pullY - 20f * scaleFactor), end = Offset(cx, pullY + 45f * scaleFactor), strokeWidth = strokeWidth * 1.8f, cap = StrokeCap.Round)
            }

            // Dips
            exerciseId.contains("dip") -> {
                val barY = cy + 15f * scaleFactor
                val dipY = cy - (progress - 0.5f) * (60f * scaleFactor)

                // Dip Handles
                drawLine(color = surfaceColor, start = Offset(cx - 50f * scaleFactor, barY - 10f * scaleFactor), end = Offset(cx - 50f * scaleFactor, barY + 60f * scaleFactor), strokeWidth = 12f * scaleFactor, cap = StrokeCap.Round)
                drawLine(color = surfaceColor, start = Offset(cx + 50f * scaleFactor, barY - 10f * scaleFactor), end = Offset(cx + 50f * scaleFactor, barY + 60f * scaleFactor), strokeWidth = 12f * scaleFactor, cap = StrokeCap.Round)

                // Head
                drawCircle(color = primaryColor, radius = headRadius, center = Offset(cx, dipY - 60f * scaleFactor))
                // Torso
                drawLine(color = primaryColor, start = Offset(cx, dipY - 50f * scaleFactor), end = Offset(cx, dipY), strokeWidth = strokeWidth * 1.8f, cap = StrokeCap.Round)

                // Bent Arms on Dip Bars
                drawLine(color = secondaryColor, start = Offset(cx - 50f * scaleFactor, barY), end = Offset(cx - 15f * scaleFactor, dipY - 35f * scaleFactor), strokeWidth = strokeWidth * 1.4f, cap = StrokeCap.Round)
                drawLine(color = secondaryColor, start = Offset(cx + 50f * scaleFactor, barY), end = Offset(cx + 15f * scaleFactor, dipY - 35f * scaleFactor), strokeWidth = strokeWidth * 1.4f, cap = StrokeCap.Round)
            }

            // Bench Press & Dumbbell Press & Curls (Default)
            else -> {
                val liftHeight = (progress - 0.5f) * (65f * scaleFactor)
                val barY = cy - 25f * scaleFactor - liftHeight

                // Workout Bench Padding
                drawRoundRect(
                    color = surfaceColor,
                    topLeft = Offset(cx - 80f * scaleFactor, cy + 15f * scaleFactor),
                    size = androidx.compose.ui.geometry.Size(160f * scaleFactor, 16f * scaleFactor),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
                )

                // Athlete Lying on Bench (Head & Torso)
                drawCircle(color = primaryColor, radius = headRadius, center = Offset(cx - 65f * scaleFactor, cy))
                drawLine(color = primaryColor, start = Offset(cx - 50f * scaleFactor, cy), end = Offset(cx + 50f * scaleFactor, cy), strokeWidth = strokeWidth * 1.6f, cap = StrokeCap.Round)

                // Barbell Pressing Upwards
                drawLine(color = secondaryColor, start = Offset(cx - 70f * scaleFactor, barY), end = Offset(cx + 70f * scaleFactor, barY), strokeWidth = 9f * scaleFactor, cap = StrokeCap.Round)
                // Metallic Weight Plates
                drawRoundRect(color = primaryColor, topLeft = Offset(cx - 76f * scaleFactor, barY - 22f * scaleFactor), size = androidx.compose.ui.geometry.Size(12f * scaleFactor, 44f * scaleFactor), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))
                drawRoundRect(color = primaryColor, topLeft = Offset(cx + 64f * scaleFactor, barY - 22f * scaleFactor), size = androidx.compose.ui.geometry.Size(12f * scaleFactor, 44f * scaleFactor), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f))

                // Pushing Arms
                drawLine(color = secondaryColor, start = Offset(cx - 10f * scaleFactor, cy), end = Offset(cx - 25f * scaleFactor, barY), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
                drawLine(color = secondaryColor, start = Offset(cx + 10f * scaleFactor, cy), end = Offset(cx + 25f * scaleFactor, barY), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
            }
        }
    }
}


