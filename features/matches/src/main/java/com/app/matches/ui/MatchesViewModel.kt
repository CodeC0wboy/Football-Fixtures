package com.app.matches.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.network.model.NetworkResult
import com.app.matches.domain.repository.Match
import com.app.matches.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

const val DATE_PARAM = "date"

class MatchesViewModel(
    private val getMatchesUseCase: GetMatchesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesState>(MatchesState.Idle)
    val state: StateFlow<MatchesState> = _state.asStateFlow()

    private var currentDate: String = LocalDate.now().toString()

    init {
        loadMatches(currentDate, initial = true)
    }

    fun onEvent(event: MatchesEvent) {
        when (event) {
            is MatchesEvent.LoadMatches -> {
                currentDate = event.date
                loadMatches(currentDate, initial = false)
            }
        }
    }

    private fun loadMatches(date: String, initial: Boolean) {
        viewModelScope.launch {
            if (initial) {
                _state.value = MatchesState.Loading
            } else {
                val current = _state.value
                if (current is MatchesState.Success) {
                    _state.value = current.copy(isRefreshing = true)
                }
            }

            val result = getMatchesUseCase(mapOf(DATE_PARAM to date))
            _state.value = when (result) {
                is NetworkResult.Success -> MatchesState.Success(result.data)
                is NetworkResult.Failure.Server -> MatchesState.Error(result.rawMessage)
                is NetworkResult.Failure.Internal -> MatchesState.Error(
                    result.throwable.message ?: "Internal server error"
                )
            }
        }
    }

    fun refresh() {
        loadMatches(currentDate, initial = false)
    }
}


sealed interface MatchesState {
    object Idle : MatchesState
    object Loading : MatchesState
    data class Success(
        val matches: List<Match>,
        val isRefreshing: Boolean = false
    ) : MatchesState

    data class Error(val message: String) : MatchesState
}

sealed interface MatchesEvent {
    data class LoadMatches(val date: String) : MatchesEvent
}
