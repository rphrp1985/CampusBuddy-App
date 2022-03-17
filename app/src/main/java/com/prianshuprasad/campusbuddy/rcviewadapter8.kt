package com.prianshuprasad.campusbuddy



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment

class rcviewadapter8( private val listener: postView): RecyclerView.Adapter<rcviewholder8>()
{
    //    private val item: ArrayList<datamap> = ArrayList()
    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder8 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.rcviewitems2,parent,false)

        val viewHolder=rcviewholder8(view)



       viewHolder.imageView.setOnClickListener {

   listener.openImage(item[viewHolder.adapterPosition].map["imgview4"].toString())


       }

        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder8, position: Int) {

        val curritem= item[position]

        if(curritem.map.containsKey("imgview4"))
        {

            Glide.with(holder.imageView.context).load(curritem.map["imgview4"]).into(holder.imageView)
        }

        if(curritem.map.containsKey("rcviewitem2text1"))
        {

            holder.textView1.text = curritem.map["rcviewitem2text1"].toString()
        }else
            holder.textView1.text=""


//        holder.textView1.text= "By RP"

//        holder.textView1.text  = curritem.map["textview1"].toString()
//        holder.description.text= curritem.Description
//        Glide.with(holder.imageView.context).load(curritem.urltoimage).into(holder.imageView)


    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder8(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.rcviewitem2text1)
    val imageView:ImageView= itemViews.findViewById(R.id.imgview4)




}
