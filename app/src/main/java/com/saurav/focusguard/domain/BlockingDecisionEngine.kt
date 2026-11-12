package com.saurav.focusguard.domain

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

sealed class BlockDecision {
    data class Allow(val remainingWindowMillis: Long? = null) : BlockDecision()
    data class Block(val reason: String) : BlockDecision()
}

data class TimedWindowState(
    val windowStartedAtMillis: Long,
    val calmDownUntilMillis: Long = 0L
)

data class OneReelPlayStatus(
    val reelStartedOnMillis: Long,
    val lastReelSignature: String
)

@Singleton
class BlockingDecisionEngine @Inject constructor() {

    private val platformTimedWindows = ConcurrentHashMap<String, TimedWindowState>()
    private val oneReelStates = ConcurrentHashMap<String, OneReelPlayStatus>()
    private var curiousWindowState: TimedWindowState? = null

    fun resetPlatformState(platformId: String) {
        platformTimedWindows.remove(platformId)
        oneReelStates.remove(platformId)
    }

    fun resetAllStates() {
        platformTimedWindows.clear()
        oneReelStates.clear()
        curiousWindowState = null
    }

    fun evaluate(
        platformId: String,
        restrictionMode: PlatformRestrictionMode,
        activePlan: FocusPlan,
        pausedUntilMillis: Long,
        nowMillis: Long,
        reelSignature: String = "",
        isVerticalScrollEvent: Boolean = false,
        curiousAllowedMillis: Long = 300_000L,
        curiousCalmDownMillis: Long = 300_000L
    ): BlockDecision {
        // 1. Check global pause
        if (pausedUntilMillis > nowMillis) {
            return BlockDecision.Allow(remainingWindowMillis = pausedUntilMillis - nowMillis)
        }

        // 2. Check platform-specific override
        return when (restrictionMode) {
            PlatformRestrictionMode.DONT_BLOCK -> BlockDecision.Allow()
            PlatformRestrictionMode.ALWAYS_BLOCK -> BlockDecision.Block("Always Block enabled for $platformId")
            PlatformRestrictionMode.ALLOW_1_MIN,
            PlatformRestrictionMode.ALLOW_5_MIN,
            PlatformRestrictionMode.ALLOW_10_MIN -> evaluateTimedWindow(
                key = platformId,
                allowedMillis = restrictionMode.allowedMillis,
                calmDownMillis = restrictionMode.calmDownMillis,
                nowMillis = nowMillis
            )
            PlatformRestrictionMode.AS_PER_PLAN -> evaluateActivePlan(
                platformId = platformId,
                activePlan = activePlan,
                nowMillis = nowMillis,
                reelSignature = reelSignature,
                isVerticalScrollEvent = isVerticalScrollEvent,
                curiousAllowedMillis = curiousAllowedMillis,
                curiousCalmDownMillis = curiousCalmDownMillis
            )
        }
    }

    private fun evaluateActivePlan(
        platformId: String,
        activePlan: FocusPlan,
        nowMillis: Long,
        reelSignature: String,
        isVerticalScrollEvent: Boolean,
        curiousAllowedMillis: Long,
        curiousCalmDownMillis: Long
    ): BlockDecision {
        return when (activePlan) {
            FocusPlan.BLOCK_ALL -> BlockDecision.Block("Block All plan active")
            FocusPlan.CURIOUS -> evaluateCuriousPlan(
                nowMillis = nowMillis,
                allowedMillis = curiousAllowedMillis,
                calmDownMillis = curiousCalmDownMillis
            )
            FocusPlan.ONE_REEL -> evaluateOneReelPlan(
                platformId = platformId,
                nowMillis = nowMillis,
                reelSignature = reelSignature,
                isVerticalScrollEvent = isVerticalScrollEvent
            )
        }
    }

