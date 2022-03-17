package com.prianshuprasad.campusbuddy

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import java.util.concurrent.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NoteRepository(private val noteDao: NoteDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    var allnotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note)
    {
        noteDao.insert(note)
    }
     suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    suspend fun deleteALL(){
        noteDao.deleteAll()
    }



}