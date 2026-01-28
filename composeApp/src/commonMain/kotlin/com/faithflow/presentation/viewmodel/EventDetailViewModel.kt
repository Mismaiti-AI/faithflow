package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.model.Event
import com.faithflow.domain.repository.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventId: String,
    private val repository: EventRepository
) : ViewModel() {

    val uiState: StateFlow<EventDetailUiState> = combine(
        repository.selectedEvent,
        repository.isLoading,
        repository.error
    ) { event, isLoading, error ->
        EventDetailUiState(
            event = event,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventDetailUiState(isLoading = true)
    )

    init {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            repository.selectEvent(eventId)
        }
    }

    fun clearError() {
        repository.clearError()
    }

    override fun onCleared() {
        super.onCleared()
        repository.clearSelection()
    }
}

data class EventDetailUiState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
