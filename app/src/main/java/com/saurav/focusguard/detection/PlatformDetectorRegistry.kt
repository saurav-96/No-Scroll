package com.saurav.focusguard.detection

import android.view.accessibility.AccessibilityNodeInfo
import com.saurav.focusguard.domain.AppGroupDefinition
import com.saurav.focusguard.domain.PlatformRestrictionMode
import com.saurav.focusguard.domain.SubFeatureDefinition

data class DetectorRule(
    val featureId: String,
    val title: String,
    val packageNames: Set<String>,
    val viewIdSuffixes: List<String> = emptyList(),
    val contentDescriptions: List<String> = emptyList(),
    val textPatterns: List<String> = emptyList(),
    val urlKeywords: List<String> = emptyList(),
    val exceptionViewIdSuffixes: List<String> = emptyList(),
    val defaultMode: PlatformRestrictionMode = PlatformRestrictionMode.AS_PER_PLAN
)

data class DetectionMatch(
    val featureId: String,
    val title: String,
    val packageName: String,
    val reelSignature: String,
    val defaultMode: PlatformRestrictionMode
)

/**
 * Clean-room registry reproducing the exact view-ID, content-description, and URL rules
 * discovered from NoScroll's res/raw/platforms_config.json and NoScrollAccessibilityService.
 */
object PlatformDetectorRegistry {

