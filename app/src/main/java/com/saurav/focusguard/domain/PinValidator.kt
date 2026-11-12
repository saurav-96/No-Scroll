package com.saurav.focusguard.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinValidator @Inject constructor() {

    fun expectedPin(
        option: PinOption,
        customPin: String,
        timeFormat: PinTimeFormat,
        dateFormat: PinDateFormat,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): String {
        return when (option) {
            PinOption.NONE -> ""
            PinOption.CUSTOM -> customPin.ifBlank { "0000" }
            PinOption.DEVICE_DEFAULT -> customPin.ifBlank { "1234" }
            PinOption.TIME -> {
                val pattern = when (timeFormat) {
                    PinTimeFormat.HH_MM_24 -> "HHmm"
                    PinTimeFormat.HH_MM_12 -> "hhmm"
                    PinTimeFormat.MM_HH_24 -> "mmHH"
                }
                dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.US))
            }
            PinOption.DATE -> {
                val pattern = when (dateFormat) {
                    PinDateFormat.DD_MM -> "ddMM"
                    PinDateFormat.MM_DD -> "MMdd"
                }
                dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.US))
            }
        }
    }

    fun validate(
        enteredPin: String,
        option: PinOption,
        customPin: String,
        timeFormat: PinTimeFormat = PinTimeFormat.HH_MM_24,
        dateFormat: PinDateFormat = PinDateFormat.DD_MM,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): Boolean {
        if (option == PinOption.NONE) return true
        val expected = expectedPin(option, customPin, timeFormat, dateFormat, dateTime)
        return enteredPin.trim() == expected
    }
}
