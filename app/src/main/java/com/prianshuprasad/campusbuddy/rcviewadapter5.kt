package com.prianshuprasad.campusbuddy



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment

class rcviewadapter5( private val listener: rcviewadapter4): RecyclerView.Adapter<rcviewholder5>()
{
    //    private val item: ArrayList<datamap> = ArrayList()
    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder5{

        val view= LayoutInflater.from(parent.context).inflate(R.layout.rcviewitems2,parent,false)

        val viewHolder=rcviewholder5(view)



        view.setOnClickListener {



//            listener.onrcview2clicked(item[viewHolder.adapterPosition].map["DOCUMENTID"] as String)
            if(item[viewHolder.adapterPosition].map.containsKey("DOCUMENTID"))
                listener.onrcview2clickedchild(item[viewHolder.adapterPosition].map["DOCUMENTID"] as String)
            else
                listener.onrcview2clickedchild("0")


        }

        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder5, position: Int) {

        val curritem= item[position]

        if(curritem.map.containsKey("imgview4"))
        {

            Glide.with(holder.imageView.context).load(curritem.map["imgview4"]).centerCrop().into(holder.imageView)
        }

        if(curritem.map.containsKey("rcviewitem2text1"))
        {

            holder.textView1.visibility = View.VISIBLE
            holder.textView1.text = curritem.map["rcviewitem2text1"].toString()
        }else
            holder.textView1.text=""


    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder5(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.rcviewitem2text1)
    val imageView:ImageView= itemViews.findViewById(R.id.imgview4)




}


