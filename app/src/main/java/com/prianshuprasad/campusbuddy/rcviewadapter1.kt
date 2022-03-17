package com.prianshuprasad.campusbuddy



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class rcviewadapter1( private val listener: HomeFragment ): RecyclerView.Adapter<rcviewholder1>()
{
    private val item: ArrayList<datamap> = ArrayList()

    private val item2: MutableMap<String, ArrayList<datamap> > = mutableMapOf()
    var uid ="";
    val db = FirebaseFirestore.getInstance()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder1 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.homercviewitems,parent,false)

        val viewHolder=rcviewholder1(view)


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid



        viewHolder.imgview2.setOnClickListener {

            val postid = item[viewHolder.adapterPosition].map["DOCUMENTID"].toString()

            if(uid!=""){
                val doc = db.collection("Like").document(postid).collection(postid).document(uid).get()

                doc.addOnCompleteListener {
                    if(it.getResult().exists()){
                        viewHolder.imgview2.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
                        listener.UpdateInterest(-10,item[viewHolder.adapterPosition])
                        db.collection("Like").document(postid).collection(postid).document(uid).delete()

                    }else
                    {


                        val tempdatamapii:datamap = datamap(mutableMapOf<String,Any?>())
                        tempdatamapii.map["DOCUMENTID"]= postid
                        tempdatamapii.map["Type"]= "Like"
                        db.collection("User-Data").document(uid).collection(uid).document().set(clouddata2(NoteData().toString(tempdatamapii),System.nanoTime()))

                        listener.UpdateInterest(10,item[viewHolder.adapterPosition])
                        viewHolder.imgview2.setImageResource(R.drawable.ic_twotone_arrow_circle_up_24)
                        db.collection("Like").document(postid).collection(postid).document(uid).set(clouddata("liked"))

                    }
                }

            }else
                listener.makeToast("Please Login First!")



        }


        viewHolder.imgview3.setOnClickListener {

            listener.onitemclicked(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"Comment")



        }

        viewHolder.imgview1.setOnClickListener {

//            listener.onitemclicked(viewHolder.adapterPosition,"imgview1")

        }

        viewHolder.textView1.setOnClickListener{

            listener.onitemclicked(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"Post")

        }

        viewHolder.textView2.setOnClickListener{

            listener.onitemclicked(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"Post")

        }

        viewHolder.imgview1.setOnClickListener{

            listener.onitemclicked(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"Post")

        }
        viewHolder.clickablearea.setOnClickListener{

            listener.onitemclicked(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"Post")

        }


        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder1, position: Int) {

        val curritem= item[position]
        var mAdapter2: rcviewadapter2

        holder.rcview2.layoutManager = LinearLayoutManager(holder.rcview2.context,LinearLayoutManager.HORIZONTAL,false)


        var temparr:ArrayList<datamap> = ArrayList()

        mAdapter2= rcviewadapter2(this)
        holder.rcview2.adapter=mAdapter2
        temparr.clear()

        mAdapter2.update(temparr)


        holder.imgview1.setImageResource(R.drawable.ic_baseline_account_circle_24)
        holder.imgview2.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24)
        holder.imgview3.setImageResource(R.drawable.ic_baseline_comment_24)


        //Book

        if(curritem.map.containsKey("CategoryBook")){

            holder.textView1.text = curritem.map["CategoryBook"].toString()

            holder.imgview1.setImageDrawable(null)
            holder.imgview3.setImageDrawable(null)
            holder.imgview2.setImageDrawable(null)
            holder.textView3.setText(" ")

            holder.textView2.setText(" ")


            ////

            val collection = db.collection("Book").document("Book").collection(curritem.map["CategoryBook"].toString())

            collection.get().addOnCompleteListener(
                OnCompleteListener<QuerySnapshot?> { task ->
                    if (task.isSuccessful) {
   var i =0;
                        for (document in task.result!!) {

                            val clouddata: clouddata = document.toObject(clouddata::class.java)

                            val datamap1: datamap = NoteData().toDataMap(clouddata.datastr)

                            val datamap2: datamap = datamap(mutableMapOf<String, Any?>())
                            datamap2.map["IsBook"]=1;

                            if (datamap1.map.containsKey("DOCUMENTID")) {
                                datamap2.map["DOCUMENTID"] = datamap1.map["DOCUMENTID"]
                            }
                            if (datamap1.map.containsKey("bookname")) {
                                datamap2.map["rcviewitem2text1"] = datamap1.map["bookname"]
                            }
                            if (datamap1.map.containsKey("booklogo")) {
                                datamap2.map["imgview4"] = datamap1.map["booklogo"]

                            }

                            temparr.add(datamap2)
                            i++;
                            if(i>5)
                                break;

                        }
                        mAdapter2.update(temparr)

                    }

                        })






                            return

        }









