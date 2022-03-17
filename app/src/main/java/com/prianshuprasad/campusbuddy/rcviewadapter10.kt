package com.prianshuprasad.campusbuddy

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

class rcviewadapter10(options: FirestoreRecyclerOptions<clouddata2>, val listener: NotificationsFragment) :
    FirestoreRecyclerAdapter<clouddata2, viewholder>(options) {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {

        val viewholderi= viewholder(LayoutInflater.from(parent.context).inflate(R.layout.activitynnotification,parent,false))


        viewholderi.textView.setOnClickListener {

           val clouddata2:clouddata2= snapshots.getSnapshot(viewholderi.adapterPosition).toObject(clouddata2::class.java)!!
            val datamap :datamap = NoteData().toDataMap(clouddata2.datastr)

            if(datamap.map.containsKey("Type")){

                listener.actNotiClicked(datamap.map["DOCUMENTID"].toString(),datamap.map["Type"].toString())


            }



        }




        return viewholderi


    }

    override fun onBindViewHolder(holder: viewholder, position: Int, model: clouddata2) {

        holder.textView.visibility = View.VISIBLE



        val datamap: datamap = NoteData().toDataMap(model.datastr)

        if(datamap.map.containsKey("Type")){

            val type = datamap.map["Type"].toString()

            if(type =="Comment"){
                if(datamap.map["DOCUMENTID"].toString().substring(0,4)=="BOOK")
                holder.textView.text= "You gave a Feedback "
                else
                    holder.textView.text= "You commented on a post "
            }

            if(type=="Like"){


                    holder.textView.text= "You liked a post "

            }

            if(type=="Post")
                holder.textView.text= "You published a post"

            if(type=="Book")
                holder.textView.text= "You published a Pdf File"


            if(type=="Notification"){
                if(datamap.map["whatis"].toString()=="1"){
                    holder.textView.text= "You Turned on Notifications"
                }else
                    holder.textView.text= "You Turned off Notifications"

            }

            if(type=="NewComment"){
                holder.textView.text= "check out new comments"
            }
            if(type=="NewFeedback")
            {
                holder.textView.text= "Check out these new Feedbacks"
            }




        }






    }
    interface IPostAdapter {
        fun onLikeClicked(postId: String)


    }
}

class viewholder(item: View): RecyclerView.ViewHolder(item){

val textView:TextView = item.findViewById(R.id.textview4)



}
