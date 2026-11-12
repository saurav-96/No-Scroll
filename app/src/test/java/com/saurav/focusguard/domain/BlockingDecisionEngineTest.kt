package com.saurav.focusguard.domain

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BlockingDecisionEngineTest {

    private lateinit var engine: BlockingDecisionEngine

    @Before
    fun setUp() {
        engine = BlockingDecisionEngine()
    }

    @Test
    fun `blockAll plan blocks immediately when not paused and mode is AS_PER_PLAN`() {
        val decision = engine.evaluate(
            platformId = "YOUTUBE_SHORTS",
            restrictionMode = PlatformRestrictionMode.AS_PER_PLAN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = 10_000L
        )
        assertTrue(decision is BlockDecision.Block)
    }

    @Test
    fun `global pause allows playback even under Block All plan`() {
        val decision = engine.evaluate(
            platformId = "YOUTUBE_SHORTS",
            restrictionMode = PlatformRestrictionMode.AS_PER_PLAN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 50_000L,
            nowMillis = 10_000L
        )
        assertTrue(decision is BlockDecision.Allow)
    }

    @Test
    fun `dontBlock override allows playback even under Block All plan`() {
        val decision = engine.evaluate(
            platformId = "WHATSAPP_STATUS",
            restrictionMode = PlatformRestrictionMode.DONT_BLOCK,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = 10_000L
        )
        assertTrue(decision is BlockDecision.Allow)
    }

    @Test
    fun `allow1Min allows for 60s then blocks for 15s cooldown then resets`() {
        val start = 1_000_000L
        // First evaluation starts 60s window
        val d1 = engine.evaluate(
            platformId = "REDDIT_REELS",
            restrictionMode = PlatformRestrictionMode.ALLOW_1_MIN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = start
        )
        assertTrue(d1 is BlockDecision.Allow)

        // At 59s -> still allowed
        val d2 = engine.evaluate(
            platformId = "REDDIT_REELS",
            restrictionMode = PlatformRestrictionMode.ALLOW_1_MIN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = start + 59_000L
        )
        assertTrue(d2 is BlockDecision.Allow)

        // At 61s -> window expired, enters 15s cooldown and blocks
        val d3 = engine.evaluate(
            platformId = "REDDIT_REELS",
            restrictionMode = PlatformRestrictionMode.ALLOW_1_MIN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = start + 61_000L
        )
        assertTrue(d3 is BlockDecision.Block)

        // 10s into cooldown -> still blocked
        val d4 = engine.evaluate(
            platformId = "REDDIT_REELS",
            restrictionMode = PlatformRestrictionMode.ALLOW_1_MIN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = start + 71_000L
        )
        assertTrue(d4 is BlockDecision.Block)

        // After 15s cooldown finishes (61s + 15s = 76s) -> starts fresh window and allows
        val d5 = engine.evaluate(
            platformId = "REDDIT_REELS",
            restrictionMode = PlatformRestrictionMode.ALLOW_1_MIN,
            activePlan = FocusPlan.BLOCK_ALL,
            pausedUntilMillis = 0L,
            nowMillis = start + 77_000L
        )
        assertTrue(d5 is BlockDecision.Allow)
    }

    @Test
    fun `oneReel plan allows first reel and blocks on vertical scroll after 3s grace period`() {
        val start = 500_000L
        // First reel opens -> allowed
        val d1 = engine.evaluate(
            platformId = "INSTAGRAM_REELS",
            restrictionMode = PlatformRestrictionMode.AS_PER_PLAN,
            activePlan = FocusPlan.ONE_REEL,
            pausedUntilMillis = 0L,
            nowMillis = start,
            reelSignature = "reel_A",
            isVerticalScrollEvent = false
        )
        assertTrue(d1 is BlockDecision.Allow)

        // Within 3s grace period -> still allowed
        val d2 = engine.evaluate(
            platformId = "INSTAGRAM_REELS",
            restrictionMode = PlatformRestrictionMode.AS_PER_PLAN,
            activePlan = FocusPlan.ONE_REEL,
            pausedUntilMillis = 0L,
            nowMillis = start + 1_500L,
            reelSignature = "reel_A",
            isVerticalScrollEvent = true
        )
        assertTrue(d2 is BlockDecision.Allow)

        // After 3s grace period, vertical scroll to next reel -> blocks
        val d3 = engine.evaluate(
            platformId = "INSTAGRAM_REELS",
            restrictionMode = PlatformRestrictionMode.AS_PER_PLAN,
            activePlan = FocusPlan.ONE_REEL,
            pausedUntilMillis = 0L,
            nowMillis = start + 4_000L,
            reelSignature = "reel_B",
            isVerticalScrollEvent = true
        )
        assertTrue(d3 is BlockDecision.Block)
    }
}