val postid = item[position].map["DOCUMENTID"].toString()


        if(curritem.map.containsKey("time")){

            holder.textView3.text =  Utils().getTimeAgo(curritem.map["time"].toString().toLong()  )
        }else
            holder.textView3.text=" "


        if(uid!=""){
            val doc = db.collection("Like").document(postid).collection(postid).document(uid).get()

            doc.addOnCompleteListener {
                if(it.getResult().exists()){
                    holder.imgview2.setImageResource(R.drawable.ic_twotone_arrow_circle_up_24)
                }
            }

        }



        if(curritem.map.containsKey("textview1")) {
            holder.textView1.text = curritem.map["textview1"].toString()
        }else
            holder.textView1.text = "Anonymous"



        if(curritem.map.containsKey("textview2")) {
            holder.textView2.text = curritem.map["textview2"].toString()
        }

        if(curritem.map.containsKey("imgview1")) {
          Glide.with(holder.imgview1.context).load(curritem.map["imgview1"]).circleCrop().into(holder.imgview1)
        }



   if(curritem.map.containsKey("rcview2"))
        {




               if(item2.containsKey(curritem.map["DOCUMENTID"].toString()))
               temparr= item2[curritem.map["DOCUMENTID"].toString()] !!


            mAdapter2.update(temparr)



        }


//        holder.description.text= curritem.Description
//        Glide.with(holder.imageView.context).load(curritem.urltoimage).into(holder.imageView)


    }

    fun update(array:ArrayList<datamap>){

        item.clear()
        item.addAll(array)

        var i=0;
        var n =  array.size


        while(i<n){

            val datamaparr: ArrayList<datamap> = ArrayList()

            if(array[i].map.containsKey("rcview2")){


                val db= FirebaseFirestore.getInstance()




                val docid = array[i].map["DOCUMENTID"].toString()
                val collection= db.collection("Post").document("rcview2").collection(docid);


                collection.get().addOnCompleteListener(
                    OnCompleteListener<QuerySnapshot?> { task ->
                        if (task.isSuccessful) {
//                           val list: MutableList<String> = ArrayList()
                            for (document in task.result!!) {


                                val clouddata:clouddata= document.toObject(clouddata::class.java)

                                datamaparr.add( NoteData().toDataMap(clouddata.datastr)  )

                            }
                        } else {



                        }

                        item2[docid]= datamaparr
                        notifyDataSetChanged()



                    })



            }

            i++;

            if(i==n)
            {
                listener.refreshingdone()

            }

        }






        notifyDataSetChanged()

    }

    fun onrcview2clickedchild(docID:String,whatis:String){
listener.onrcview2clicked(docID,whatis)

    }



}




class rcviewholder1(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.textview1)
    val textView2: TextView=  itemViews.findViewById(R.id.textview2)
    val textView3: TextView=  itemViews.findViewById(R.id.textview3)
    val imgview1: ImageView=  itemViews.findViewById(R.id.imgview1)
    val imgview2: ImageView=  itemViews.findViewById(R.id.imgview2)
    val imgview3: ImageView=  itemViews.findViewById(R.id.imgview3)
    val rcview2: RecyclerView=  itemViews.findViewById(R.id.rcview2)
    val constlayout1:  ConstraintLayout= itemViews.findViewById(R.id.contlayout1)
    val clickablearea: ImageView = itemViews.findViewById(R.id.clickablearea)


}


interface itemclicked {
    fun onitemclicked(item: datamap)

}
