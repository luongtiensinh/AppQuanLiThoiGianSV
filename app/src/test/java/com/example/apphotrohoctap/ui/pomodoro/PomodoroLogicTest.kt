package com.example.apphotrohoctap.ui.pomodoro

import org.junit.Assert.assertEquals
import org.junit.Test

class PomodoroLogicTest {

    @Test
    fun nextMode_afterFirstFocus_isShortBreak() {
        val result = PomodoroLogic.nextMode(PomodoroMode.FOCUS, 1)
        assertEquals(PomodoroMode.SHORT_BREAK, result)
    }

    @Test
    fun nextMode_afterThirdFocus_isShortBreak() {
        val result = PomodoroLogic.nextMode(PomodoroMode.FOCUS, 3)
        assertEquals(PomodoroMode.SHORT_BREAK, result)
    }

    @Test
    fun nextMode_afterFourthFocus_isLongBreak() {
        val result = PomodoroLogic.nextMode(PomodoroMode.FOCUS, 4)
        assertEquals(PomodoroMode.LONG_BREAK, result)
    }

    @Test
    fun nextMode_afterShortBreak_isFocus() {
        val result = PomodoroLogic.nextMode(PomodoroMode.SHORT_BREAK, 1)
        assertEquals(PomodoroMode.FOCUS, result)
    }

    @Test
    fun nextMode_afterLongBreak_isFocus() {
        val result = PomodoroLogic.nextMode(PomodoroMode.LONG_BREAK, 4)
        assertEquals(PomodoroMode.FOCUS, result)
    }

    @Test
    fun remainingMillis_whenEndTimeIsFuture_isCorrect() {
        val now = 1000L
        val end = 5000L
        assertEquals(4000L, PomodoroLogic.remainingMillis(end, now))
    }

    @Test
    fun remainingMillis_whenEndTimeIsPast_isZero() {
        val now = 5000L
        val end = 1000L
        assertEquals(0L, PomodoroLogic.remainingMillis(end, now))
    }
}
