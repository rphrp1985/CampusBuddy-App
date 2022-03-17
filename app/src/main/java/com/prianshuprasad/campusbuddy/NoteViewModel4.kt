package com.prianshuprasad.campusbuddy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel4(application: Application): AndroidViewModel(application) {



    val allnotes: LiveData<List<Note>>
    private val repository: NoteRepository


    init {

        val dao= NoteDatabase4.getDatabase( application).getNodeDao()
        repository= NoteRepository(dao)
        allnotes= repository.allnotes

    }

    fun deleteNode(note: Note)= viewModelScope.launch(Dispatchers.IO){

        repository.delete(note)
    }


    fun InsertNote(note: Note)= viewModelScope.launch(Dispatchers.IO){

        repository.insert(note)

    }

    fun deleteALL() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteALL()
    }






}