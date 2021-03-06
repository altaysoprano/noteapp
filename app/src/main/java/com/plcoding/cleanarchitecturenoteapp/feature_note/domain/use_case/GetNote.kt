package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import java.time.LocalDate
import java.time.LocalTime

class GetNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(id: Int): Note? {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()



        return repository.getNoteById(id)
    }
}