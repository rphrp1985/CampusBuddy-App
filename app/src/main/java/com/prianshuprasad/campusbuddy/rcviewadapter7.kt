package com.prianshuprasad.campusbuddy


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment

class rcviewadapter7( private val listener: postBook): RecyclerView.Adapter<rcviewholder7>()
{

    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder7{

        val view= LayoutInflater.from(parent.context).inflate(R.layout.taglayout,parent,false)

        val viewHolder=rcviewholder7(view)
        val n = item.size

view.setOnClickListener {

    listener.onSuggestionSelected(item[viewHolder.adapterPosition].map["Tag"].toString())
}



        return viewHolder

    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder7, position: Int) {

        val curritem= item[position]

        holder.textView1.setTextSize(18F)

holder.imageView.visibility = View.GONE;

        holder.editText.visibility = View.GONE
        holder.textView1.visibility = View.VISIBLE

        if(curritem.map.containsKey("Tag"))
            holder.textView1.text = curritem.map["Tag"].toString()



    }

    fun update(Newsarray:ArrayList<datamap>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder7(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.textview4)
    val imageView:ImageView= itemViews.findViewById(R.id.removeButton)
    val editText:EditText = itemViews.findViewById(R.id.edittext1)




}


