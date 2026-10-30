package com.example.apphotrohoctap.ui.grades

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GradeCalculatorTest {

    @Test
    fun calculateRequiredScore_isCorrect() {
        // Môn học cần đạt B+ (8.0 điểm)
        // Chuyên cần 10% được 10 điểm
        // Giữa kỳ 30% được 8 điểm
        // Cuối kỳ 60% chưa có điểm.
        // Tích lũy hiện tại: 10*0.1 + 8*0.3 = 1.0 + 2.4 = 3.4
        // Còn lại: 8.0 - 3.4 = 4.6
        // Điểm cần cho 60% còn lại: 4.6 / 0.6 = 7.6666
        
        val components = listOf(
            Pair(10.0, 10.0),
            Pair(30.0, 8.0),
            Pair(60.0, null)
        )
        
        val required = GradeCalculator.calculateRequiredScore("B+", components)
        assertEquals(7.6666, required!!, 0.001)
    }

    @Test
    fun calculateRequiredScore_returnsNull_ifImpossible() {
        // Cần đạt A (8.5 điểm)
        // Chuyên cần 10% được 5 điểm
        // Giữa kỳ 30% được 4 điểm
        // Tích lũy: 0.5 + 1.2 = 1.7
        // Còn lại: 8.5 - 1.7 = 6.8
        // Điểm cần cho 60% còn lại: 6.8 / 0.6 = 11.33 > 10.0 => Bất khả thi (Null)
        
        val components = listOf(
            Pair(10.0, 5.0),
            Pair(30.0, 4.0),
            Pair(60.0, null)
        )
        
        val required = GradeCalculator.calculateRequiredScore("A", components)
        assertNull(required)
    }

    @Test
    fun calculateOverallGpa_isCorrect() {
        val subjects = listOf(
            Pair(3, 8.5), // 3 tín chỉ, A (4.0) -> 12
            Pair(2, 7.5), // 2 tín chỉ, B (3.0) -> 6
            Pair(3, 6.0)  // 3 tín chỉ, C (2.0) -> 6
        )
        // Tổng tín = 8. Tổng điểm = 24. GPA = 3.0
        
        val (credits, gpa) = GradeCalculator.calculateOverallGpa(subjects)
        assertEquals(8, credits)
        assertEquals(3.0, gpa, 0.01)
    }
}