    val appGroups: List<AppGroupDefinition> = listOf(
        AppGroupDefinition(
            groupId = "youtube",
            displayName = "Youtube",
            packageNames = listOf("com.google.android.youtube", "app.revanced.android.youtube"),
            brandColorHex = 0xFFFF0000,
            badgeText = "S",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "YOUTUBE_SHORTS",
                    title = "YouTube Shorts",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN
                )
            )
        ),
        AppGroupDefinition(
            groupId = "instagram",
            displayName = "Instagram",
            packageNames = listOf("com.instagram.android"),
            brandColorHex = 0xFFE1306C,
            badgeText = "IG",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "INSTAGRAM_REELS",
                    title = "Instagram Reels",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN
                ),
                SubFeatureDefinition(
                    id = "INSTAGRAM_DM_REELS",
                    title = "Allow Reels By Friends (DMs)",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK,
                    isToggleSwitch = true,
                    defaultToggleValue = true
                ),
                SubFeatureDefinition(
                    id = "INSTAGRAM_STORIES",
                    title = "Instagram Stories",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                ),
                SubFeatureDefinition(
                    id = "INSTAGRAM_EXPLORE",
                    title = "Instagram Explore",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                )
            )
        ),
        AppGroupDefinition(
            groupId = "snapchat",
            displayName = "Snapchat",
            packageNames = listOf("com.snapchat.android"),
            brandColorHex = 0xFFFFFC00,
            badgeText = "SC",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "SNAPCHAT_SPOTLIGHTS",
                    title = "Snapchat Spotlights",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN
                ),
                SubFeatureDefinition(
                    id = "SNAPCHAT_STORIES",
                    title = "Snapchat Stories",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                ),
                SubFeatureDefinition(
                    id = "SNAPCHAT_DISCOVER",
                    title = "Snapchat Discover",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                )
            )
        ),
        AppGroupDefinition(
            groupId = "whatsapp",
            displayName = "Whatsapp",
            packageNames = listOf("com.whatsapp", "com.whatsapp.w4b"),
            brandColorHex = 0xFF25D366,
            badgeText = "WA",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "WHATSAPP_STATUS",
                    title = "WhatsApp Status",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                )
            )
        ),
        AppGroupDefinition(
            groupId = "twitter",
            displayName = "X (Twitter)",
            packageNames = listOf("com.twitter.android"),
            brandColorHex = 0xFF000000,
            badgeText = "X",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "TWITTER_VIDEOS",
                    title = "X Reels",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN
                )
            )
        ),
        AppGroupDefinition(
            groupId = "linkedin",
            displayName = "LinkedIn",
            packageNames = listOf("com.linkedin.android"),
            brandColorHex = 0xFF0A66C2,
            badgeText = "in",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "LINKEDIN_REELS",
                    title = "LinkedIn Watch",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN,
                    requiresPremiumBadge = true
                )
            )
        ),
        AppGroupDefinition(
            groupId = "reddit",
            displayName = "Reddit",
            packageNames = listOf("com.reddit.frontpage"),
            brandColorHex = 0xFFFF4500,
            badgeText = "r/",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "REDDIT_REELS",
                    title = "Reddit Watch",
                    defaultMode = PlatformRestrictionMode.AS_PER_PLAN
                )
            )
        ),
        AppGroupDefinition(
            groupId = "youtube_music",
            displayName = "YouTube Music",
            packageNames = listOf("com.google.android.apps.youtube.music"),
            brandColorHex = 0xFFFF0000,
            badgeText = "YM",
            subFeatures = listOf(
                SubFeatureDefinition(
                    id = "YOUTUBE_MUSIC_SAMPLES",
                    title = "YT Music Samples",
                    defaultMode = PlatformRestrictionMode.DONT_BLOCK
                )
            )
        )
    )

    val supportedBrowsers: Map<String, String> = mapOf(
        "com.android.chrome" to ":id/url_bar",
        "com.chrome.beta" to ":id/url_bar",
        "org.mozilla.firefox" to ":id/mozac_browser_toolbar_url_view",
        "com.microsoft.emmx" to ":id/url_bar",
        "com.brave.browser" to ":id/url_bar",
        "com.opera.browser" to ":id/url_field",
        "com.sec.android.app.sbrowser" to ":id/location_bar_edit_text"
    )

    val detectorRules: List<DetectorRule> = listOf(
        DetectorRule(
            featureId = "YOUTUBE_SHORTS",
            title = "YouTube Shorts",
            packageNames = setOf("com.google.android.youtube", "app.revanced.android.youtube"),
            viewIdSuffixes = listOf(
                ":id/reel_player_underlay",
                ":id/reel_recycler",
                ":id/reel_watch_player"
            ),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "INSTAGRAM_REELS",
            title = "Instagram Reels",
            packageNames = setOf("com.instagram.android"),
            viewIdSuffixes = listOf(
                ":id/clips_video_container",
                ":id/clips_viewer_view_pager"
            ),
            exceptionViewIdSuffixes = listOf(":id/reply_bar_edittext"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "INSTAGRAM_STORIES",
            title = "Instagram Stories",
            packageNames = setOf("com.instagram.android"),
            viewIdSuffixes = listOf(":id/reel_viewer_title"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "INSTAGRAM_EXPLORE",
            title = "Instagram Explore",
            packageNames = setOf("com.instagram.android"),
            viewIdSuffixes = listOf(":id/action_bar_search_edit_text"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "SNAPCHAT_SPOTLIGHTS",
            title = "Snapchat Spotlights",
            packageNames = setOf("com.snapchat.android"),
            viewIdSuffixes = listOf(":id/spotlight_view_count"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "SNAPCHAT_STORIES",
            title = "Snapchat Stories",
            packageNames = setOf("com.snapchat.android"),
            viewIdSuffixes = listOf(":id/view_profile", "context_vertical_actions/context_vertical_action_share"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "SNAPCHAT_DISCOVER",
            title = "Snapchat Discover",
            packageNames = setOf("com.snapchat.android"),
            viewIdSuffixes = listOf(":id/df_large_story"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "WHATSAPP_STATUS",
            title = "WhatsApp Status",
            packageNames = setOf("com.whatsapp", "com.whatsapp.w4b"),
            viewIdSuffixes = listOf(":id/status_header"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "TWITTER_VIDEOS",
            title = "X Reels",
            packageNames = setOf("com.twitter.android"),
            viewIdSuffixes = listOf("VideoTab", ":id/mediacontroller_progress"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "LINKEDIN_REELS",
            title = "LinkedIn Watch",
            packageNames = setOf("com.linkedin.android"),
            viewIdSuffixes = listOf(":id/media_viewer_actor_bottom"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "REDDIT_REELS",
            title = "Reddit Watch",
            packageNames = setOf("com.reddit.frontpage"),
            viewIdSuffixes = listOf("content_video_view"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "YOUTUBE_MUSIC_SAMPLES",
            title = "YT Music Samples",
            packageNames = setOf("com.google.android.apps.youtube.music"),
            viewIdSuffixes = listOf(":id/reel_player_page_container"),
            defaultMode = PlatformRestrictionMode.DONT_BLOCK
        ),
        DetectorRule(
            featureId = "TIKTOK",
            title = "TikTok",
            packageNames = setOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill"),
            viewIdSuffixes = listOf(":id/desc"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        ),
        DetectorRule(
            featureId = "FACEBOOK_REELS",
            title = "Facebook Reels",
            packageNames = setOf("com.facebook.katana"),
            contentDescriptions = listOf("Add a comment…", "Add a comment"),
            defaultMode = PlatformRestrictionMode.AS_PER_PLAN
        )
    )

    val browserShortUrlPatterns: List<Pair<String, String>> = listOf(
        "youtube.com/shorts" to "BROWSER_YOUTUBE_SHORTS",
        "instagram.com/reels" to "BROWSER_INSTAGRAM_REELS",
        "instagram.com/reel/" to "BROWSER_INSTAGRAM_REELS",
        "tiktok.com/@" to "BROWSER_TIKTOK",
        "facebook.com/reel" to "BROWSER_FACEBOOK_REELS"
    )

    fun inspectWindowTree(
        rootNode: AccessibilityNodeInfo?,
        packageName: String,
        allowInstagramDmReels: Boolean
    ): DetectionMatch? {
        if (rootNode == null) return null

        val candidateRules = detectorRules.filter { packageName in it.packageNames }
        if (candidateRules.isEmpty()) return null

        val discoveredViewIds = mutableSetOf<String>()
        val discoveredContentDescs = mutableSetOf<String>()
        val signatureBuilder = StringBuilder()

        collectNodeMetadata(
            node = rootNode,
            depth = 0,
            maxDepth = 18,
            viewIds = discoveredViewIds,
            contentDescs = discoveredContentDescs,
            signatureBuilder = signatureBuilder
        )

        for (rule in candidateRules) {
            // Check DM exception for Instagram Reels when allowInstagramDmReels is true
            if (rule.featureId == "INSTAGRAM_REELS" && allowInstagramDmReels) {
                val hasDmReplyBar = rule.exceptionViewIdSuffixes.any { suffix ->
                    discoveredViewIds.any { it.endsWith(suffix, ignoreCase = true) || it.contains(suffix, ignoreCase = true) }
                }
                if (hasDmReplyBar) continue
            }

            val viewMatched = rule.viewIdSuffixes.isNotEmpty() && rule.viewIdSuffixes.any { suffix ->
                discoveredViewIds.any { id -> id.endsWith(suffix, ignoreCase = true) || id.contains(suffix, ignoreCase = true) }
            }
            val descMatched = rule.contentDescriptions.isNotEmpty() && rule.contentDescriptions.any { pattern ->
                discoveredContentDescs.any { desc -> desc.contains(pattern, ignoreCase = true) }
            }

            if (viewMatched || descMatched) {
                return DetectionMatch(
                    featureId = rule.featureId,
                    title = rule.title,
                    packageName = packageName,
                    reelSignature = signatureBuilder.toString().take(120),
                    defaultMode = rule.defaultMode
                )
            }
        }
        return null
    }

    fun extractBrowserUrl(rootNode: AccessibilityNodeInfo?, packageName: String): String? {
        if (rootNode == null) return null
        val urlBarSuffix = supportedBrowsers[packageName] ?: return null
        return findUrlText(rootNode, urlBarSuffix, 0)
    }

    private fun findUrlText(node: AccessibilityNodeInfo?, suffix: String, depth: Int): String? {
        if (node == null || depth > 14) return null
        val viewId = node.viewIdResourceName ?: ""
        if (viewId.endsWith(suffix, ignoreCase = true) || viewId.contains("url_bar", ignoreCase = true)) {
            val text = node.text?.toString()?.trim()
            if (!text.isNullOrBlank()) return text
        }
        val childCount = node.childCount
        for (i in 0 until childCount) {
            val found = findUrlText(node.getChild(i), suffix, depth + 1)
            if (found != null) return found
        }
        return null
    }

    private fun collectNodeMetadata(
        node: AccessibilityNodeInfo?,
        depth: Int,
        maxDepth: Int,
        viewIds: MutableSet<String>,
        contentDescs: MutableSet<String>,
        signatureBuilder: StringBuilder
    ) {
        if (node == null || depth > maxDepth) return
        node.viewIdResourceName?.let { id ->
            viewIds.add(id)
            if (id.contains("title", ignoreCase = true) || id.contains("desc", ignoreCase = true) || id.contains("reel", ignoreCase = true)) {
                node.text?.toString()?.takeIf { it.isNotBlank() }?.let {
                    if (signatureBuilder.length < 120) {
                        signatureBuilder.append(it).append("|")
                    }
                }
            }
        }
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { desc ->
            contentDescs.add(desc)
            if (signatureBuilder.length < 120) {
                signatureBuilder.append(desc.take(30)).append("|")
            }
        }
        val count = node.childCount
        for (i in 0 until count) {
            collectNodeMetadata(
                node = node.getChild(i),
                depth = depth + 1,
                maxDepth = maxDepth,
                viewIds = viewIds,
                contentDescs = contentDescs,
                signatureBuilder = signatureBuilder
            )
        }
    }
}
