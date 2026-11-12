package com.saurav.focusguard.ui.blocker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saurav.focusguard.domain.MindfulQuotes
import com.saurav.focusguard.ui.MainViewModel
import com.saurav.focusguard.ui.home.PauseDurationDialog
import com.saurav.focusguard.ui.theme.ActiveTabMaroon
import com.saurav.focusguard.ui.theme.CardNavy
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.theme.InnerDarkNavy
import com.saurav.focusguard.ui.theme.PrimaryMagenta
import com.saurav.focusguard.ui.theme.TextPrimaryWhite
import com.saurav.focusguard.ui.theme.TextSecondarySlate
import com.saurav.focusguard.ui.theme.TopRadialMagenta

@Composable
fun BlockerShieldScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToBlockedApps: () -> Unit,
    onNavigateToBlockedWebsites: () -> Unit
) {
    val prefs by viewModel.prefsState.collectAsState()
    val scrollTimeMillis by viewModel.todayScrollTimeMillis.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()

    var showEditLimitDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }

    val usedMinutes = (scrollTimeMillis / 60_000L).toInt()
    val limitMinutes = prefs.dailyLimitMinutes.coerceAtLeast(15)
    val progress = (usedMinutes.toFloat() / limitMinutes.toFloat()).coerceIn(0f, 1f)
    val isPaused = prefs.pausedUntilMillis > nowMillis

    val formattedLimit = formatHoursMinutes(limitMinutes)
    val formattedUsed = if (usedMinutes < 60) "${usedMinutes}m" else formatHoursMinutes(usedMinutes)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TopRadialMagenta, DeepSpaceNavy, DeepSpaceNavy)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CardNavy)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "NoScroll Blocker Shield",
                            color = TextPrimaryWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Your focus is currently protected.",
                                color = TextSecondarySlate,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF90A4AE),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 2. Hero Daily Limit Circular Gauge Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1A2438), Color(0xFF0E2138))
                            )
                        )
                        .padding(vertical = 28.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Daily Limit row with pencil icon
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { showEditLimitDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Limit: $formattedLimit",
                            color = TextPrimaryWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Daily Limit",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 240° Open-Bottom Circular Arc Gauge
                    Box(
                        modifier = Modifier.size(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 18.dp.toPx()
                            drawArc(
                                color = Color(0xFF061329),
                                startAngle = 145f,
                                sweepAngle = 250f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            if (progress > 0f) {
                                drawArc(
                                    color = PrimaryMagenta,
                                    startAngle = 145f,
                                    sweepAngle = 250f * progress,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF39D369),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formattedUsed,
                                color = TextPrimaryWhite,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "of $formattedLimit",
                                color = TextPrimaryWhite.copy(alpha = 0.85f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PAUSE button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                if (isPaused) {
                                    viewModel.resumeFromPause()
                                } else {
                                    showPauseDialog = true
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isPaused) "RESUME" else "PAUSE",
                            color = TextPrimaryWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // 3. BLOCK LIST Section
            item {
                val blockedAppsCount = prefs.blockedApps.size + prefs.pinLockedApps.size
                val blockedSitesCount = prefs.blockedWebsites.size

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "BLOCK LIST",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Blocked Apps Card
                    BlockListCard(
                        icon = Icons.Default.Apps,
                        title = "Blocked Apps",
                        badgeText = if (blockedAppsCount == 0) "None blocked" else "$blockedAppsCount blocked",
                        onClick = onNavigateToBlockedApps
                    )

                    // Blocked Websites Card
                    BlockListCard(
                        icon = Icons.Default.Public,
                        title = "Blocked Websites",
                        badgeText = if (blockedSitesCount == 0) "None blocked" else "$blockedSitesCount blocked",
                        onClick = onNavigateToBlockedWebsites
                    )
                }
            }
        }
    }

    if (showEditLimitDialog) {
        var sliderMinutes by remember { mutableIntStateOf(prefs.dailyLimitMinutes) }
        val band = MindfulQuotes.bandForMinutes(sliderMinutes)

        AlertDialog(
            onDismissRequest = { showEditLimitDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = "Set Daily Scroll Limit",
                    color = TextPrimaryWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${band.emoji} ${formatHoursMinutes(sliderMinutes)}",
                        color = PrimaryMagenta,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = band.label,
                        color = TextSecondarySlate,
                        fontSize = 14.sp
                    )
                    Slider(
                        value = sliderMinutes.toFloat(),
                        onValueChange = { sliderMinutes = (it.toInt() / 15) * 15 },
                        valueRange = 15f..360f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryMagenta,
                            activeTrackColor = PrimaryMagenta
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setDailyLimitMinutes(sliderMinutes)
                        showEditLimitDialog = false
                    }
                ) {
                    Text("Save", color = PrimaryMagenta, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditLimitDialog = false }) {
                    Text("Cancel", color = TextSecondarySlate)
                }
            }
        )
    }

    if (showPauseDialog) {
        PauseDurationDialog(
            maxMinutes = prefs.maxPauseDurationMinutes,
            onDismiss = { showPauseDialog = false },
            onSelectMinutes = { mins ->
                viewModel.pauseForMinutes(mins)
                showPauseDialog = false
            }
        )
    }
}

@Composable
private fun BlockListCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    badgeText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardNavy)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InnerDarkNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryMagenta,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimaryWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ActiveTabMaroon)
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(InnerDarkNavy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

private fun formatHoursMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}
