package com.prianshuprasad.campusbuddy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_table")
class Note(

//    @ColumnInfo(name = "text1") var textview1: String,
//    @ColumnInfo(name = "text2") var textview2: String,
//    @ColumnInfo(name = "text3") var textview3: String,
//    @ColumnInfo(name = "image1") var image1: String,
//    @ColumnInfo(name = "image2") var image2: String,
//    @ColumnInfo(name = "image3") var image3: String,
//    @ColumnInfo(name = "image4") var image4: String,
//    @ColumnInfo(name = "rcview2") var rcview2: String,
    @ColumnInfo(name = "data") var data: String




){

    @PrimaryKey(autoGenerate = true) var id =0


}