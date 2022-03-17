package com.prianshuprasad.campusbuddy

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class optimizeSearch {

val db = FirebaseFirestore.getInstance()

    fun postNow(clouddata: clouddata,str:String,docid:String){



        val n = str.length
        var i=0;
        var collection = db.collection("O-Search")

        while(i<n) {

            if(!str[i].isLetterOrDigit())
                continue;

         val doc= collection.document("${str[i].toUpperCase()}").collection("Data")


            doc.document(docid).set(clouddata)


            collection = collection.document("${str[i].toUpperCase()}").collection("${str[i].toUpperCase()}")

            i++;
        }





    }





}