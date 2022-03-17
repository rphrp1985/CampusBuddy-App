package com.prianshuprasad.campusbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comment_view.*
import org.w3c.dom.Comment

class commentView : AppCompatActivity() {


    var uid =""
    lateinit var bookpost:datamap
    lateinit var docID :String
    lateinit var whatis:String
    lateinit var viewmodel3: NoteViewModel3
    var InterestMap: datamap = datamap(mutableMapOf<String,Any?>())


    val arr:ArrayList<datamap> = ArrayList()
    lateinit var owner:datamap

    lateinit var mAdapter9 : rcviewadapter9
    lateinit var rcview9:RecyclerView


    val TagList:ArrayList<String> = ArrayList()

    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_view)

        supportActionBar?.hide()


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid


        docID = intent.getStringExtra("docID")!!

        whatis = intent.getStringExtra("whatis")!!


       owner = datamap(mutableMapOf<String,Any?>())


        val collection = db.collection("$whatis").document("$whatis").collection("ALL").document(docID).get()

        rcview9 = findViewById(R.id.rcview9)

        viewmodel3= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( getApplication()  ) ).get(NoteViewModel3::class.java)


        viewmodel3.allnotes.observe( this, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = NoteData().NotetoData(notelist)

                if(datalist.size>=1)
                    InterestMap= datalist[0];

//                Toast.makeText(this,"${InterestMap.toString()} fetched  ${datalist.size}",Toast.LENGTH_LONG).show()


            }
        })






        collection.addOnCompleteListener {

            if(it.getResult().exists()){

                val clouddata :clouddata = it.getResult().toObject(clouddata::class.java)!!
                bookpost = NoteData().toDataMap(clouddata.datastr)



                updateTag()


                UpdateInterest(5)


                UpdateRcview()

                edittext2.visibility = View.VISIBLE
                send.visibility = View.VISIBLE
                progressbar.visibility = View.GONE



            }else
            {
                Toast.makeText(this,"Something Went Wrong!",Toast.LENGTH_LONG).show()
                finish()
            }


        }




        send.setOnClickListener {

            if(uid!="") {

                val tempdatamapii:datamap = datamap(mutableMapOf<String,Any?>())
                tempdatamapii.map["DOCUMENTID"]= docID
                tempdatamapii.map["Type"]= "Comment"
                db.collection("User-Data").document(uid).collection(uid).document().set(clouddata2(NoteData().toString(tempdatamapii),System.nanoTime()))

                val collection = db.collection("Comments").document("Comments").collection(docID)

                val str: String = edittext2.text.toString()
                edittext2.setText("");
                UpdateInterest(10)
                val tempdatamap: datamap = datamap(mutableMapOf<String, Any?>())

                for ((k, v) in owner.map) {
                    tempdatamap.map[k] = v

                }


                tempdatamap.map["textview2"] = str;
                tempdatamap.map["textview1"] = Firebase.auth.currentUser!!.displayName.toString()
                tempdatamap.map["uid"] = Firebase.auth.currentUser!!.uid.toString()
                tempdatamap.map["imgview1"] = Firebase.auth.currentUser!!.photoUrl.toString()

                collection.document().set(clouddata(NoteData().toString(tempdatamap)))
                arr.add(0, tempdatamap)
                mAdapter9.update(arr)

            }else
                Toast.makeText(this,"Please Login first!",Toast.LENGTH_LONG).show()

        }








    }

    fun updateTag() {

        var n = 0;

        var i=0;
        if (bookpost.map.containsKey("TagN"))
            n = bookpost.map["TagN"] as Int

        while (i<n) {

            if(bookpost.map.containsKey("Tag-$i"))
                TagList.add(bookpost.map["Tag-$i"].toString())

i++
        }

    }







    fun UpdateInterest(percentage:Int){


        var all : Long =0;






        if(InterestMap.map.containsKey("ALL"))
            all = InterestMap.map["ALL"].toString().toLong()

        var n = TagList.size-1;


        val collection3 = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");


//        Toast.makeText(this,"${TagList.toString()}",Toast.LENGTH_LONG).show()


        while(n>=0){

            var current: Long =0;

            if(InterestMap.map.containsKey( "${TagList[n]}" ))
            {
                current = InterestMap.map["${TagList[n]}"].toString().toLong()

            }


            InterestMap.map["${TagList[n]}"]= current+ (all/100*percentage)+1;




            collection3.document(TagList[n]).set(clouddata("cloudata"))
            collection3.document(TagList[n]).collection(TagList[n]).document().set(clouddata("Category"))

//            Toast.makeText(this,"$n $current ${TagList[n]}",Toast.LENGTH_LONG).show()



            n--;

        }

        viewmodel3.deleteALL()

        viewmodel3.InsertNote(Note(NoteData().toString(InterestMap)))
//        Toast.makeText(this,"${InterestMap.toString()} after",Toast.LENGTH_LONG).show()



    }

    fun UpdateRcview(){


        arr.clear()
        rcview9.layoutManager = LinearLayoutManager(rcview9.context,
            LinearLayoutManager.VERTICAL,false)


        mAdapter9= rcviewadapter9(this)
        rcview9.adapter=mAdapter9


        val collection = db.collection("Comments").document("Comments").collection(docID)

        collection.get().addOnCompleteListener {

            if(!it.getResult().isEmpty){

                for(document in it.result!!){

                    val clouddata:clouddata = document.toObject(clouddata::class.java)

                    val datamap:datamap = NoteData().toDataMap(clouddata.datastr)
                    datamap.map["cid"] = document.id.toString()

                    arr.add(datamap)




                }

                mAdapter9.update(arr);


            }

        }




    }


    fun delete(docid:String){

        val collection = db.collection("Comments").document("Comments").collection(docID).document(docid).delete()




    }



}