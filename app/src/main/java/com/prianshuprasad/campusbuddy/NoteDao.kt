package com.prianshuprasad.campusbuddy

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.concurrent.Flow

//import notes

@Dao
interface NoteDao {



      @Insert( onConflict = OnConflictStrategy.IGNORE)
      fun insert(note: Note)

      @Delete
       fun delete(note : Note)


//    @Query("DELETE FROM notes_table")
//    suspend fun deleteAll()

    @Query("Select * from notes_table order by id DESC")
     fun getAllNotes(): LiveData<List<Note>>

    @Query("DELETE FROM notes_table")
    fun deleteAll()



}