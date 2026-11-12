package com.saurav.focusguard.ui.analytics

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saurav.focusguard.ui.MainViewModel
import com.saurav.focusguard.ui.theme.CardNavy
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.theme.InnerDarkNavy
import com.saurav.focusguard.ui.theme.PinkStatsGradient
import com.saurav.focusguard.ui.theme.PrimaryMagenta
import com.saurav.focusguard.ui.theme.TextPrimaryWhite
import com.saurav.focusguard.ui.theme.TextSecondarySlate
import com.saurav.focusguard.ui.theme.TopRadialMagenta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val sessionsCount by viewModel.todaySessionsCount.collectAsState()
    val blockedCount by viewModel.todayBlockedCount.collectAsState()
    val scrollTimeMillis by viewModel.todayScrollTimeMillis.collectAsState()
    val platformBreakdown by viewModel.platformBreakdown.collectAsState()
    val recentBlocks by viewModel.recentBlockEvents.collectAsState()

    val totalSeconds = scrollTimeMillis / 1000L
    val formattedScrollTime = String.format(Locale.US, "%02d:%02d", totalSeconds / 60L, totalSeconds % 60L)
    val timeFormatter = SimpleDateFormat("hh:mm:ss a", Locale.US)

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
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                            text = "Scroll Analytics",
                            color = TextPrimaryWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Today's sessions, blocks & watchtime",
                            color = TextSecondarySlate,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Summary Gradient Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(PinkStatsGradient)
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnalyticsStatMetric(icon = Icons.Default.Timer, value = "$sessionsCount", label = "Sessions")
                    AnalyticsStatMetric(icon = Icons.Default.Block, value = "$blockedCount", label = "Blocked")
                    AnalyticsStatMetric(icon = Icons.Default.TrendingUp, value = formattedScrollTime, label = "Scroll Time")
                }
            }

            // Platform Breakdown
            item {
                Text(
                    text = "PLATFORM BREAKDOWN",
                    color = TextSecondarySlate,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            if (platformBreakdown.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardNavy)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No short-video scroll sessions recorded today.",
                            color = TextSecondarySlate,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(platformBreakdown) { item ->
                    val secs = item.totalDurationMillis / 1000L
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardNavy)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.platformId.replace('_', ' '),
                                color = TextPrimaryWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.sessionCount} sessions today",
                                color = TextSecondarySlate,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = String.format(Locale.US, "%02dm %02ds", secs / 60, secs % 60),
                            color = PrimaryMagenta,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Recent Blocks Log
            item {
                Text(
                    text = "RECENT BLOCK EVENTS",
                    color = TextSecondarySlate,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            if (recentBlocks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardNavy)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Zero reels blocked yet today. Keep your focus strong!",
                            color = TextSecondarySlate,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(recentBlocks) { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardNavy)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(InnerDarkNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = PrimaryMagenta,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = event.platformId.replace('_', ' '),
                                    color = TextPrimaryWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = event.reason,
                                    color = TextSecondarySlate,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = timeFormatter.format(Date(event.timestampMillis)),
                            color = TextSecondarySlate,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
    }
}
