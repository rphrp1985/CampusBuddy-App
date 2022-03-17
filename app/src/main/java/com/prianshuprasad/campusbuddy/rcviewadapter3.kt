package com.prianshuprasad.campusbuddy

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment




class rcviewadapter3( private val listener: addPost): RecyclerView.Adapter<rcviewholder3>()
{
    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder3 {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.rcviewitems2,parent,false)

        val viewHolder=rcviewholder3(view)

        viewHolder.textView1.visibility= View.GONE;
        viewHolder.delete.visibility= View.VISIBLE

  viewHolder.imageView.alpha = 0.8F



        viewHolder.delete.setOnClickListener {

           listener.deleteImage(viewHolder.adapterPosition);


        }



        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder3, position: Int) {

        val curritem= item[position]

        if(curritem.map.containsKey("imgview4"))
            holder.imageView.setImageURI(curritem.map["imgview4"] as Uri )



    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder3(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.rcviewitem2text1)
    val imageView:ImageView= itemViews.findViewById(R.id.imgview4)
    val delete: ImageView =itemViews.findViewById(R.id.deleteimage)




}

