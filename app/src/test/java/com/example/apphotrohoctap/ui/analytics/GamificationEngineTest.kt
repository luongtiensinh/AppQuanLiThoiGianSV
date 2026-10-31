package com.example.apphotrohoctap.ui.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun evaluateBadges_unlocksFirstStep_whenTaskCountAtLeast1() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 1,
            completedPomodoroSessions = 0,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.5f
        )

        val firstStepBadge = badges.find { it.id == "first_step" }!!
        assertTrue(firstStepBadge.isUnlocked)
        assertEquals("Đã đạt", firstStepBadge.progressText)
    }

    @Test
    fun evaluateBadges_unlocksPomoMaster_whenPomodoroAtLeast5() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 0,
            completedPomodoroSessions = 5,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.0f
        )

        val pomoBadge = badges.find { it.id == "pomo_master" }!!
        assertTrue(pomoBadge.isUnlocked)
    }

    @Test
    fun evaluateBadges_locksNightOwl_whenNoLateNightTask() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 2,
            completedPomodoroSessions = 3,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.8f
        )

        val nightBadge = badges.find { it.id == "night_owl" }!!
        assertFalse(nightBadge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksGoldenWeek_whenRateIs100Percent() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 4,
            completedPomodoroSessions = 2,
            hasLateNightTask = true,
            weeklyCompletionRate = 1.0f
        )

        val goldenBadge = badges.find { it.id == "golden_week" }!!
        assertTrue(goldenBadge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksEarlyBird_whenHasEarlyBirdTask() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 1,
            completedPomodoroSessions = 0,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.2f,
            hasEarlyBirdTask = true
        )

        val badge = badges.find { it.id == "early_bird" }!!
        assertTrue(badge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksTaskMaster_when10TasksCompleted() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 10,
            completedPomodoroSessions = 0,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.5f
        )

        val badge = badges.find { it.id == "task_master" }!!
        assertTrue(badge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksPomoGod_when10PomodoroSessionsCompleted() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 0,
            completedPomodoroSessions = 10,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.0f
        )

        val badge = badges.find { it.id == "pomo_god" }!!
        assertTrue(badge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksGpaHunter_whenSubjectCountAtLeast3() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 0,
            completedPomodoroSessions = 0,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.0f,
            subjectCount = 3
        )

        val badge = badges.find { it.id == "gpa_hunter" }!!
        assertTrue(badge.isUnlocked)
    }

    @Test
    fun evaluateBadges_unlocksScheduleMaster_whenHasFullSchedule() {
        val badges = GamificationEngine.evaluateBadges(
            completedTasksCount = 0,
            completedPomodoroSessions = 0,
            hasLateNightTask = false,
            weeklyCompletionRate = 0.0f,
            hasFullSchedule = true
        )

        val badge = badges.find { it.id == "schedule_master" }!!
        assertTrue(badge.isUnlocked)
    }
}
