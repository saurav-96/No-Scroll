package com.saurav.focusguard.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saurav.focusguard.data.prefs.FocusPreferencesState
import com.saurav.focusguard.detection.PlatformDetectorRegistry
import com.saurav.focusguard.domain.AppGroupDefinition
import com.saurav.focusguard.domain.FocusPlan
import com.saurav.focusguard.domain.PlatformRestrictionMode
import com.saurav.focusguard.permissions.PermissionHelper
import com.saurav.focusguard.ui.MainViewModel
import com.saurav.focusguard.ui.theme.ActivePlanPlum
import com.saurav.focusguard.ui.theme.ActiveTabMaroon
import com.saurav.focusguard.ui.theme.CardNavy
import com.saurav.focusguard.ui.theme.CardStrokeBlue
import com.saurav.focusguard.ui.theme.CollapseLavender
import com.saurav.focusguard.ui.theme.CuriousBlue
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.theme.DiamondCircleGradient
import com.saurav.focusguard.ui.theme.ElectricIndigo
import com.saurav.focusguard.ui.theme.EmeraldWatchtime
import com.saurav.focusguard.ui.theme.InnerDarkNavy
import com.saurav.focusguard.ui.theme.PauseAmber
import com.saurav.focusguard.ui.theme.PinkStatsGradient
import com.saurav.focusguard.ui.theme.PlanBannerPlum
import com.saurav.focusguard.ui.theme.PremiumPinkPurpleGradient
import com.saurav.focusguard.ui.theme.PrimaryMagenta
import com.saurav.focusguard.ui.theme.TextMutedGrey
import com.saurav.focusguard.ui.theme.TextPrimaryWhite
import com.saurav.focusguard.ui.theme.TextSecondarySlate
import com.saurav.focusguard.ui.theme.TopRadialMagenta
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAllApps: () -> Unit,
    onNavigateToBlockWebsites: () -> Unit,
    onNavigateToPinConfig: () -> Unit
) {
    val context = LocalContext.current
    val prefs by viewModel.prefsState.collectAsState()
    val sessionsCount by viewModel.todaySessionsCount.collectAsState()
    val blockedCount by viewModel.todayBlockedCount.collectAsState()
    val scrollTimeMillis by viewModel.todayScrollTimeMillis.collectAsState()
    val installedPackages by viewModel.installedPackages.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()

    var blocklistExpanded by remember { mutableStateOf(true) }
    var browsersExpanded by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showPermissionsSheet by remember { mutableStateOf(false) }
    var showWatchtimeInfoDialog by remember { mutableStateOf(false) }

    val expandedPlatformCards = remember {
        mutableStateMapOf(
            "youtube" to true,
            "whatsapp" to true,
            "linkedin" to true,
            "reddit" to true,
            "youtube_music" to true
        )
    }

    val isPaused = prefs.pausedUntilMillis > nowMillis
    val pauseRemainingSec = if (isPaused) ((prefs.pausedUntilMillis - nowMillis) / 1000L).coerceAtLeast(0L) else 0L

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NoScroll",
                                color = TextPrimaryWhite,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(ActiveTabMaroon)
                                    .clickable { showPermissionsSheet = true }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "UPGRADE",
                                    color = Color(0xFFFF80B3),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reclaim your focus today.",
                            color = TextSecondarySlate,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DiamondCircleGradient)
                            .clickable { showPermissionsSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Premium Status",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 2. Hero Circular Status Indicator
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3D0C2E))
                            .clickable {
                                if (!PermissionHelper.isAccessibilityServiceEnabled(context)) {
                                    showPermissionsSheet = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(116.dp)
                                .clip(CircleShape)
                                .border(3.5.dp, PrimaryMagenta, CircleShape)
                                .padding(7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        if (isPaused) PauseAmber else PrimaryMagenta
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isPaused -> Icons.Default.Pause
                                        prefs.activePlan == FocusPlan.CURIOUS -> Icons.Default.Psychology
                                        prefs.activePlan == FocusPlan.ONE_REEL -> Icons.Default.TouchApp
                                        else -> Icons.Default.Block
                                    },
                                    contentDescription = prefs.activePlan.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isPaused) "Paused" else prefs.activePlan.title,
                        color = TextPrimaryWhite,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPaused) {
                            String.format(
                                Locale.US,
                                "Resuming automatically in %02d:%02d",
                                pauseRemainingSec / 60,
                                pauseRemainingSec % 60
                            )
                        } else {
                            prefs.activePlan.subtitle
                        },
                        color = TextSecondarySlate,
                        fontSize = 14.sp
                    )
                }
            }

            // 3. Pink Gradient Quick-Stats Pill
            item {
                val totalSeconds = scrollTimeMillis / 1000L
                val formattedScrollTime = String.format(
                    Locale.US,
                    "%02d:%02d",
                    totalSeconds / 60L,
                    totalSeconds % 60L
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .clip(RoundedCornerShape(37.dp))
                        .background(PinkStatsGradient)
                        .clickable { onNavigateToAnalytics() }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sessions
                    StatColumn(
                        icon = Icons.Default.Timer,
                        value = sessionsCount.toString(),
                        label = "Sessions",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalPinkDivider()
                    // Blocked
                    StatColumn(
                        icon = Icons.Default.Block,
                        value = blockedCount.toString(),
                        label = "Blocked",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalPinkDivider()
                    // Scroll Time
                    StatColumn(
                        icon = Icons.Default.TrendingUp,
                        value = formattedScrollTime,
                        label = "Scroll Time",
                        modifier = Modifier.weight(1.15f)
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DeepSpaceNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Analytics",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. SELECT PLAN Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT PLAN",
                            color = TextSecondarySlate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PauseAmber)
                                .clickable {
                                    if (isPaused) {
                                        viewModel.resumeFromPause()
                                    } else {
                                        showPauseDialog = true
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = "Pause",
                                    tint = Color(0xFF1E1300),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isPaused) "Resume" else "Pause",
                                    color = Color(0xFF1E1300),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Active Plan Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PlanBannerPlum)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(PrimaryMagenta),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = prefs.activePlan.bannerText,
                            color = TextPrimaryWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 3 Plan Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PlanSelectorCard(
                            plan = FocusPlan.CURIOUS,
                            selected = prefs.activePlan == FocusPlan.CURIOUS,
                            onSelect = { viewModel.selectPlan(FocusPlan.CURIOUS) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanSelectorCard(
                            plan = FocusPlan.BLOCK_ALL,
                            selected = prefs.activePlan == FocusPlan.BLOCK_ALL,
                            onSelect = { viewModel.selectPlan(FocusPlan.BLOCK_ALL) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanSelectorCard(
                            plan = FocusPlan.ONE_REEL,
                            selected = prefs.activePlan == FocusPlan.ONE_REEL,
                            onSelect = { viewModel.selectPlan(FocusPlan.ONE_REEL) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Wide NoScroll Gesture Preview Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(82.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardNavy)
                            .clickable { showPermissionsSheet = true },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "NoScroll",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "NoScroll",
                            color = TextSecondarySlate,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 5. SCROLL WATCHTIME Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SCROLL WATCHTIME",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(CardNavy)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldWatchtime),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "00:00",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Scroll Watchtime",
                                color = TextPrimaryWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Watchtime Info",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { showWatchtimeInfoDialog = true }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Watchtime Settings",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { showWatchtimeInfoDialog = true }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Switch(
                                checked = prefs.scrollWatchtimeEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && !PermissionHelper.isOverlayPermissionGranted(context)) {
                                        PermissionHelper.openOverlaySettings(context)
                                    }
                                    viewModel.setScrollWatchtime(enabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryMagenta,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFF6B7280)
                                )
                            )
                        }
                    }
                }
            }

            // 6. NoScroll Community Banner + Carousel Dots
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardNavy)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryMagenta)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(InnerDarkNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "NoScroll Community",
                                    color = TextPrimaryWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Join NoScroll Reddit community!",
                                    color = TextSecondarySlate,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PrimaryMagenta)
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "Join →",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(PrimaryMagenta)
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF5D1441))
                        )
                    }
                }
            }

            // 7. DOOM-SCROLLING APPS Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DOOM-SCROLLING APPS",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    // Scroll Blocklist summary header card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(CardNavy)
                            .border(1.dp, Color(0xFF6B1546), RoundedCornerShape(22.dp))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Scroll Blocklist",
                                    color = TextPrimaryWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(InnerDarkNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "3",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                BrandMiniBadge(text = "S", bgColor = Color(0xFFFF0000))
                                BrandMiniBadge(text = "in", bgColor = Color(0xFF0A66C2))
                                BrandMiniBadge(text = "r/", bgColor = Color(0xFFFF4500))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CollapseLavender)
                                .clickable { blocklistExpanded = !blocklistExpanded }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (blocklistExpanded) "Collapse" else "Expand",
                                    color = Color(0xFF0B1831),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (blocklistExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFF0B1831),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = blocklistExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            PlatformDetectorRegistry.appGroups.forEach { group ->
                                val isInstalled = group.packageNames.any { it in installedPackages } ||
                                    group.groupId in setOf("youtube", "whatsapp", "linkedin", "reddit", "youtube_music")
                                val isCardExpanded = expandedPlatformCards[group.groupId] ?: false

                                PlatformGroupCard(
                                    group = group,
                                    isInstalled = isInstalled,
                                    isExpanded = isCardExpanded,
                                    prefs = prefs,
                                    onToggleExpand = {
                                        expandedPlatformCards[group.groupId] = !isCardExpanded
                                    },
                                    onSelectRestriction = { featureId, mode ->
                                        viewModel.setFeatureMode(featureId, mode)
                                    },
                                    onToggleDmReels = { allow ->
                                        viewModel.setAllowInstagramDmReels(allow)
                                    },
                                    onLockClick = {
                                        group.packageNames.firstOrNull()?.let { pkg ->
                                            viewModel.toggleBlockedApp(pkg)
                                        }
                                    },
                                    onPinClick = {
                                        onNavigateToPinConfig()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 8. BROWSERS Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "BROWSERS",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { browsersExpanded = !browsersExpanded }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Supported Browsers",
                                    color = TextPrimaryWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CardNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (browsersExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand Browsers",
                                tint = Color.White
                            )
                        }

                        AnimatedVisibility(visible = browsersExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardNavy)
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "• Google Chrome (Shorts, Reels & Blocked Websites active)",
                                    color = TextPrimaryWhite,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "• Brave, Firefox, Edge, Opera & Samsung Internet supported",
                                    color = TextSecondarySlate,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 9. ALL APPS & SITES Section
            item {
                val restrictedCount = prefs.blockedApps.size + prefs.pinLockedApps.size
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ALL APPS & SITES",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(CardNavy)
                    ) {
                        // All Apps Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAllApps() }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(ActiveTabMaroon),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "All Apps",
                                            color = TextPrimaryWhite,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(ActiveTabMaroon)
                                                .padding(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$restrictedCount Restricted",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Lock or restrict any installed app",
                                        color = TextSecondarySlate,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
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

                        // Block Websites Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToBlockWebsites() }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(ActiveTabMaroon),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Block Websites",
                                        color = TextPrimaryWhite,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Manage blocked URLs & keywords",
                                        color = TextSecondarySlate,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
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
                }
            }

            // 10. SUPPORT Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "SUPPORT",
                        color = TextSecondarySlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(CardNavy)
                            .clickable { showPermissionsSheet = true }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CollapseLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = Color(0xFF0B1831),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Support",
                                color = TextPrimaryWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Troubleshoot & contact us",
                                color = TextSecondarySlate,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
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

    if (showWatchtimeInfoDialog) {
        AlertDialog(
            onDismissRequest = { showWatchtimeInfoDialog = false },
            containerColor = CardNavy,
            title = {
                Text("Scroll Watchtime Pill", color = TextPrimaryWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Displays a live floating timer pill (00:00) over short-video feeds so you stay aware of how long you have been scrolling today.",
                    color = TextSecondarySlate
                )
            },
            confirmButton = {
                TextButton(onClick = { showWatchtimeInfoDialog = false }) {
                    Text("Got it", color = PrimaryMagenta)
                }
            }
        )
    }

    if (showPermissionsSheet) {
        val accEnabled = PermissionHelper.isAccessibilityServiceEnabled(context)
        val overlayEnabled = PermissionHelper.isOverlayPermissionGranted(context)
        val adminEnabled = PermissionHelper.isDeviceAdminActive(context)
        AlertDialog(
            onDismissRequest = { showPermissionsSheet = false },
            containerColor = CardNavy,
            title = {
                Text("System Permissions & Status", color = TextPrimaryWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PermissionRowItem(
                        title = "Accessibility Service (Reel Detection)",
                        granted = accEnabled,
                        onClick = { PermissionHelper.openAccessibilitySettings(context) }
                    )
                    PermissionRowItem(
                        title = "Display Over Other Apps (Watchtime Pill)",
                        granted = overlayEnabled,
                        onClick = { PermissionHelper.openOverlaySettings(context) }
                    )
                    PermissionRowItem(
                        title = "Device Admin (Uninstall Protection)",
                        granted = adminEnabled,
                        onClick = { PermissionHelper.requestDeviceAdmin(context) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPermissionsSheet = false }) {
                    Text("Close", color = PrimaryMagenta, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun StatColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun VerticalPinkDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.White.copy(alpha = 0.35f))
    )
}

@Composable
private fun PlanSelectorCard(
    plan: FocusPlan,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (selected) ActivePlanPlum else CardNavy
    val borderColor = when {
        selected -> PrimaryMagenta.copy(alpha = 0.6f)
        plan == FocusPlan.ONE_REEL -> ElectricIndigo.copy(alpha = 0.7f)
        else -> CardStrokeBlue
    }

    Column(
        modifier = modifier
            .height(118.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val circleColor = when (plan) {
                FocusPlan.CURIOUS -> CuriousBlue
                FocusPlan.BLOCK_ALL -> PrimaryMagenta
                FocusPlan.ONE_REEL -> Color(0xFF9EA6B8)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (plan) {
                        FocusPlan.CURIOUS -> Icons.Default.Psychology
                        FocusPlan.BLOCK_ALL -> Icons.Default.Block
                        FocusPlan.ONE_REEL -> Icons.Default.TouchApp
                    },
                    contentDescription = plan.title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (plan == FocusPlan.ONE_REEL) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Premium",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column {
            Text(
                text = plan.title,
                color = if (selected) Color.White else TextSecondarySlate,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (selected) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(PrimaryMagenta)
                )
            }
        }
    }
}

@Composable
private fun BrandMiniBadge(text: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun PlatformGroupCard(
    group: AppGroupDefinition,
    isInstalled: Boolean,
    isExpanded: Boolean,
    prefs: FocusPreferencesState,
    onToggleExpand: () -> Unit,
    onSelectRestriction: (String, PlatformRestrictionMode) -> Unit,
    onToggleDmReels: (Boolean) -> Unit,
    onLockClick: () -> Unit,
    onPinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardNavy)
            .clickable { onToggleExpand() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(group.brandColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = group.badgeText,
                        color = if (group.groupId == "snapchat") Color.Black else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = group.displayName,
                        color = TextPrimaryWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isInstalled) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "• App not installed!",
                            color = TextMutedGrey,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isInstalled || isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock App",
                        tint = TextSecondarySlate,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onLockClick() }
                    )
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = "PIN Protect",
                        tint = TextSecondarySlate,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onPinClick() }
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(InnerDarkNavy)
                            .clickable { onToggleExpand() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Sub-features",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = isInstalled && isExpanded) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                group.subFeatures.forEach { subFeature ->
                    var menuExpanded by remember { mutableStateOf(false) }
                    val currentMode = prefs.restrictionForFeature(subFeature.id, subFeature.defaultMode)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(InnerDarkNavy)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subFeature.title,
                            color = TextPrimaryWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        if (subFeature.isToggleSwitch) {
                            Switch(
                                checked = prefs.allowInstagramDmReels,
                                onCheckedChange = onToggleDmReels,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryMagenta
                                )
                            )
                        } else {
                            Box {
                                val useGradientBadge = subFeature.requiresPremiumBadge &&
                                    !prefs.platformRestrictions.containsKey(subFeature.id)

                                val pillModifier = when {
                                    useGradientBadge -> Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(PremiumPinkPurpleGradient)
                                    currentMode == PlatformRestrictionMode.DONT_BLOCK -> Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFF0E2244))
                                    else -> Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(PrimaryMagenta)
                                }

                                Box(
                                    modifier = pillModifier
                                        .clickable { menuExpanded = true }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (useGradientBadge) "Upgrade" else "${currentMode.label} ▾",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    modifier = Modifier.background(CardNavy)
                                ) {
                                    PlatformRestrictionMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = mode.label,
                                                    color = if (mode == currentMode) PrimaryMagenta else TextPrimaryWhite,
                                                    fontWeight = if (mode == currentMode) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                onSelectRestriction(subFeature.id, mode)
                                                menuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PauseDurationDialog(
    maxMinutes: Int,
    onDismiss: () -> Unit,
    onSelectMinutes: (Int) -> Unit
) {
    val options = listOf(1, 5, 10, 15, 30).filter { it <= maxMinutes.coerceAtLeast(15) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNavy,
        title = {
            Text(
                text = "Pause NoScroll Blocking",
                color = TextPrimaryWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select how long to temporarily pause short-video blocking:",
                    color = TextSecondarySlate,
                    fontSize = 13.sp
                )
                options.forEach { mins ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(InnerDarkNavy)
                            .clickable { onSelectMinutes(mins) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Pause for $mins ${if (mins == 1) "Minute" else "Minutes"}",
                            color = PauseAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondarySlate)
            }
        }
    )
}

@Composable
private fun PermissionRowItem(
    title: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InnerDarkNavy)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimaryWhite,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (granted) Icons.Default.Check else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (granted) Color(0xFF22C55E) else PrimaryMagenta
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (granted) "Active" else "Enable",
                color = if (granted) Color(0xFF22C55E) else PrimaryMagenta,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
