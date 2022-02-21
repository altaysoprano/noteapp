package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.R
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.Alarm
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.MyAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
    private val alarm: Alarm
) : ViewModel() {

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter title..."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter some content"
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _dateText = mutableStateOf(
        NoteTextFieldState(
            dateText = ""
        )
    )
    val dateText: State<NoteTextFieldState> = _dateText

    private val _timeText = mutableStateOf(
        NoteTextFieldState(
            timeText = ""
        )
    )
    val timeText: State<NoteTextFieldState> = _timeText

    private val _isAlarmSetted = mutableStateOf(
        NoteTextFieldState(
            isAlarmSetted = false
        )
    )
    private val isAlarmSetted: State<NoteTextFieldState> = _isAlarmSetted

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null

    init {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1) {
                viewModelScope.launch {
                    noteUseCases.getNote(noteId)?.also { note ->

                        Log.d("Mesaj", "Bu notun date: ${note.date}")
                        Log.d("Mesaj", "Bu notun time: ${note.time}")

                        currentNoteId = note.id
                        if(note.date.isNotBlank() && note.time.isNotBlank()) {
                            val noteDate = LocalDate.parse(note.date)
                            val noteTime = LocalTime.parse(note.time)

                            if(noteDate < currentDate || noteDate == currentDate && noteTime < currentTime) {
                                _isAlarmSetted.value = isAlarmSetted.value.copy(
                                    isAlarmSetted = false
                                )
                                _dateText.value = dateText.value.copy(
                                    dateText = ""
                                )
                                _timeText.value = timeText.value.copy(
                                    timeText = ""
                                )
                            }
                            else {
                                _dateText.value = dateText.value.copy(
                                    dateText = note.date
                                )
                                _timeText.value = timeText.value.copy(
                                    timeText = note.time
                                )
                                _isAlarmSetted.value = isAlarmSetted.value.copy(
                                    isAlarmSetted = note.isAlarmSetted
                                )

                            }
                        }
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = _noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = _noteContent.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = _noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            _noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNoteId,
                                date = dateText.value.dateText,
                                time = timeText.value.timeText,
                                isAlarmSetted = isAlarmSetted.value.isAlarmSetted
                            )
                        )
                        if(isAlarmSetted.value.isAlarmSetted) {
                            val noteDate = LocalDate.parse(dateText.value.dateText)
                            val noteTime = LocalTime.parse(timeText.value.timeText)

                            alarm.setAlarm(
                                event.context,
                                noteDate.year,
                                noteDate.monthValue,
                                noteDate.dayOfMonth,
                                noteTime.hour,
                                noteTime.minute,
                                noteTitle.value.text,
                                noteContent.value.text,
                                currentNoteId ?: -1
                            )
                        }
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
            is AddEditNoteEvent.ChangeDate -> {
                val currentDate = LocalDate.now()
                val selectedDate = LocalDate.parse(event.date)
                if (selectedDate < currentDate) {
                    viewModelScope.launch {
                        _eventFlow.emit(
                            UiEvent.ShowWarnDateTimeSnackBar(
                                message = "Please set reminder for upcoming date and time"
                            )
                        )
                    }
                    event.dialogStateDate.show()
                } else {
                    _dateText.value = _dateText.value.copy(
                        dateText = event.date
                    )
                    event.dialogStateTime.show()
                }
            }

            is AddEditNoteEvent.ChangeClock -> { //if dateTextState.value.dateText<date ve time < kontrolü BURADA YAPILACAK
                val currentTime = LocalTime.now()
                val currentDate = LocalDate.now()
                val selectedDate = LocalDate.parse(_dateText.value.dateText)
                val selectedTime = LocalTime.parse(event.time)
                if (selectedDate <= currentDate && selectedTime < currentTime) {
                    viewModelScope.launch {
                        _eventFlow.emit(
                            UiEvent.ShowWarnTimeSnackbar(
                                message = "Please set reminder for upcoming date and time"
                            )
                        )
                    }
                    event.dialogStateTime.show()
                } else {
                    _timeText.value = _timeText.value.copy(
                        timeText = event.time,
                    )
                    _isAlarmSetted.value = _isAlarmSetted.value.copy(
                        isAlarmSetted = true
                    )
                }
            }
            is AddEditNoteEvent.OnCancel -> {
                if (isAlarmSetted.value.isAlarmSetted) {
                    _dateText.value = _dateText.value.copy(
                        dateText = dateText.value.dateText,
                    )
                    _timeText.value = _timeText.value.copy(
                        timeText = timeText.value.timeText,
                    )
                } else {
                    _dateText.value = _dateText.value.copy(
                        dateText = ""
                    )
                    _timeText.value = _timeText.value.copy(
                        timeText = ""
                    )
                }
            }
            is AddEditNoteEvent.RemoveAlarm -> {
                _isAlarmSetted.value = _isAlarmSetted.value.copy(
                    isAlarmSetted = false
                )
                _dateText.value = _dateText.value.copy(
                    dateText = ""
                )
                _timeText.value = _timeText.value.copy(
                    timeText = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.ShowAlarmRemovedSnackBar(
                            message = "Reminder Removed"
                        )
                    )
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class ShowAlarmRemovedSnackBar(val message: String) : UiEvent()
        data class ShowWarnDateTimeSnackBar(val message: String) : UiEvent()
        data class ShowWarnTimeSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}