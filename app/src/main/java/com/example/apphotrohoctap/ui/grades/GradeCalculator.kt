package com.example.apphotrohoctap.ui.grades

object GradeCalculator {

    /**
     * Chuyển đổi từ Điểm chữ (HUST) sang Điểm hệ 4.
     */
    fun gradeToGpa4(grade: String): Double {
        return when (grade.uppercase()) {
            "A+" -> 4.0
            "A" -> 4.0
            "B+" -> 3.5
            "B" -> 3.0
            "C+" -> 2.5
            "C" -> 2.0
            "D+" -> 1.5
            "D" -> 1.0
            else -> 0.0
        }
    }

    /**
     * Lấy mốc điểm hệ 10 tối thiểu để đạt được Điểm chữ.
     */
    fun getMinScoreForGrade(grade: String): Double {
        return when (grade.uppercase()) {
            "A+", "A" -> 8.5
            "B+" -> 8.0
            "B" -> 7.0
            "C+" -> 6.5
            "C" -> 5.5
            "D+" -> 5.0
            "D" -> 4.0
            else -> 0.0
        }
    }

    /**
     * Tính tổng số tín chỉ tích lũy và GPA hiện tại.
     * @param subjects Danh sách các cặp (Số tín chỉ, Điểm hệ 10 tổng kết môn)
     * @return Cặp (Tổng tín chỉ, GPA hệ 4)
     */
    fun calculateOverallGpa(subjects: List<Pair<Int, Double>>): Pair<Int, Double> {
        var totalCredits = 0
        var totalPoints = 0.0

        for ((credits, finalScore) in subjects) {
            val letterGrade = getLetterGradeFromScore(finalScore)
            val gpa4 = gradeToGpa4(letterGrade)
            totalCredits += credits
            totalPoints += gpa4 * credits
        }

        val gpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0
        return Pair(totalCredits, gpa)
    }

    /**
     * Chuyển từ điểm hệ 10 sang điểm chữ (Chuẩn HUST).
     */
    fun getLetterGradeFromScore(score: Double): String {
        return when {
            score >= 8.5 -> "A"
            score >= 8.0 -> "B+"
            score >= 7.0 -> "B"
            score >= 6.5 -> "C+"
            score >= 5.5 -> "C"
            score >= 5.0 -> "D+"
            score >= 4.0 -> "D"
            else -> "F"
        }
    }

    /**
     * Tính điểm cần thiết cho phần trăm trọng số còn lại để đạt mục tiêu.
     * @param targetGrade Điểm chữ mục tiêu (Ví dụ: "B+")
     * @param components Danh sách các điểm thành phần (Trọng số %, Điểm đạt được)
     * @return Điểm số (hệ 10) cần đạt trên phần trăm còn lại.
     *         Sẽ trả về null nếu đã quá 100% trọng số hoặc mục tiêu bất khả thi (> 10.0)
     */
    fun calculateRequiredScore(targetGrade: String, components: List<Pair<Double, Double?>>): Double? {
        val targetScore10 = getMinScoreForGrade(targetGrade)
        var accumulatedScore = 0.0
        var completedWeight = 0.0

        for ((weight, score) in components) {
            if (score != null) {
                accumulatedScore += (weight / 100.0) * score
                completedWeight += weight
            }
        }

        val missingWeight = 100.0 - completedWeight

        // Nếu đã hoàn thành đủ 100% trọng số, không tính nữa.
        if (missingWeight <= 0.0) return null

        val requiredScore = (targetScore10 - accumulatedScore) / (missingWeight / 100.0)

        return when {
            requiredScore > 10.0 -> null // Bất khả thi
            requiredScore < 0.0 -> 0.0   // Chắc chắn đạt
            else -> requiredScore
        }
    }
}
