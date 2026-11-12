package com.saurav.focusguard.ui.pin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saurav.focusguard.data.prefs.FocusPreferencesRepository
import com.saurav.focusguard.data.prefs.FocusPreferencesState
import com.saurav.focusguard.domain.MindfulQuotes
import com.saurav.focusguard.domain.PinValidator
import com.saurav.focusguard.ui.theme.CardNavy
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.theme.FocusGuardTheme
import com.saurav.focusguard.ui.theme.PrimaryMagenta
import com.saurav.focusguard.ui.theme.TextPrimaryWhite
import com.saurav.focusguard.ui.theme.TextSecondarySlate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PinBlockOverlayActivity : ComponentActivity() {

    @Inject lateinit var prefsRepository: FocusPreferencesRepository
    @Inject lateinit var pinValidator: PinValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reason = intent.getStringExtra(EXTRA_REASON) ?: "Protected by NoScroll Shield"
        val quote = MindfulQuotes.quotes.random()

        setContent {
            FocusGuardTheme {
                val prefs by prefsRepository.stateFlow.collectAsState(initial = FocusPreferencesState())
                var enteredPin by remember { mutableStateOf("") }
                var errorMessage by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepSpaceNavy)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(PrimaryMagenta),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = reason,
                            color = TextPrimaryWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"$quote\"",
                            color = TextSecondarySlate,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // PIN Dots
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = enteredPin.map { '●' }.joinToString("  ").ifEmpty { "Enter 4-Digit PIN" },
                            color = if (enteredPin.isEmpty()) TextSecondarySlate else PrimaryMagenta,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (errorMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = errorMessage, color = Color(0xFFFF5252), fontSize = 13.sp)
                        }
                    }

                    // Numeric Keypad
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("Clear", "0", "Exit")
                        )
                        rows.forEach { rowKeys ->
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                rowKeys.forEach { key ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 86.dp, height = 58.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(CardNavy)
                                            .clickable {
                                                when (key) {
                                                    "Clear" -> {
                                                        enteredPin = ""
                                                        errorMessage = ""
                                                    }
                                                    "Exit" -> goHome()
                                                    else -> {
                                                        if (enteredPin.length < 4) {
                                                            enteredPin += key
                                                            if (enteredPin.length == 4) {
                                                                val valid = pinValidator.validate(
                                                                    enteredPin = enteredPin,
                                                                    option = prefs.pinOption,
                                                                    customPin = prefs.customPin,
                                                                    timeFormat = prefs.pinTimeFormat,
                                                                    dateFormat = prefs.pinDateFormat
                                                                )
                                                                if (valid) {
                                                                    finish()
                                                                } else {
                                                                    errorMessage = "Incorrect PIN"
                                                                    enteredPin = ""
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            color = TextPrimaryWhite,
                                            fontSize = if (key.length == 1) 20.sp else 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    companion object {
        const val EXTRA_TARGET_PACKAGE = "extra_target_package"
        const val EXTRA_REASON = "extra_reason"
    }
}
