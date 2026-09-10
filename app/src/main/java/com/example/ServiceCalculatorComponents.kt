package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Background displaying the engine image with subtle opacity ("بە کاڵی")
 * as explicitly requested in the prompt.
 */
@Composable
fun FadedEngineBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(PetroleumDarkBg)) {
        // Engine photo rendered with faded opacity
        Image(
            painter = painterResource(id = R.drawable.img_engine_bg),
            contentDescription = "وێنەی بزوێنەر لە باگڕاوند بە کاڵی",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.17f // Faded ("بە کاڵی") as requested
        )
        // Dark vignette gradient overlay to ensure text and inputs remain ultra-sharp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PetroleumDarkBg.copy(alpha = 0.85f),
                            Color(0x99090E14),
                            PetroleumDarkBg.copy(alpha = 0.95f)
                        )
                    )
                )
        )
    }
}

/**
 * Top app bar with brigade insignia, proudly displaying "لیوای 1 نەوت"
 */
@Composable
fun BrigadeTopHeader(
    useKurdishDigits: Boolean,
    historyCount: Int,
    onToggleDigits: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = PetroleumNavy.copy(alpha = 0.92f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brigade Emblem and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_brigade_icon),
                            contentDescription = "درووشمی لیوای 1 نەوت",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column {
                        Text(
                            text = "لیوای 1 نەوت",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.testTag("app_title")
                        )
                        Text(
                            text = "هەژمارکردنی ساڵ و مانگی خزمەت",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SteelMuted,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Quick actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Kurdish/Arabic vs Latin digits toggle
                    IconButton(
                        onClick = onToggleDigits,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0x33D4AF37))
                            .testTag("toggle_digits_button")
                    ) {
                        Text(
                            text = if (useKurdishDigits) "١٢٣" else "123",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // History button with badge
                    BadgedBox(
                        badge = {
                            if (historyCount > 0) {
                                Badge(
                                    containerColor = GoldPrimary,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = ServiceCalculatorHelper.formatNumber(historyCount, useKurdishDigits),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onOpenHistory,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x2238BDF8))
                                .testTag("history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "مێژووی هەژمارەکان",
                                tint = SteelBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab Navigation bar to switch between calculation modes
 */
@Composable
fun CalculationModeSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple("هەژماری دەستی", Icons.Default.Calculate, 0),
        Triple("نێوان دوو بەروار", Icons.Default.DateRange, 1),
        Triple("پشکنینی خانەنشینی", Icons.Default.MilitaryTech, 2)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0x99142230),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { (title, icon, index) ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GoldPrimary else Color.Transparent)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF1B1202) else SteelMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1B1202) else SteelMuted,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-impact result card displaying computed years, months, and days
 */
@Composable
fun ServiceResultDisplayCard(
    result: CalculationResult,
    useKurdishDigits: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = PetroleumCardBg,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.7f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ئەنجامی کۆی گشتی خزمەت",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    )
                }

                if (result.years > 0 || result.months > 0 || result.days > 0) {
                    TextButton(
                        onClick = onReset,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "سڕینەوە",
                            tint = CrimsonAlert,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("سفرکردن", color = CrimsonAlert, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary 3 Stat Boxes (Years, Months, Days)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    label = "ساڵ",
                    value = ServiceCalculatorHelper.formatNumber(result.years, useKurdishDigits),
                    unitColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "مانگ",
                    value = ServiceCalculatorHelper.formatNumber(result.months, useKurdishDigits),
                    unitColor = SteelBlue,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "ڕۆژ",
                    value = ServiceCalculatorHelper.formatNumber(result.days, useKurdishDigits),
                    unitColor = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x440B131C))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "کۆی مانگەکان",
                        style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = "${ServiceCalculatorHelper.formatNumber(result.totalMonths, useKurdishDigits)} مانگ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = Color(0x33CBD5E1)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "کۆی ڕۆژەکان",
                        style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = "${ServiceCalculatorHelper.formatNumber(result.totalDays, useKurdishDigits)} ڕۆژ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp),
                    color = Color(0x33CBD5E1)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "هاوتای ساڵانە",
                        style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = "${ServiceCalculatorHelper.formatNumber(result.decimalYears, useKurdishDigits)} ساڵ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GoldLight, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons: Copy & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val textToCopy = "لیوای 1 نەوت - هەژمارکردنی خزمەت:\nکۆی گشتی: ${result.summaryText}\n(کۆی مانگەکان: ${result.totalMonths} مانگ | ڕۆژەکان: ${result.totalDays} ڕۆژ)"
                        clipboardManager.setText(AnnotatedString(textToCopy))
                        copied = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("copy_result_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SteelBlue.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "کۆپیکردن",
                        tint = SteelBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (copied) "کۆپی کرا!" else "کۆپیکردن",
                        color = SteelBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("save_calculation_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "پاشەکەوتکردن",
                        tint = Color(0xFF1E1303),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پاشەکەوتکردن",
                        color = Color(0xFF1E1303),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    unitColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0x660F1A24),
        border = androidx.compose.foundation.BorderStroke(1.dp, unitColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.ifBlank { "٠" },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = unitColor,
                    fontSize = 26.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

/**
 * Manual Period Entry card: User types Years, Months, and Days manually
 */
@Composable
fun ManualPeriodInputCard(
    period: ServicePeriod,
    index: Int,
    totalPeriods: Int,
    useKurdishDigits: Boolean,
    onYearsChange: (String) -> Unit,
    onMonthsChange: (String) -> Unit,
    onDaysChange: (String) -> Unit,
    onToggleDouble: () -> Unit,
    onToggleDeduction: () -> Unit,
    onRemove: () -> Unit,
    onQuickAdd: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = PetroleumCardBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (period.isDoubleService) GoldPrimary else if (period.isDeduction) CrimsonAlert else Color(0x33D4AF37)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header of period
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ServiceCalculatorHelper.formatNumber(index + 1, useKurdishDigits),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = period.title.ifBlank { "ماوەی خزمەت ${index + 1}" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    if (period.isDoubleService) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "دووقات (2x)",
                                color = GoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (period.isDeduction) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CrimsonAlert.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "لێدەرکردن (-)",
                                color = CrimsonAlert,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (totalPeriods > 1) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "سڕینەوەی ئەم ماوەیە",
                            tint = CrimsonAlert.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inputs: Year, Month, Day side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Years Input
                OutlinedTextField(
                    value = period.years,
                    onValueChange = onYearsChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_years_${period.id}"),
                    label = { Text("ساڵ", fontSize = 12.sp) },
                    placeholder = { Text("0", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x44CBD5E1),
                        focusedLabelColor = GoldLight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Months Input
                OutlinedTextField(
                    value = period.months,
                    onValueChange = onMonthsChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_months_${period.id}"),
                    label = { Text("مانگ", fontSize = 12.sp) },
                    placeholder = { Text("0", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SteelBlue,
                        unfocusedBorderColor = Color(0x44CBD5E1),
                        focusedLabelColor = SteelBlue,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Days Input
                OutlinedTextField(
                    value = period.days,
                    onValueChange = onDaysChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_days_${period.id}"),
                    label = { Text("ڕۆژ", fontSize = 12.sp) },
                    placeholder = { Text("0", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberAccent,
                        unfocusedBorderColor = Color(0x44CBD5E1),
                        focusedLabelColor = AmberAccent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick add chips (+1 Year, +6 Months, etc.)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickDurationChip(label = "+١ ساڵ", onClick = { onQuickAdd(1, 0) })
                QuickDurationChip(label = "+٣ ساڵ", onClick = { onQuickAdd(3, 0) })
                QuickDurationChip(label = "+٦ مانگ", onClick = { onQuickAdd(0, 6) })
                QuickDurationChip(label = "+١ مانگ", onClick = { onQuickAdd(0, 1) })
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Checkboxes: Double Service & Deduction
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleDouble() }
                        .padding(4.dp)
                ) {
                    Checkbox(
                        checked = period.isDoubleService,
                        onCheckedChange = { onToggleDouble() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldPrimary,
                            uncheckedColor = SteelMuted
                        )
                    )
                    Text(
                        text = "خزمەتی دووقات (سەنگەر)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (period.isDoubleService) GoldLight else TextSecondary,
                            fontWeight = if (period.isDoubleService) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleDeduction() }
                        .padding(4.dp)
                ) {
                    Checkbox(
                        checked = period.isDeduction,
                        onCheckedChange = { onToggleDeduction() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CrimsonAlert,
                            uncheckedColor = SteelMuted
                        )
                    )
                    Text(
                        text = "لێدەرکردن (مۆڵەت)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (period.isDeduction) CrimsonAlert else TextSecondary,
                            fontWeight = if (period.isDeduction) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuickDurationChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Color(0x3338BDF8),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, SteelBlue.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            color = SteelBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Date range calculator section
 */
@Composable
fun DateRangeCalculationCard(
    startYear: String,
    startMonth: String,
    startDay: String,
    endYear: String,
    endMonth: String,
    endDay: String,
    isDoubleService: Boolean,
    onStartDateChange: (String, String, String) -> Unit,
    onEndDateChange: (String, String, String) -> Unit,
    onSetToday: () -> Unit,
    onToggleDouble: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = PetroleumCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "دیاریکردنی ماوە لەنێوان دوو بەرواردا",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Start Date
            Text(
                text = "بەرواری دەستپێکردنی خزمەت:",
                style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.5.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startYear,
                    onValueChange = { onStartDateChange(it, startMonth, startDay) },
                    modifier = Modifier.weight(1.2f),
                    label = { Text("ساڵ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = startMonth,
                    onValueChange = { onStartDateChange(startYear, it, startDay) },
                    modifier = Modifier.weight(1f),
                    label = { Text("مانگ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = startDay,
                    onValueChange = { onStartDateChange(startYear, startMonth, it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("ڕۆژ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // End Date with "Today" quick button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "بەرواری کۆتایی خزمەت:",
                    style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.5.sp)
                )
                TextButton(
                    onClick = onSetToday,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Today, contentDescription = null, tint = SteelBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ئەمڕۆ", color = SteelBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = endYear,
                    onValueChange = { onEndDateChange(it, endMonth, endDay) },
                    modifier = Modifier.weight(1.2f),
                    label = { Text("ساڵ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endMonth,
                    onValueChange = { onEndDateChange(endYear, it, endDay) },
                    modifier = Modifier.weight(1f),
                    label = { Text("مانگ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endDay,
                    onValueChange = { onEndDateChange(endYear, endMonth, it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("ڕۆژ", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleDouble() }
                    .padding(4.dp)
            ) {
                Checkbox(
                    checked = isDoubleService,
                    onCheckedChange = { onToggleDouble() },
                    colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                )
                Text(
                    text = "هەژمارکردنی تەواوی ماوەکە بە دووقات (2x)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDoubleService) GoldLight else TextSecondary,
                        fontWeight = if (isDoubleService) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

/**
 * Retirement & Promotion Seniority Card
 */
@Composable
fun RetirementAndPromotionCard(
    currentYears: Int,
    currentMonths: Int,
    currentDays: Int,
    targetYears: Int,
    rankTargetYears: Int,
    useKurdishDigits: Boolean,
    onSetTargetYears: (Int) -> Unit,
    onSetRankTarget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val (remY, remM, remD) = ServiceCalculatorHelper.calculateRetirementRemaining(
        currentYears = currentYears,
        currentMonths = currentMonths,
        currentDays = currentDays,
        targetYears = targetYears
    )

    val progress = ((currentYears * 360f + currentMonths * 30f + currentDays) / (targetYears * 360f)).coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = PetroleumCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = GoldPrimary)
                Text(
                    text = "پشکنەری ماوەی خانەنشینی سەربازی",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target selection chips
            Text(
                text = "دیاریکردنی ساڵی پێویست بۆ خانەنشینی:",
                style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 20, 25, 30).forEach { years ->
                    val isSelected = targetYears == years
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSetTargetYears(years) },
                        color = if (isSelected) GoldPrimary else Color(0x33142230),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldPrimary else Color(0x33CBD5E1))
                    ) {
                        Text(
                            text = "${ServiceCalculatorHelper.formatNumber(years, useKurdishDigits)} ساڵ",
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = GoldPrimary,
                trackColor = Color(0x33CBD5E1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "خزمەتی ئێستا: ${ServiceCalculatorHelper.formatNumber(currentYears, useKurdishDigits)} ساڵ",
                    style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
                )
                Text(
                    text = "ئامانج: ${ServiceCalculatorHelper.formatNumber(targetYears, useKurdishDigits)} ساڵ",
                    style = MaterialTheme.typography.bodySmall.copy(color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Remaining Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (remY == 0 && remM == 0 && remD == 0) MilitaryGreenAccent.copy(alpha = 0.15f) else Color(0x440B131C),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (remY == 0 && remM == 0 && remD == 0) MilitaryGreenAccent else SteelBlue.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (remY == 0 && remM == 0 && remD == 0 && currentYears >= targetYears) {
                        Text(
                            text = "پیرۆزە! مەرجی ساڵانی خزمەتی خانەنشینی تەواو کراوە.",
                            color = MilitaryGreenAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "ماوەی ماوە بۆ تەواوکردنی خزمەت:",
                            style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${ServiceCalculatorHelper.formatNumber(remY, useKurdishDigits)} ساڵ و ${ServiceCalculatorHelper.formatNumber(remM, useKurdishDigits)} مانگ و ${ServiceCalculatorHelper.formatNumber(remD, useKurdishDigits)} ڕۆژ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculation History Modal Dialog
 */
@Composable
fun HistoryRecordsDialog(
    historyList: List<CalculationRecord>,
    useKurdishDigits: Boolean,
    onDismiss: () -> Unit,
    onDeleteRecord: (String) -> Unit,
    onClearAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مێژووی هەژمارەکان",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )
                if (historyList.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("سڕینەوەی هەموو", color = CrimsonAlert, fontSize = 11.sp)
                    }
                }
            }
        },
        text = {
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ هەژمارێکی پاشەکەوتکراو بەردەست نییە.",
                        color = SteelMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    historyList.forEach { record ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PetroleumNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4AF37))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight
                                        )
                                    )
                                    Text(
                                        text = "${ServiceCalculatorHelper.formatNumber(record.years, useKurdishDigits)} ساڵ و ${ServiceCalculatorHelper.formatNumber(record.months, useKurdishDigits)} مانگ و ${ServiceCalculatorHelper.formatNumber(record.days, useKurdishDigits)} ڕۆژ",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = record.details,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SteelMuted,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteRecord(record.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "سڕینەوە",
                                        tint = CrimsonAlert.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("داخستن", color = GoldPrimary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = PetroleumDarkBg
    )
}
