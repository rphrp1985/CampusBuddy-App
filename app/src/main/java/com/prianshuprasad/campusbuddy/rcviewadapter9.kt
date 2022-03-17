package com.prianshuprasad.campusbuddy



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment
import com.squareup.okhttp.internal.DiskLruCache

class rcviewadapter9( private val listener: commentView): RecyclerView.Adapter<rcviewholder9>()
{
    //    private val item: ArrayList<datamap> = ArrayList()
    private val item: ArrayList<datamap> = ArrayList()
  val db = FirebaseFirestore.getInstance()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder9 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.homercviewitems,parent,false)

        val viewHolder=rcviewholder9(view)




        viewHolder.imgview2.visibility = View.GONE

        viewHolder.imgview3.visibility = View.GONE

        viewHolder.deleteAll.setOnClickListener {

            listener.delete(item[viewHolder.adapterPosition].map["cid"].toString())
            item.remove(item[viewHolder.adapterPosition])
            notifyDataSetChanged()

        }



        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder9, position: Int) {

        val curritem= item[position]
  holder.deleteAll.visibility = View.GONE


        if(Firebase.auth.currentUser!=null){
            if(curritem.map.containsKey("uid"))
            {
                if(Firebase.auth.currentUser!!.uid.toString()== curritem.map["uid"].toString())
                {
                    holder.deleteAll.visibility = View.VISIBLE
                }
            }
        }




        if(curritem.map.containsKey("textview1")) {
            holder.textView1.text = curritem.map["textview1"].toString()
        }else
            holder.textView1.text = "Unknown"


                if(curritem.map.containsKey("textview2")) {
            holder.textView2.text = curritem.map["textview2"].toString()
        }

        if(curritem.map.containsKey("imgview1"))
        {
            Glide.with(holder.imgview1.context).load(curritem.map["imgview1"]).circleCrop().into(holder.imgview1)
        }










    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder9(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.textview1)
    val textView2: TextView=  itemViews.findViewById(R.id.textview2)
    val textView3: TextView=  itemViews.findViewById(R.id.textview3)
    val imgview1: ImageView=  itemViews.findViewById(R.id.imgview1)
    val imgview2: ImageView=  itemViews.findViewById(R.id.imgview2)
    val imgview3: ImageView=  itemViews.findViewById(R.id.imgview3)
    val rcview2: RecyclerView=  itemViews.findViewById(R.id.rcview2)
    val constlayout1: ConstraintLayout = itemViews.findViewById(R.id.contlayout1)
    val clickablearea: ImageView = itemViews.findViewById(R.id.clickablearea)
    val deleteAll: ImageView = itemViews.findViewById(R.id.deleteAll)




}
