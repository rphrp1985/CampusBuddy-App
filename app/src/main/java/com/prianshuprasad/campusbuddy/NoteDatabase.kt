package com.prianshuprasad.campusbuddy

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import org.junit.runner.manipulation.Ordering
import  android.content.Context
//import notedao
//import notes


@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
public abstract class NoteDatabase() : RoomDatabase() {




    abstract fun getNodeDao(): NoteDao

    companion object {


        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "Post_DATABASE"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}