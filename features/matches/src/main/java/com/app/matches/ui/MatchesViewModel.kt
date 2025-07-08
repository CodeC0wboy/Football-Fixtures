package com.app.matches.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.network.model.NetworkResult
import com.app.matches.R
import com.app.matches.domain.repository.Match
import com.app.matches.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

const val DATE_PARAM = "date"

class MatchesViewModel(
    private val getMatchesUseCase: GetMatchesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesState>(MatchesState.Idle)
    val state: StateFlow<MatchesState> = _state.asStateFlow()

    private var selectedFilter: TimeFilter = TimeFilter.Today
    private var rawMatches: List<Match> = emptyList()

    init {
        loadMatches()
    }

    fun applyFilter(filter: TimeFilter) {
        selectedFilter = filter
        val filtered = filterMatches(rawMatches, filter)
        _state.value = MatchesState.Success(matches = filtered, selectedFilter = filter)
    }

    fun refresh() {
        loadMatches()
    }

    private fun loadMatches() {
        viewModelScope.launch {
            _state.value = MatchesState.Loading

            val date = LocalDateTime.now().toLocalDate().toString()
            val result = getMatchesUseCase(mapOf(DATE_PARAM to date))

            _state.value = when (result) {
                is NetworkResult.Success -> {
                    rawMatches = result.data
                    val filtered = filterMatches(rawMatches, selectedFilter)
                    MatchesState.Success(matches = filtered, selectedFilter = selectedFilter)
                }

                is NetworkResult.Failure.Server -> MatchesState.Error(result.rawMessage)
                is NetworkResult.Failure.Internal -> MatchesState.Error(
                    result.throwable.message ?: "Internal server error"
                )
            }
        }
    }

    private fun filterMatches(matches: List<Match>, filter: TimeFilter): List<Match> {
        val now = Instant.now()

        val fromInstant = when (filter.hoursAgo) {
            0 -> now.truncatedTo(ChronoUnit.DAYS)
            else -> now.minusSeconds(filter.hoursAgo * 3600L)
        }

        val toInstant = now

        return matches.filter { match ->
            try {
                val matchInstant = Instant.parse(match.matchDate)
                matchInstant.isAfter(fromInstant) && matchInstant.isBefore(toInstant)
            } catch (e: Exception) {
                false
            }
        }
    }
}

sealed interface MatchesState {
    object Idle : MatchesState
    object Loading : MatchesState
    data class Success(
        val matches: List<Match>,
        val timeFilters: List<TimeFilter> = listOf(
            TimeFilter.Last1Hour,
            TimeFilter.Last3Hours,
            TimeFilter.Last6Hours,
            TimeFilter.Today
        ),
        val selectedFilter: TimeFilter,
        val isRefreshing: Boolean = false
    ) : MatchesState

    data class Error(val message: String) : MatchesState
}

sealed class TimeFilter(@StringRes val labelResId: Int, val hoursAgo: Int) {
    object Last1Hour : TimeFilter(R.string.filter_last_1_hour, 1)
    object Last3Hours : TimeFilter(R.string.filter_last_3_hours, 3)
    object Last6Hours : TimeFilter(R.string.filter_last_6_hours, 6)
    object Today : TimeFilter(R.string.filter_today, 0)
}
