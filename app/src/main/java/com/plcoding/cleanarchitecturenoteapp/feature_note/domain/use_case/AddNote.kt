package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import java.time.LocalDate
import java.time.LocalTime

class AddNote(
    private val repository: NoteRepository
) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        if(note.title.isBlank()) {
            throw InvalidNoteException("The title of the note can't be empty.")
        }
        if(note.content.isBlank()) {
            throw InvalidNoteException("The content of the note can't be empty.")
        }
        if(note.date.isNotBlank() && note.time.isNotBlank()) {
            val noteDate = LocalDate.parse(note.date)
            val noteTime = LocalTime.parse(note.time)

            if(noteDate < currentDate || noteDate == currentDate && noteTime < currentTime) {
                throw InvalidNoteException("Please set reminder for upcoming date and time")
            }
        }
        repository.insertNote(note)
    }
}