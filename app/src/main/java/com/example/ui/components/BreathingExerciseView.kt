package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.BreathPhase
import com.example.ui.model.BreathingPattern
import kotlinx.coroutines.delay

@Composable
fun BreathingExerciseView(
    onSessionCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPattern by remember { mutableStateOf(BreathingPattern.BOX_BREATHING) }
    var isRunning by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var secondsRemainingInStep by remember { mutableIntStateOf(selectedPattern.steps[0].durationSeconds) }
    var completedCycles by remember { mutableIntStateOf(0) }

    val currentStep = selectedPattern.steps[currentStepIndex]
    val animScale = remember { Animatable(0.7f) }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    // Timer logic
    LaunchedEffect(isRunning, currentStepIndex, selectedPattern) {
        if (!isRunning) return@LaunchedEffect

        // Animate circle scale according to phase
        when (currentStep.phase) {
            BreathPhase.INHALE -> {
                vibrateShort()
                animScale.animateTo(
                    targetValue = 1.25f,
                    animationSpec = tween(
                        durationMillis = currentStep.durationSeconds * 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            BreathPhase.EXHALE -> {
                vibrateShort()
                animScale.animateTo(
                    targetValue = 0.7f,
                    animationSpec = tween(
                        durationMillis = currentStep.durationSeconds * 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            BreathPhase.HOLD_IN -> {
                vibrateShort()
            }
            BreathPhase.HOLD_OUT -> {
                vibrateShort()
            }
        }
    }

    LaunchedEffect(isRunning, secondsRemainingInStep, selectedPattern) {
        if (!isRunning) return@LaunchedEffect

        if (secondsRemainingInStep > 1) {
            delay(1000L)
            secondsRemainingInStep -= 1
        } else {
            delay(1000L)
            // Next step in pattern
            val nextIndex = (currentStepIndex + 1) % selectedPattern.steps.size
            if (nextIndex == 0) {
                completedCycles += 1
                if (completedCycles >= 4) {
                    onSessionCompleted()
                }
            }
            currentStepIndex = nextIndex
            secondsRemainingInStep = selectedPattern.steps[nextIndex].durationSeconds
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("breathing_exercise_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pattern Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BreathingPattern.values().forEach { pattern ->
                    val isSelected = selectedPattern == pattern
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                isRunning = false
                                selectedPattern = pattern
                                currentStepIndex = 0
                                secondsRemainingInStep = pattern.steps[0].durationSeconds
                                completedCycles = 0
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (pattern) {
                                BreathingPattern.BOX_BREATHING -> "Box 4x4"
                                BreathingPattern.RELAXING_478 -> "4-7-8 Calm"
                                BreathingPattern.CALM_FLOW -> "Paced 4-6"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Large Animated Breathing Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .padding(10.dp)
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(animScale.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Inner core
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(animScale.value)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = if (isRunning) currentStep.phase.label else "Ready",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isRunning) {
                            Text(
                                text = "${secondsRemainingInStep}s",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(
                                text = "Tap Play",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phase instructions
            Text(
                text = if (isRunning) currentStep.phase.instruction else selectedPattern.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Cycles completed: $completedCycles",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Play / Pause / Reset Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        currentStepIndex = 0
                        secondsRemainingInStep = selectedPattern.steps[0].durationSeconds
                        completedCycles = 0
                    },
                    modifier = Modifier.testTag("reset_breathing_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Breathing",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { isRunning = !isRunning },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("toggle_breathing_button")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pause" else "Start Breathing")
                }
            }
        }
    }
}
