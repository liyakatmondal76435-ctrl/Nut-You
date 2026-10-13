package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.CleanStreakUiState
import com.example.ui.components.DailyCheckInCard
import com.example.ui.components.DailyCheckInDialog
import com.example.ui.components.DailyPledgeCard
import com.example.ui.components.LivePulseTimerCard
import com.example.ui.components.RelapseDialog

@Composable
fun DashboardScreen(
    uiState: CleanStreakUiState,
    onPledgeClicked: () -> Unit,
    onSubmitCheckIn: (mood: Int, urge: Int, note: String, gratitude: String) -> Unit,
    onRecordRelapse: (triggers: List<String>, note: String, lesson: String) -> Unit,
    onNavigateToSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showRelapseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Live Pulse Timer Card
        LivePulseTimerCard(
            streakTime = uiState.streakTime,
            habitName = uiState.habitProfile.habitName,
            startTimeMillis = uiState.habitProfile.currentStreakStartTime,
            nextMilestone = uiState.nextMilestone
        )

        // Emergency SOS Urge-Breaker Callout Card
        Card(
            onClick = onNavigateToSos,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("emergency_sos_banner"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Urge SOS",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Feeling an Urge Right Now?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Tap for guided breathing & urge surfing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Button(
                    onClick = onNavigateToSos,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.testTag("sos_quick_button")
                ) {
                    Text("Break Urge")
                }
            }
        }

        // Daily Mindful Check-in
        DailyCheckInCard(
            todayCheckIn = uiState.todayCheckIn,
            onOpenCheckInDialog = { showCheckInDialog = true }
        )

        // Daily Commitment Pledge & Quote
        DailyPledgeCard(
            pledgeText = uiState.habitProfile.pledge,
            isPledgedToday = uiState.isPledgedToday,
            dailyQuote = uiState.dailyQuote,
            onPledgeClicked = onPledgeClicked
        )

        // Relapse / Reset Section with Thoughtful Framing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Had a Slip or Relapse?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Honesty strengthens recovery. Reset with compassion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { showRelapseDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("relapse_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Streak")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showCheckInDialog) {
        DailyCheckInDialog(
            initialCheckIn = uiState.todayCheckIn,
            onDismiss = { showCheckInDialog = false },
            onSubmit = onSubmitCheckIn
        )
    }

    if (showRelapseDialog) {
        RelapseDialog(
            onDismiss = { showRelapseDialog = false },
            onConfirmReset = onRecordRelapse
        )
    }
}
