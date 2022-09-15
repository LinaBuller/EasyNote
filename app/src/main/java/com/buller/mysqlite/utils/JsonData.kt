package com.buller.mysqlite.utils

import com.buller.mysqlite.model.Note

object JsonData {
     lateinit var map: HashMap<String, Map<String, String>>

    private fun putNote(note: Note):HashMap<String,String>{
        val id = note.id
        val titleText = note.title
        val contentText = note.content
        val dateLastChangeNote = note.time

        val arrayKey = arrayListOf("id", "title", "content", "date")
        val arrayValue = arrayListOf(id.toString(), titleText, contentText, dateLastChangeNote)

        val mapNote = HashMap <String, String>()

        arrayKey.forEach { key ->
            arrayValue.forEach { value ->
                mapNote[key] = value
            }
        }
        return mapNote
    }

    fun putNotes(listNotes: List<Note>) {
        map = HashMap()
        listNotes.forEach { note ->
            val mapNote = putNote(note)
            map[note.id.toString()] = mapNote
        }
    }
}