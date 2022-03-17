package com.prianshuprasad.campusbuddy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import org.json.JSONObject

class NoteData() {


    fun wordCutter(str:String):ArrayList<String>{

        val arr:ArrayList<String> = ArrayList()

        val n= str.length

        var i=0;

        var temstr=""

        while(i<n){

            if(str[i].isLetterOrDigit()){


                temstr = "$temstr${str[i]}"


            }else{

                if(temstr!="")
                {
                    arr.add(temstr)
                    temstr=""
                }

            }

            i++;
        }

        if(temstr!="")
            arr.add(temstr)



        return  arr


    }




   fun toDataMap(str:String):datamap{

//       var arr: ArrayList<data> = ArrayList()

       val map = ObjectMapper().readValue<MutableMap<String, Any?>>(str)



       return datamap(map);

   }

    fun toString(datai : datamap):String
    {
        val gson = Gson()
        val json = JSONObject(gson.toJson(datai.map))

        return  json.toString()


    }


    fun NotetoData(arr:ArrayList<Note>):ArrayList<datamap>{

        var n= arr.size;
        var i=0

        var brr:ArrayList<datamap> = ArrayList();
//
        while(i<n){
//

            val map = ObjectMapper().readValue<MutableMap<String, Any?>>(arr[i].data.toString())
            brr.add( datamap(map) );
            i++;





//
//
        }
        return brr

    }



//    fun DatatoNote(arr: ArrayList<data>):ArrayList<Note>{
//
//        var brr:ArrayList<Note> = ArrayList()
//
//        var n= arr.size
//
//        var i=0;
//
//        while(i<n){
//
//            var str:String = toString(arr[i].map)
//
//            brr.add(Note(str))
//
//            i++;
//
//        }
//        return brr;
//
//    }





}