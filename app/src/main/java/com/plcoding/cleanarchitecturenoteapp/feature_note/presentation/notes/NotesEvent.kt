package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import android.content.Context
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder

sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder): NotesEvent()
    data class DeleteNote(val note: Note, val context: Context): NotesEvent()
    data class Type(val text: String, val noteOrder: NoteOrder): NotesEvent()
    object RestoreNote: NotesEvent()
    object ToggleOrderSection: NotesEvent()
    object ToggleSearchBar : NotesEvent()
    object Close: NotesEvent()
}
