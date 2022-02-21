package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import android.content.Context
import androidx.compose.ui.focus.FocusState
import com.vanpra.composematerialdialogs.MaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

sealed class AddEditNoteEvent{
    data class EnteredTitle(val value: String): AddEditNoteEvent()
    data class ChangeTitleFocus(val focusState: FocusState): AddEditNoteEvent()
    data class EnteredContent(val value: String): AddEditNoteEvent()
    data class ChangeContentFocus(val focusState: FocusState): AddEditNoteEvent()
    data class ChangeColor(val color: Int): AddEditNoteEvent()
    data class ChangeDate(val date: String, val dialogStateTime: MaterialDialogState, val dialogStateDate: MaterialDialogState): AddEditNoteEvent()
    data class ChangeClock(val time: String, val dialogStateTime: MaterialDialogState): AddEditNoteEvent()
    object RemoveAlarm: AddEditNoteEvent()
    object OnCancel: AddEditNoteEvent()
    data class SaveNote(val context: Context): AddEditNoteEvent()
}

