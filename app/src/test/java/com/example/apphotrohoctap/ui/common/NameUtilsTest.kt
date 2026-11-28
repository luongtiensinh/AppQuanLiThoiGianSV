package com.example.apphotrohoctap.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class NameUtilsTest {

    @Test
    fun testVietnameseStandardName_returnsLastName() {
        val result = NameUtils.extractFirstName("Nguyễn Lương Tiến")
        assertEquals("Tiến", result)
    }

    @Test
    fun testGoogleWesternFormatName_returnsFirstName() {
        val result = NameUtils.extractFirstName("Tien Nguyen")
        assertEquals("Tien", result)
    }

    @Test
    fun testEmailFormat_returnsUsernameWithCapital() {
        val result = NameUtils.extractFirstName("tiennguyen@gmail.com")
        assertEquals("Tiennguyen", result)
    }

    @Test
    fun testSingleWordName_returnsCapitalizedWord() {
        val result = NameUtils.extractFirstName("sinhvien")
        assertEquals("Sinhvien", result)
    }

    @Test
    fun testNullOrBlankName_returnsDefault() {
        assertEquals("Sinh viên", NameUtils.extractFirstName(null))
        assertEquals("Sinh viên", NameUtils.extractFirstName(""))
        assertEquals("Sinh viên", NameUtils.extractFirstName("   "))
    }
}
