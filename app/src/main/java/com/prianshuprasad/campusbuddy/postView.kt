package com.prianshuprasad.campusbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_post_view.*

class postView : AppCompatActivity() {


    var InterestDataMap: datamap = datamap(mutableMapOf<String,Any?>())
    lateinit var viewmodel3: NoteViewModel3

    val TagList:ArrayList<String> = ArrayList()
    var postid=""
    val db = FirebaseFirestore.getInstance()
    lateinit var post:datamap

    var uid =""

    lateinit var like:ImageView
    lateinit var mAdapter2: rcviewadapter8




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        supportActionBar?.hide()



        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid


        postid = intent.getStringExtra("docID")!!


        like = findViewById(R.id.imgview2)

        val doc=
            db.collection("Post").document("Post").collection("ALL").document(postid).get()

        doc.addOnCompleteListener {

            if(it.getResult().exists())
            {
                linearProgressIndicator.visibility = View.GONE;
                val clouddata : clouddata = it.getResult().toObject(clouddata::class.java)!!
                post = NoteData().toDataMap(clouddata.datastr)




                updatetagList()

                if(post.map.containsKey("textview1")) {
                    textview1.text = post.map["textview1"].toString()
                }

                if(post.map.containsKey("textview2")) {
                    textview2.text = post.map["textview2"].toString()
                }

                imgview2.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                imgview3.setImageResource(R.drawable.ic_baseline_comment_24)

                if(post.map.containsKey("imgview1")) {
                    Glide.with(imgview1.context).load(post.map["imgview1"]).circleCrop().into(imgview1)
                }


                if(post.map.containsKey("uid")){
                    if(post.map["uid"].toString()==uid)
                    {
                        delete.visibility = View.VISIBLE
                    }
                }

                check()

                rcview2.layoutManager = LinearLayoutManager(rcview2.context,
                    LinearLayoutManager.HORIZONTAL,false)
                if(post.map.containsKey("rcview2"))
                {


                    var temparr:ArrayList<datamap> = ArrayList()


                    val docid = post.map["DOCUMENTID"].toString()
                    val collection= db.collection("Post").document("rcview2").collection(docid);


                    collection.get().addOnCompleteListener(
                        OnCompleteListener<QuerySnapshot?> { task ->
                            if (task.isSuccessful) {
//                           val list: MutableList<String> = ArrayList()
                                for (document in task.result!!) {


                                    val clouddata:clouddata= document.toObject(com.prianshuprasad.campusbuddy.clouddata::class.java)

                                    temparr.add( NoteData().toDataMap(clouddata.datastr)  )

                                }
                            } else {



                            }


                            mAdapter2= rcviewadapter8(this)
                            rcview2.adapter=mAdapter2

                            mAdapter2.update(temparr)


                        })
                }






            }else
            {


                Toast.makeText(this,"An Error Occurred",Toast.LENGTH_LONG).show()
                finish()

            }

        }


        viewmodel3= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( getApplication()  ) ).get(NoteViewModel3::class.java)

        viewmodel3.allnotes.observe( this, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = NoteData().NotetoData(notelist)

                if(datalist.size>=1) {
                    InterestDataMap = datalist[0];


                }

            }
        })



  imgview3.setOnClickListener {
      val intent = Intent(this,commentView::class.java)
      intent.putExtra("docID",postid);
      intent.putExtra("whatis","Post")
      startActivity(intent)

  }



        imgview2.setOnClickListener {
            likeclicked()
        }

delete.setOnClickListener {


    db.collection("Post").document("Post").collection("ALL").document(postid).delete()


    var n = TagList.size

    var i=0;
    while(i<n){


        db.collection("Post").document("Post").collection("${TagList[i]}").document(postid).delete()

        i++
    }

    finish()



}




    }




    fun check(){

        if(uid!=""){
        val doc = db.collection("Like").document(postid).collection(postid).document(uid).get()

            doc.addOnCompleteListener {
                if(it.getResult().exists()){
                    like.setImageResource(R.drawable.ic_twotone_arrow_circle_up_24)
                }
            }

    }}


    fun likeclicked(){

        if(uid!=""){
            val doc = db.collection("Like").document(postid).collection(postid).document(uid).get()

            doc.addOnCompleteListener {
                if(it.getResult().exists()){
                    like.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                    UpdateInterest(-10,post)
                    db.collection("Like").document(postid).collection(postid).document(uid).delete()

                }else
                {

                    val tempdatamapii:datamap = datamap(mutableMapOf<String,Any?>())
                    tempdatamapii.map["DOCUMENTID"]= postid
                    tempdatamapii.map["Type"]= "Like"
                    db.collection("User-Data").document(uid).collection(uid).document().set(clouddata2(NoteData().toString(tempdatamapii),System.nanoTime()))


                    like.setImageResource(R.drawable.ic_twotone_arrow_circle_up_24)
                    UpdateInterest(10,post)
                    db.collection("Like").document(postid).collection(postid).document(uid).set(clouddata("liked"))

                }
            }

        }else
            Toast.makeText(this,"Please Login First!",Toast.LENGTH_LONG).show()

    }


    fun updatetagList(){



        var TagN:Int =0;

        if(post.map.containsKey("TagN"))
            TagN = post.map["TagN"] as Int



        var i =0;

        while(i<TagN){




            if(post.map.containsKey("Tag-$i")) {
                TagList.add(post.map["Tag-$i"].toString())


            }
            i++;
        }





    }




    fun UpdateInterest(percentage:Int,datamap: datamap){


        var all : Long =0;






        if( InterestDataMap.map.containsKey("ALL") )
            all = InterestDataMap.map["ALL"].toString().toLong()

        var n = TagList.size-1;


        val collection3 = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");



        while(n>=0){

            var current: Long =0;

            if(InterestDataMap.map.containsKey( "${TagList[n]}" ))
            {
                current = InterestDataMap.map["${TagList[n]}"].toString().toLong()

            }


            InterestDataMap.map["${TagList[n]}"]= current+ (all/100*percentage)+1;




            collection3.document(TagList[n]).set(clouddata("cloudata"))
            collection3.document(TagList[n]).collection(TagList[n]).document().set(clouddata("Category"))


            n--;


        }






        viewmodel3.deleteALL()
        viewmodel3.InsertNote(Note(NoteData().toString(InterestDataMap)))


    }




    fun openImage(url:String){

        val intent = Intent(this,imgView::class.java);

        intent.putExtra("imgurl",url);
        startActivity(intent)
    }







}