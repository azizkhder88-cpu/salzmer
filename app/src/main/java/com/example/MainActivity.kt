package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: ServiceCalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Liway1NawtApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Liway1NawtApp(
    viewModel: ServiceCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.showCopySnackbar) {
        if (state.showCopySnackbar) {
            snackbarHostState.showSnackbar(
                message = state.snackbarMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSnackbar()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Background engine image with faded opacity ("وینەی بزوینەرێکی لە باکراۆند بێ بە کالی")
        FadedEngineBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = GoldPrimary,
                        contentColor = Color(0xFF1E1303),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            topBar = {
                BrigadeTopHeader(
                    useKurdishDigits = state.useKurdishDigits,
                    historyCount = state.historyList.size,
                    onToggleDigits = { viewModel.toggleKurdishDigits() },
                    onOpenHistory = { showHistoryDialog = true }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mode selector
                item {
                    CalculationModeSelector(
                        selectedTab = state.selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }

                // Active Result Card shown at the top for instantaneous feedback
                item {
                    val activeResult = if (state.selectedTab == 0) state.manualResult else state.dateRangeResult
                    ServiceResultDisplayCard(
                        result = activeResult,
                        useKurdishDigits = state.useKurdishDigits,
                        onSave = { viewModel.saveCalculation() },
                        onReset = {
                            if (state.selectedTab == 0) viewModel.resetManual()
                            else viewModel.updateStartDate("2015", "1", "1")
                        }
                    )
                }

                // Mode 0: Manual Entry of Years and Months ("بە لیدانی سال و مانگ بەدەست بۆت هەژمار بکات")
                if (state.selectedTab == 0) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "تۆمارکردنی ساڵ و مانگ بە دەست:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                            Text(
                                text = "${ServiceCalculatorHelper.formatNumber(state.periods.size, state.useKurdishDigits)} ماوە",
                                style = MaterialTheme.typography.bodySmall.copy(color = SteelMuted)
                            )
                        }
                    }

                    itemsIndexed(state.periods, key = { _, p -> p.id }) { index, period ->
                        ManualPeriodInputCard(
                            period = period,
                            index = index,
                            totalPeriods = state.periods.size,
                            useKurdishDigits = state.useKurdishDigits,
                            onYearsChange = { viewModel.updatePeriodYears(period.id, it) },
                            onMonthsChange = { viewModel.updatePeriodMonths(period.id, it) },
                            onDaysChange = { viewModel.updatePeriodDays(period.id, it) },
                            onToggleDouble = { viewModel.toggleDoubleService(period.id) },
                            onToggleDeduction = { viewModel.toggleDeduction(period.id) },
                            onRemove = { viewModel.removePeriod(period.id) },
                            onQuickAdd = { y, m -> viewModel.addQuickDurationToFirst(y, m) }
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.addPeriod() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("add_period_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "زیادکردنی ماوە",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "زیادکردنی ماوەیەکی تری خزمەت",
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Mode 1: Date Range
                if (state.selectedTab == 1) {
                    item {
                        DateRangeCalculationCard(
                            startYear = state.startYear,
                            startMonth = state.startMonth,
                            startDay = state.startDay,
                            endYear = state.endYear,
                            endMonth = state.endMonth,
                            endDay = state.endDay,
                            isDoubleService = state.isDateRangeDouble,
                            onStartDateChange = { y, m, d -> viewModel.updateStartDate(y, m, d) },
                            onEndDateChange = { y, m, d -> viewModel.updateEndDate(y, m, d) },
                            onSetToday = { viewModel.setEndDateToToday() },
                            onToggleDouble = { viewModel.toggleDateRangeDouble() }
                        )
                    }
                }

                // Mode 2: Retirement and Seniority Checker
                if (state.selectedTab == 2) {
                    item {
                        val activeRes = if (state.selectedTab == 0) state.manualResult else state.manualResult
                        RetirementAndPromotionCard(
                            currentYears = activeRes.years,
                            currentMonths = activeRes.months,
                            currentDays = activeRes.days,
                            targetYears = state.retirementTargetYears,
                            rankTargetYears = state.rankTargetYears,
                            useKurdishDigits = state.useKurdishDigits,
                            onSetTargetYears = { viewModel.setRetirementTarget(it) },
                            onSetRankTarget = { viewModel.setRankTarget(it) }
                        )
                    }
                }
            }
        }
    }

    if (showHistoryDialog) {
        HistoryRecordsDialog(
            historyList = state.historyList,
            useKurdishDigits = state.useKurdishDigits,
            onDismiss = { showHistoryDialog = false },
            onDeleteRecord = { viewModel.deleteHistoryRecord(it) },
            onClearAll = { viewModel.clearHistory() }
        )
    }
}

/**
 * Kept for test compatibility with GreetingScreenshotTest.kt
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PetroleumDarkBg
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "لیوای 1 نەوت - $name",
                style = MaterialTheme.typography.titleLarge.copy(color = GoldLight, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("لیوای 1 نەوت")
    }
}
