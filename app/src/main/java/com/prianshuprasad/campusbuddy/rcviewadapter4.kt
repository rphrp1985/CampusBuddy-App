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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.prianshuprasad.campusbuddy.ui.dashboard.DashboardFragment
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.sql.Types.NULL


class rcviewadapter4( private val listener: DashboardFragment ): RecyclerView.Adapter<rcviewholder4>()
{
    private val item: ArrayList<datamap> = ArrayList()

    private val item2: MutableMap<String, ArrayList<datamap> > = mutableMapOf()






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder4 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.homercviewitems,parent,false)

        val viewHolder=rcviewholder4(view)


        viewHolder.imgview1.setOnClickListener {

            listener.onitemclicked(viewHolder.adapterPosition,"imgview1")

        }

        view.setOnClickListener{


        }

        viewHolder.deleteall.setOnClickListener {
            listener.clearRecent()
        }

        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder4, position: Int) {

        holder.deleteall.visibility= View.GONE

        val curritem= item[position]
        var mAdapter2: rcviewadapter5
        holder.imgview1.setImageResource(NULL)
        holder.imgview2.setImageResource(NULL)
        holder.imgview3.setImageResource(NULL)
        holder.textView3.text =" "
        holder.textView2.text =" "



        if(curritem.map.containsKey("Category")) {
            holder.textView1.text = curritem.map["Category"].toString()






            holder.rcview2.layoutManager =
                LinearLayoutManager(holder.rcview2.context, LinearLayoutManager.HORIZONTAL, false)

            var temparr: ArrayList<datamap> = ArrayList()



            mAdapter2= rcviewadapter5(this)
            holder.rcview2.adapter=mAdapter2


            if(curritem.map.containsKey("Recent")){

                holder.deleteall.visibility = View.VISIBLE
                temparr= curritem.map["Recent"] as ArrayList<datamap>
                mAdapter2.update(temparr)
                return

            }
            if(curritem.map.containsKey("fav")){


                temparr= curritem.map["fav"] as ArrayList<datamap>
                mAdapter2.update(temparr)
                return

            }


            val db = FirebaseFirestore.getInstance()

            val collection = db.collection("Book").document("Book").collection(curritem.map["Category"].toString())

            collection.get().addOnCompleteListener(
                OnCompleteListener<QuerySnapshot?> { task ->
                    if (task.isSuccessful) {

                        for (document in task.result!!) {

                           val clouddata: clouddata = document.toObject(clouddata::class.java)

                            val datamap1:datamap = NoteData().toDataMap(clouddata.datastr)

                            val datamap2:datamap = datamap(mutableMapOf<String,Any?>())

                            if(datamap1.map.containsKey("DOCUMENTID"))
                            {
                                datamap2.map["DOCUMENTID"]= datamap1.map["DOCUMENTID"]
                            }
                            if(datamap1.map.containsKey("bookname"))
                            {
                                datamap2.map["rcviewitem2text1"]= datamap1.map["bookname"]
                            }
                            if(datamap1.map.containsKey("booklogo"))
                            {
                                datamap2.map["imgview4"]= datamap1.map["booklogo"]

                            }

                            temparr.add(datamap2)

                        }


                        mAdapter2.update(temparr)


                    } else {


                    }
                })






            mAdapter2.update(temparr)




        }



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

    fun onrcview2clickedchild(docID:String){
        listener.onrcview2clicked(docID)

    }



}




class rcviewholder4(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.textview1)
    val textView2: TextView=  itemViews.findViewById(R.id.textview2)
    val textView3: TextView=  itemViews.findViewById(R.id.textview3)
    val imgview1: ImageView=  itemViews.findViewById(R.id.imgview1)
    val imgview2: ImageView=  itemViews.findViewById(R.id.imgview2)
    val imgview3: ImageView=  itemViews.findViewById(R.id.imgview3)
    val rcview2: RecyclerView=  itemViews.findViewById(R.id.rcview2)
    val constlayout1:  ConstraintLayout= itemViews.findViewById(R.id.contlayout1)
    val deleteall:ImageView = itemViews.findViewById(R.id.deleteAll)


}


