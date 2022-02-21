package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteViewModel
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesEvent
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun AddAlarmSection(
    time: String,
    date: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {

    IconButton(
        onClick = {
            onClick()
        },
    ) {
        Surface(
            modifier = Modifier.padding(8.dp),
            elevation = 8.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.primary
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                if (date.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Alarm",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Reminder",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$date $time",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel Alarm",
                        tint = Color.Black,
                        modifier = Modifier.clickable(onClick = onRemoveClick).padding(start = 8.dp)
                    )
                }
            }
        }
    }
}