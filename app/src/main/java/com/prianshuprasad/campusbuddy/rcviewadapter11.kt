package com.prianshuprasad.campusbuddy

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
//import com.example.socialapp.Utils
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter
//import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.ui.notifications.NotificationsFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class rcviewadapter11(options: FirestoreRecyclerOptions<clouddata>, val listener: searchView) :
    FirestoreRecyclerAdapter<clouddata, viewholder11>(options) {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder11 {

        val viewholderi= viewholder11(LayoutInflater.from(parent.context).inflate(R.layout.homercviewitems,parent,false))


        viewholderi.imgview2.visibility = View.GONE
       viewholderi.imgview3.visibility = View.GONE
        viewholderi.textView3.visibility = View.GONE


        viewholderi.textView1.setOnClickListener {

            val clouddata2:clouddata2= snapshots.getSnapshot(viewholderi.adapterPosition).toObject(clouddata2::class.java)!!
            val datamap :datamap = NoteData().toDataMap(clouddata2.datastr)


            if(datamap.map.containsKey("textview1")) {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Post")
            }else
            {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Book")
            }




            }

        viewholderi.textView2.setOnClickListener {

            val clouddata2:clouddata2= snapshots.getSnapshot(viewholderi.adapterPosition).toObject(clouddata2::class.java)!!
            val datamap :datamap = NoteData().toDataMap(clouddata2.datastr)


            if(datamap.map.containsKey("textview1")) {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Post")
            }else
            {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Book")
            }




        }
        viewholderi.imgview1.setOnClickListener {

            val clouddata2:clouddata2= snapshots.getSnapshot(viewholderi.adapterPosition).toObject(clouddata2::class.java)!!
            val datamap :datamap = NoteData().toDataMap(clouddata2.datastr)


            if(datamap.map.containsKey("textview1")) {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Post")
            }else
            {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Book")
            }




        }


        viewholderi.clickablearea.setOnClickListener {

            val clouddata2:clouddata2= snapshots.getSnapshot(viewholderi.adapterPosition).toObject(clouddata2::class.java)!!
            val datamap :datamap = NoteData().toDataMap(clouddata2.datastr)


            if(datamap.map.containsKey("textview1")) {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Post")
            }else
            {

                listener.open(datamap.map["DOCUMENTID"].toString(),"Book")
            }




        }


        return viewholderi


    }

    override fun onBindViewHolder(holder: viewholder11, position: Int, model: clouddata) {

        val datamap:datamap = NoteData().toDataMap( model.datastr )

        holder.textView1.text ="jjjj"


        if(datamap.map.containsKey("textview1")){

            //its post

            if(datamap.map.containsKey("imgview1"))
            {
                Glide.with(holder.imgview1.context).load(datamap.map["imgview1"]).circleCrop().into(holder.imgview1)
            }
            holder.textView1.text = datamap.map["textview1"].toString()

            if(datamap.map.containsKey("textview2")){
                holder.textView2.text = datamap.map["textview2"].toString()
            }

        }else
        {

            //book

            if(datamap.map.containsKey("booklogo"))
            {
                Glide.with(holder.imgview1.context).load(datamap.map["booklogo"]).into(holder.imgview1)

            }

            if(datamap.map.containsKey("bookname"))
                holder.textView1.text = datamap.map["bookname"].toString()

            holder.textView2.text =" "

        }







    }
    interface IPostAdapter {
        fun onLikeClicked(postId: String)


    }
}

class viewholder11(item: View): RecyclerView.ViewHolder(item){

    val textView1: TextView=  item.findViewById(R.id.textview1)
    val textView2: TextView=  item.findViewById(R.id.textview2)
    val textView3: TextView=  item.findViewById(R.id.textview3)
    val imgview1: ImageView=  item.findViewById(R.id.imgview1)
    val imgview2: ImageView=  item.findViewById(R.id.imgview2)
    val imgview3: ImageView=  item.findViewById(R.id.imgview3)
    val rcview2: RecyclerView=  item.findViewById(R.id.rcview2)
    val constlayout1: ConstraintLayout = item.findViewById(R.id.contlayout1)
    val clickablearea: ImageView = item.findViewById(R.id.clickablearea)



}
