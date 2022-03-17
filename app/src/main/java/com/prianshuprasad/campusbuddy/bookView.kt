package com.prianshuprasad.campusbuddy

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.databinding.ActivityBookViewBinding
import kotlinx.android.synthetic.main.activity_book_view.*


class bookView : AppCompatActivity() {

    var docID=""
    var uid =""


    lateinit var viewmodel: NoteViewModel3


    lateinit var binding: ActivityBookViewBinding

lateinit var book : datamap


lateinit var booklogo:ImageView
lateinit var bookname:TextView
lateinit var fav:ImageView
lateinit var open:ImageView
lateinit var download:ImageView
lateinit var feedback: ImageView
lateinit var description :TextView
lateinit var progress:LinearProgressIndicator
lateinit var deleteAll :ImageView


    var InterestMap: datamap = datamap(mutableMapOf<String,Any?>())

val TagList:ArrayList<String> = ArrayList();


// download manager
var str: String? = null
    private var mgr: DownloadManager? = null
    private var enqueue: Long? = null


val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_view)

        binding = ActivityBookViewBinding.inflate(layoutInflater)


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid



        supportActionBar?.hide()

        deleteAll = findViewById(R.id.deleteAll)

        booklogo = findViewById(R.id.booklogo)
        bookname = findViewById(R.id.bookname)
        fav = findViewById(R.id.favButton)
        open = findViewById(R.id.open)
        download = findViewById(R.id.download)
        description = findViewById(R.id.description)
        progress = findViewById(R.id.linearProgressIndicator)
        feedback = findViewById(R.id.feedback)

        viewmodel= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( getApplication()  ) ).get(NoteViewModel3::class.java)



        viewmodel.allnotes.observe( this, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = NoteData().NotetoData(notelist)

                if(datalist.size>=1)
                    InterestMap= datalist[0];

//                Toast.makeText(this,"${InterestMap.toString()} fetched  ${datalist.size}",Toast.LENGTH_LONG).show()


            }
        })



        onProgress()

        description.setMovementMethod(ScrollingMovementMethod())



        docID = intent.getStringExtra("docID")!!;

        open.setOnClickListener {

            UpdateInterest(5)

            val intent  = Intent(this, pdfViewer::class.java)
            if(book.map.containsKey("fileurl")) {
                str = book.map["fileurl"].toString()
                intent.putExtra("fileurl",str);
                startActivity(intent)

            }else
                Toast.makeText(this,"Something went Wrong!",Toast.LENGTH_LONG).show()


        }


        feedback.setOnClickListener {

            val intent = Intent(this,commentView::class.java)

            intent.putExtra("docID",docID)
            intent.putExtra("whatis","Book")

            startActivity(intent)



        }



        updatefav()
        fav.setOnClickListener {


            if(uid!=""){


               val collection= db.collection("User-fav-Book").document(uid).collection(uid).document(docID).get();

                collection.addOnCompleteListener {

                    if(   it.getResult().exists()   ){

                        UpdateInterest(-10);

                        Toast.makeText(this,"Removed from favorites",Toast.LENGTH_LONG).show()
                        db.collection("User-fav-Book").document(uid).collection(uid).document(docID).delete();
                        fav.setImageResource(R.drawable.ic_baseline_star_border_24 )



                    }
                    else
                    {
                        UpdateInterest(10);

                        Toast.makeText(this,"Added to favorites",Toast.LENGTH_LONG).show()
                        db.collection("User-fav-Book").document(uid).collection(uid).document(docID).set(clouddata("cloudadat"))
                        fav.setImageResource(R.drawable.ic_baseline_star_24)



                    }


                }




            }
                else
            {
                Toast.makeText(this,"Please Login first!",Toast.LENGTH_LONG).show()

            }


        }





        download.setOnClickListener {

            UpdateInterest(5)

            mgr = getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
//        registerReceiver(broadCastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

            if(book.map.containsKey("fileurl")) {
                str = book.map["fileurl"].toString()
                downloadURLdata()

            }else
                Toast.makeText(this,"Something went Wrong!",Toast.LENGTH_LONG).show()
        }







    val doc = db.collection("Book").document("Book").collection("ALL").document(docID).get()


        doc.addOnCompleteListener {


            if(it.getResult().exists()) {

                val clouddata: clouddata = it.getResult().toObject(clouddata::class.java)!!

                book = NoteData().toDataMap(clouddata.datastr)
//                Toast.makeText(this,"${clouddata.datastr}",Toast.LENGTH_LONG).show()



                updateTagList()

                onComplete()


            } else
        {
            bookname.text ="NOT FOUND"
        bookname.visibility = View.VISIBLE
            linearProgressIndicator.visibility = View.GONE

        }

        }







deleteAll.setOnClickListener {


    db.collection("Book").document("Book").collection("ALL").document(docID).delete()


    var n = TagList.size

    var i=0;
    while(i<n){


        db.collection("Book").document("Book").collection("${TagList[i]}").document(docID).delete()

        i++
    }

    finish()

}



        backButton.setOnClickListener {

            finish()

        }




    }


    fun onProgress(){
                booklogo.visibility = View.GONE
        bookname.visibility = View.GONE
        download.visibility = View.GONE
        feedback.visibility = View.GONE
        open.visibility = View.GONE
        description.visibility = View.GONE

        progress.visibility = View.VISIBLE



    }
   fun onComplete(){
       booklogo.visibility = View.VISIBLE
       bookname.visibility = View.VISIBLE
       download.visibility = View.VISIBLE
       feedback.visibility = View.VISIBLE
       open.visibility = View.VISIBLE
       description.visibility = View.VISIBLE

       progress.visibility = View.GONE


       if (book.map.containsKey("bookname"))
           bookname.text = book.map["bookname"].toString()
//
       if (book.map.containsKey("description"))
           description.text = book.map["description"].toString()

       if (book.map.containsKey("booklogo"))
           Glide.with(booklogo.context).load(book.map["booklogo"]).centerCrop().into(booklogo)

     Handler().postDelayed({
         UpdateInterest(1)
     },4000)


       if(book.map.containsKey("uid")){
           if(uid==book.map["uid"])
               deleteAll.visibility = View.VISIBLE
       }

   }


    private fun downloadURLdata() {

        enqueue = mgr?.enqueue(
            DownloadManager.Request(Uri.parse(str))
                .setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
                .setTitle("Campus Buddy")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED )
                .setDescription("Downloading")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS ,
                    "campusBuddy"
                )
        )

    }


    fun updatefav() {

        if (uid != "") {

            val collection =
                db.collection( "User-fav-Book").document(uid).collection(uid).document(docID).get();

            collection.addOnCompleteListener {

                if (it.getResult().exists()) {

                    fav.setImageResource(R.drawable.ic_baseline_star_24)

                } else {
                    fav.setImageResource(R.drawable.ic_baseline_star_border_24)

                }


            }

        }


    }

    fun updateTagList(){
        var n :Int =0;
        if(book.map.containsKey("TagN"))
        n = book.map["TagN"] as Int

        var i=0;
        while(i<n){

            if(book.map.containsKey("Tag-${i}"))
                TagList.add( book.map[ "Tag-${i}" ].toString() )


            i++;

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

viewmodel.deleteALL()

        viewmodel.InsertNote(Note(NoteData().toString(InterestMap)))
//Toast.makeText(this,"${InterestMap.toString()} after",Toast.LENGTH_LONG).show()



    }




}