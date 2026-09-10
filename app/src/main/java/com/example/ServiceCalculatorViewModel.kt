package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class CalculatorUiState(
    val selectedTab: Int = 0, // 0 = Manual Input, 1 = Date Range, 2 = Retirement/Promotion
    val useKurdishDigits: Boolean = true,
    
    // Mode 0: Manual Periods
    val periods: List<ServicePeriod> = listOf(
        ServicePeriod(title = "خزمەتی بنەڕەتی (سەرەکی)", years = "", months = "", days = "")
    ),
    val manualResult: CalculationResult = CalculationResult(),
    
    // Mode 1: Date Range
    val startYear: String = "2015",
    val startMonth: String = "1",
    val startDay: String = "1",
    val endYear: String = LocalDate.now().year.toString(),
    val endMonth: String = LocalDate.now().monthValue.toString(),
    val endDay: String = LocalDate.now().dayOfMonth.toString(),
    val isDateRangeDouble: Boolean = false,
    val dateRangeResult: CalculationResult = CalculationResult(),

    // Mode 2: Retirement / Seniority
    val retirementTargetYears: Int = 30,
    val rankTargetYears: Int = 4,

    // History
    val historyList: List<CalculationRecord> = emptyList(),
    val showCopySnackbar: Boolean = false,
    val snackbarMessage: String = ""
)

class ServiceCalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        recalculateManual()
        recalculateDateRange()
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleKurdishDigits() {
        _uiState.update { it.copy(useKurdishDigits = !it.useKurdishDigits) }
    }

    // --- Manual Mode Actions ---

    fun addPeriod() {
        val newIndex = _uiState.value.periods.size + 1
        val newPeriod = ServicePeriod(
            title = "ماوەی خزمەت $newIndex",
            years = "",
            months = "",
            days = ""
        )
        _uiState.update {
            it.copy(periods = it.periods + newPeriod)
        }
        recalculateManual()
    }

    fun removePeriod(periodId: String) {
        if (_uiState.value.periods.size <= 1) return
        _uiState.update { state ->
            state.copy(periods = state.periods.filter { it.id != periodId })
        }
        recalculateManual()
    }

    fun updatePeriodYears(periodId: String, years: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(years = years) else it
            }
            state.copy(periods = updated)
        }
        recalculateManual()
    }

    fun updatePeriodMonths(periodId: String, months: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(months = months) else it
            }
            state.copy(periods = updated)
        }
        recalculateManual()
    }

    fun updatePeriodDays(periodId: String, days: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(days = days) else it
            }
            state.copy(periods = updated)
        }
        recalculateManual()
    }

    fun toggleDoubleService(periodId: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(isDoubleService = !it.isDoubleService) else it
            }
            state.copy(periods = updated)
        }
        recalculateManual()
    }

    fun toggleDeduction(periodId: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(isDeduction = !it.isDeduction) else it
            }
            state.copy(periods = updated)
        }
        recalculateManual()
    }

    fun updatePeriodTitle(periodId: String, title: String) {
        _uiState.update { state ->
            val updated = state.periods.map {
                if (it.id == periodId) it.copy(title = title) else it
            }
            state.copy(periods = updated)
        }
    }

    fun addQuickDurationToFirst(addYears: Int, addMonths: Int) {
        val first = _uiState.value.periods.firstOrNull() ?: return
        val currentY = ServiceCalculatorHelper.cleanDigits(first.years).toIntOrNull() ?: 0
        val currentM = ServiceCalculatorHelper.cleanDigits(first.months).toIntOrNull() ?: 0

        val newY = (currentY + addYears).coerceAtLeast(0)
        val newM = (currentM + addMonths).coerceAtLeast(0)

        updatePeriodYears(first.id, if (newY > 0) newY.toString() else "")
        updatePeriodMonths(first.id, if (newM > 0) newM.toString() else "")
    }

    fun resetManual() {
        _uiState.update {
            it.copy(
                periods = listOf(
                    ServicePeriod(title = "خزمەتی بنەڕەتی (سەرەکی)", years = "", months = "", days = "")
                )
            )
        }
        recalculateManual()
    }

    private fun recalculateManual() {
        val result = ServiceCalculatorHelper.calculateManualService(_uiState.value.periods)
        _uiState.update { it.copy(manualResult = result) }
    }

    // --- Date Range Actions ---

    fun updateStartDate(year: String, month: String, day: String) {
        _uiState.update {
            it.copy(startYear = year, startMonth = month, startDay = day)
        }
        recalculateDateRange()
    }

    fun updateEndDate(year: String, month: String, day: String) {
        _uiState.update {
            it.copy(endYear = year, endMonth = month, endDay = day)
        }
        recalculateDateRange()
    }

    fun setEndDateToToday() {
        val now = LocalDate.now()
        _uiState.update {
            it.copy(
                endYear = now.year.toString(),
                endMonth = now.monthValue.toString(),
                endDay = now.dayOfMonth.toString()
            )
        }
        recalculateDateRange()
    }

    fun toggleDateRangeDouble() {
        _uiState.update { it.copy(isDateRangeDouble = !it.isDateRangeDouble) }
        recalculateDateRange()
    }

    private fun recalculateDateRange() {
        val sY = ServiceCalculatorHelper.cleanDigits(_uiState.value.startYear).toIntOrNull() ?: 2015
        val sM = ServiceCalculatorHelper.cleanDigits(_uiState.value.startMonth).toIntOrNull() ?: 1
        val sD = ServiceCalculatorHelper.cleanDigits(_uiState.value.startDay).toIntOrNull() ?: 1

        val eY = ServiceCalculatorHelper.cleanDigits(_uiState.value.endYear).toIntOrNull() ?: LocalDate.now().year
        val eM = ServiceCalculatorHelper.cleanDigits(_uiState.value.endMonth).toIntOrNull() ?: LocalDate.now().monthValue
        val eD = ServiceCalculatorHelper.cleanDigits(_uiState.value.endDay).toIntOrNull() ?: LocalDate.now().dayOfMonth

        val result = ServiceCalculatorHelper.calculateBetweenDates(
            startYear = sY, startMonth = sM, startDay = sD,
            endYear = eY, endMonth = eM, endDay = eD,
            isDoubleService = _uiState.value.isDateRangeDouble
        )
        _uiState.update { it.copy(dateRangeResult = result) }
    }

    // --- Retirement / Rank targets ---

    fun setRetirementTarget(years: Int) {
        _uiState.update { it.copy(retirementTargetYears = years) }
    }

    fun setRankTarget(years: Int) {
        _uiState.update { it.copy(rankTargetYears = years) }
    }

    // --- History Management ---

    fun saveCalculation(customTitle: String? = null) {
        val state = _uiState.value
        val result = if (state.selectedTab == 0) state.manualResult else state.dateRangeResult
        if (result.years == 0 && result.months == 0 && result.days == 0) return

        val title = customTitle?.takeIf { it.isNotBlank() }
            ?: if (state.selectedTab == 0) "هەژماری دەستی (${state.periods.size} بەش)" else "هەژماری بەروار"

        val details = if (state.selectedTab == 0) {
            state.periods.joinToString(" + ") { p ->
                val mult = if (p.isDoubleService) " (دووقات)" else ""
                "${p.title.ifBlank { "خزمەت" }}: ${p.years.ifBlank { "0" }}س ${p.months.ifBlank { "0" }}م$mult"
            }
        } else {
            "لە ${state.startYear}/${state.startMonth}/${state.startDay} تاوەکو ${state.endYear}/${state.endMonth}/${state.endDay}"
        }

        val record = CalculationRecord(
            title = title,
            years = result.years,
            months = result.months,
            days = result.days,
            details = details
        )

        _uiState.update {
            it.copy(
                historyList = listOf(record) + it.historyList,
                showCopySnackbar = true,
                snackbarMessage = "هەژمارەکە لە مێژوودا پاشەکەوت کرا"
            )
        }
    }

    fun deleteHistoryRecord(id: String) {
        _uiState.update { state ->
            state.copy(historyList = state.historyList.filter { it.id != id })
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(historyList = emptyList()) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(showCopySnackbar = false) }
    }

    fun triggerSnackbar(message: String) {
        _uiState.update { it.copy(showCopySnackbar = true, snackbarMessage = message) }
    }
}
