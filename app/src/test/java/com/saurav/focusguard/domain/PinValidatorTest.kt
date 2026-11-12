package com.saurav.focusguard.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class PinValidatorTest {

    private val validator = PinValidator()
    private val sampleTime = LocalDateTime.of(2026, 11, 8, 14, 37)

    @Test
    fun `custom pin validates exact match`() {
        assertTrue(
            validator.validate(
                enteredPin = "2580",
                option = PinOption.CUSTOM,
                customPin = "2580",
                dateTime = sampleTime
            )
        )
        assertFalse(
            validator.validate(
                enteredPin = "0000",
                option = PinOption.CUSTOM,
                customPin = "2580",
                dateTime = sampleTime
            )
        )
    }

    @Test
    fun `dynamic time pin formats 24h 12h and reverse 24h accurately`() {
        assertEquals(
            "1437",
            validator.expectedPin(PinOption.TIME, "0000", PinTimeFormat.HH_MM_24, PinDateFormat.DD_MM, sampleTime)
        )
        assertEquals(
            "0237",
            validator.expectedPin(PinOption.TIME, "0000", PinTimeFormat.HH_MM_12, PinDateFormat.DD_MM, sampleTime)
        )
        assertEquals(
            "3714",
            validator.expectedPin(PinOption.TIME, "0000", PinTimeFormat.MM_HH_24, PinDateFormat.DD_MM, sampleTime)
        )
    }

    @Test
    fun `dynamic date pin formats DDMM and MMDD accurately`() {
        assertEquals(
            "0811",
            validator.expectedPin(PinOption.DATE, "0000", PinTimeFormat.HH_MM_24, PinDateFormat.DD_MM, sampleTime)
        )
        assertEquals(
            "1108",
            validator.expectedPin(PinOption.DATE, "0000", PinTimeFormat.HH_MM_24, PinDateFormat.MM_DD, sampleTime)
        )
    }
}
