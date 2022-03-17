package com.prianshuprasad.campusbuddy



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment

class rcviewadapter2( private val listener: rcviewadapter1): RecyclerView.Adapter<rcviewholder2>()
{
//    private val item: ArrayList<datamap> = ArrayList()
    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder2 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.rcviewitems2,parent,false)

        val viewHolder=rcviewholder2(view)






        viewHolder.imageView.setOnClickListener {

            if(item[viewHolder.adapterPosition].map.containsKey("IsBook"))
            {

                listener.onrcview2clickedchild(item[viewHolder.adapterPosition].map["DOCUMENTID"].toString(),"BOOK")


            }else {


                if (item[viewHolder.adapterPosition].map.containsKey("imgview4")) {

                    listener.onrcview2clickedchild(item[viewHolder.adapterPosition].map["imgview4"].toString(),
                        "imgurl")

                }
            }



        }



        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder2, position: Int) {

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



    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder2(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.rcviewitem2text1)
    val imageView:ImageView= itemViews.findViewById(R.id.imgview4)




}

interface Newsitemclicked {
    fun onitemclicked(item: datamap)

}