    private fun evaluateTimedWindow(
        key: String,
        allowedMillis: Long,
        calmDownMillis: Long,
        nowMillis: Long
    ): BlockDecision {
        val current = platformTimedWindows[key]
        if (current != null) {
            if (current.calmDownUntilMillis > nowMillis) {
                return BlockDecision.Block("Cooldown active (${(current.calmDownUntilMillis - nowMillis) / 1000}s left)")
            }
            if (current.calmDownUntilMillis in 1..nowMillis) {
                // Cooldown finished; start a fresh window
                val fresh = TimedWindowState(windowStartedAtMillis = nowMillis)
                platformTimedWindows[key] = fresh
                return BlockDecision.Allow(remainingWindowMillis = allowedMillis)
            }
            val elapsed = nowMillis - current.windowStartedAtMillis
            return if (elapsed < allowedMillis) {
                BlockDecision.Allow(remainingWindowMillis = allowedMillis - elapsed)
            } else {
                // Allowed time elapsed -> enter cooldown and block
                platformTimedWindows[key] = current.copy(calmDownUntilMillis = nowMillis + calmDownMillis)
                BlockDecision.Block("Allowed time window expired")
            }
        } else {
            val fresh = TimedWindowState(windowStartedAtMillis = nowMillis)
            platformTimedWindows[key] = fresh
            return BlockDecision.Allow(remainingWindowMillis = allowedMillis)
        }
    }

    private fun evaluateCuriousPlan(
        nowMillis: Long,
        allowedMillis: Long,
        calmDownMillis: Long
    ): BlockDecision {
        val current = curiousWindowState
        if (current != null) {
            if (current.calmDownUntilMillis > nowMillis) {
                return BlockDecision.Block("Curious mode cooldown active")
            }
            if (current.calmDownUntilMillis in 1..nowMillis) {
                val fresh = TimedWindowState(windowStartedAtMillis = nowMillis)
                curiousWindowState = fresh
                return BlockDecision.Allow(remainingWindowMillis = allowedMillis)
            }
            val elapsed = nowMillis - current.windowStartedAtMillis
            return if (elapsed < allowedMillis) {
                BlockDecision.Allow(remainingWindowMillis = allowedMillis - elapsed)
            } else {
                curiousWindowState = current.copy(calmDownUntilMillis = nowMillis + calmDownMillis)
                BlockDecision.Block("Curious session completed")
            }
        } else {
            val fresh = TimedWindowState(windowStartedAtMillis = nowMillis)
            curiousWindowState = fresh
            return BlockDecision.Allow(remainingWindowMillis = allowedMillis)
        }
    }

    /**
     * Matches NoScrollServiceModel.processAndBlockShortContent$handleOneReel:
     * - First reel creates OneReelPlayStatus(reelStartedOnMillis = now, lastReelSignature = signature)
     * - 3,000 ms grace period after opening the first reel
     * - Blocks if user scrolls vertically to next reel, signature changes, or 5 minutes (300,000 ms) elapses
     */
    private fun evaluateOneReelPlan(
        platformId: String,
        nowMillis: Long,
        reelSignature: String,
        isVerticalScrollEvent: Boolean
    ): BlockDecision {
        val status = oneReelStates[platformId]
        if (status == null) {
            oneReelStates[platformId] = OneReelPlayStatus(
                reelStartedOnMillis = nowMillis,
                lastReelSignature = reelSignature
            )
            return BlockDecision.Allow(remainingWindowMillis = ONE_REEL_MAX_DURATION_MS)
        }

        val elapsed = nowMillis - status.reelStartedOnMillis
        if (elapsed >= ONE_REEL_MAX_DURATION_MS) {
            oneReelStates.remove(platformId)
            return BlockDecision.Block("One Reel max duration (5m) reached")
        }

        if (elapsed > ONE_REEL_GRACE_PERIOD_MS) {
            val signatureChanged = reelSignature.isNotBlank() &&
                status.lastReelSignature.isNotBlank() &&
                reelSignature != status.lastReelSignature
            if (isVerticalScrollEvent || signatureChanged) {
                oneReelStates.remove(platformId)
                return BlockDecision.Block("One Reel limit reached on swipe")
            }
        }

        return BlockDecision.Allow(remainingWindowMillis = ONE_REEL_MAX_DURATION_MS - elapsed)
    }

    companion object {
        const val ONE_REEL_GRACE_PERIOD_MS = 3_000L
        const val ONE_REEL_MAX_DURATION_MS = 300_000L
    }
}
