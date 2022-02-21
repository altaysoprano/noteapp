package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.Alarm
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.MyAlarm
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val alarm: Alarm
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null

    private var searchJob: Job? = null

    init {
        searchNotes(state.value.searchQuery, NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                searchNotes(state.value.searchQuery, event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
                event.note.id?.let { alarm.cancelAlarm(event.context, it) }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.restoreNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
            is NotesEvent.ToggleSearchBar -> {
                _state.value = state.value.copy(
                    isSearchBarVisible = !state.value.isSearchBarVisible,
                    isTopBarVisibility = false
                )
            }
            is NotesEvent.Close -> {
                if (state.value.searchQuery.isNotBlank()) {
                    _state.value = state.value.copy(
                        searchQuery = ""
                    )
                    searchNotes(state.value.searchQuery, state.value.noteOrder)
                } else {
                    _state.value = state.value.copy(
                        isSearchBarVisible = false,
                        isTopBarVisibility = true
                    )
                }
            }
            is NotesEvent.Type -> {
                searchNotes(event.text, event.noteOrder)
            }
        }
    }

    private fun searchNotes(query: String, noteOrder: NoteOrder) {
        searchJob?.cancel()
        searchJob = noteUseCases.searchNote(query, noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    searchQuery = query,
                    notes = notes,
                    noteOrder = noteOrder
                )
            }.launchIn(viewModelScope)
    }
}














