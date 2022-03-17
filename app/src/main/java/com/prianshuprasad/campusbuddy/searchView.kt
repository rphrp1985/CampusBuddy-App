package com.prianshuprasad.campusbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_search_view.*

class searchView : AppCompatActivity() {

    private lateinit var adapter: rcviewadapter11
    lateinit var rcview11: RecyclerView

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)


        supportActionBar?.hide()

        rcview11 = findViewById(R.id.rcview11)
        rcview11.layoutManager= LinearLayoutManager(this)




        searchbar.doOnTextChanged { text, start, before, count ->




            var collection = db.collection("O-Search")



            var str = searchbar.text.toString()
            val n = str.length
            var i  = 0;

            while(i<n) {

                if(!str[i].isLetterOrDigit())
                    continue;
                if(i==n-1){
                    collection = collection.document("${str[i].toUpperCase()}").collection("Data")
                     break

                }

                collection = collection.document("${str[i].toUpperCase()}").collection("${str[i].toUpperCase()}")

                i++;
            }

            val query = collection.orderBy("datastr", Query.Direction.DESCENDING)
            val recyclerViewoptions =
                FirestoreRecyclerOptions.Builder<clouddata>().setQuery(query, clouddata::class.java)
                    .build()
            adapter = rcviewadapter11(recyclerViewoptions, this)
            rcview11.adapter = adapter

            adapter.startListening()

//


        }










    }




fun open(docID:String,whatis:String){
    if(whatis=="Post"){

        val intent = Intent(this,postView::class.java)
        intent.putExtra("docID",docID)
        startActivity(intent)


    }else
    {
        val intent = Intent(this,bookView::class.java)
        intent.putExtra("docID",docID)
        startActivity(intent)
    }



}









}