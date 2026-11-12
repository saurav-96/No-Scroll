package com.saurav.focusguard.ui.pin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saurav.focusguard.domain.PinDateFormat
import com.saurav.focusguard.domain.PinOption
import com.saurav.focusguard.domain.PinTimeFormat
import com.saurav.focusguard.domain.PinValidator
import com.saurav.focusguard.ui.MainViewModel
import com.saurav.focusguard.ui.theme.CardNavy
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.theme.InnerDarkNavy
import com.saurav.focusguard.ui.theme.PrimaryMagenta
import com.saurav.focusguard.ui.theme.TextPrimaryWhite
import com.saurav.focusguard.ui.theme.TextSecondarySlate
import com.saurav.focusguard.ui.theme.TopRadialMagenta

@Composable
fun PinConfigScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs by viewModel.prefsState.collectAsState()
    val validator = remember { PinValidator() }

    var selectedOption by remember(prefs.pinOption) { mutableStateOf(prefs.pinOption) }
    var customPin by remember(prefs.customPin) { mutableStateOf(prefs.customPin) }
    var selectedTimeFormat by remember(prefs.pinTimeFormat) { mutableStateOf(prefs.pinTimeFormat) }
    var selectedDateFormat by remember(prefs.pinDateFormat) { mutableStateOf(prefs.pinDateFormat) }

    val livePinPreview = validator.expectedPin(
        option = selectedOption,
        customPin = customPin,
        timeFormat = selectedTimeFormat,
        dateFormat = selectedDateFormat
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TopRadialMagenta, DeepSpaceNavy, DeepSpaceNavy)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CardNavy)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Configure NoScroll PIN",
                            color = TextPrimaryWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Dynamic & static PIN protection for locked apps",
                            color = TextSecondarySlate,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Live PIN preview banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardNavy)
                        .border(1.dp, PrimaryMagenta, RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PrimaryMagenta
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Current Active PIN",
                                color = TextPrimaryWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedOption.displayName,
                                color = TextSecondarySlate,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text(
                        text = if (selectedOption == PinOption.NONE) "OFF" else livePinPreview,
                        color = PrimaryMagenta,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }

            // PIN Options
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(CardNavy)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PinOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selectedOption == option) InnerDarkNavy else Color.Transparent)
                                .clickable { selectedOption = option }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == option,
                                onClick = { selectedOption = option },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryMagenta)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = option.displayName,
                                    color = TextPrimaryWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = option.description,
                                    color = TextSecondarySlate,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (selectedOption == PinOption.CUSTOM || selectedOption == PinOption.DEVICE_DEFAULT) {
                item {
                    OutlinedTextField(
                        value = customPin,
                        onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) customPin = it },
                        label = { Text("Enter 4-digit PIN", color = TextSecondarySlate) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryMagenta,
                            unfocusedBorderColor = CardNavy,
                            focusedContainerColor = CardNavy,
                            unfocusedContainerColor = CardNavy,
                            focusedTextColor = TextPrimaryWhite,
                            unfocusedTextColor = TextPrimaryWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (selectedOption == PinOption.TIME) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardNavy)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TIME PIN FORMAT",
                            color = TextSecondarySlate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        PinTimeFormat.entries.forEach { fmt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTimeFormat = fmt }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimeFormat == fmt,
                                    onClick = { selectedTimeFormat = fmt },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryMagenta)
                                )
                                Text(text = fmt.label, color = TextPrimaryWhite)
                            }
                        }
                    }
                }
            }

            if (selectedOption == PinOption.DATE) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardNavy)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DATE PIN FORMAT",
                            color = TextSecondarySlate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        PinDateFormat.entries.forEach { fmt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDateFormat = fmt }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDateFormat == fmt,
                                    onClick = { selectedDateFormat = fmt },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryMagenta)
                                )
                                Text(text = fmt.label, color = TextPrimaryWhite)
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(PrimaryMagenta)
                        .clickable {
                            viewModel.savePinConfiguration(
                                option = selectedOption,
                                customPin = customPin.ifBlank { "0000" },
                                timeFormat = selectedTimeFormat,
                                dateFormat = selectedDateFormat
                            )
                            Toast.makeText(context, "PIN Configuration Saved", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save PIN Settings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
