package com.prianshuprasad.campusbuddy





import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prianshuprasad.campusbuddy.ui.home.HomeFragment

class rcviewadapter6( private val listener: postBook): RecyclerView.Adapter<rcviewholder6>()
{
    //    private val item: ArrayList<datamap> = ArrayList()
    private val item: ArrayList<datamap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rcviewholder6{

        val view= LayoutInflater.from(parent.context).inflate(R.layout.taglayout,parent,false)

        val viewHolder=rcviewholder6(view)
        val n = item.size

        viewHolder.imageView.setOnClickListener {


            if (viewHolder.adapterPosition == 0) {

            } else {
                listener.onRemoveTag(viewHolder.adapterPosition)

            }
        }

  viewHolder.editText.doOnTextChanged{ text, start, count, after ->

       listener.edittextListener(viewHolder.editText.text.toString())


  }


        return viewHolder

    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: rcviewholder6, position: Int) {

        val curritem= item[position]


        if(position==0) {
            holder.imageView.setImageResource(R.drawable.ic_outline_add_box_24)
            holder.textView1.visibility = View.GONE
            holder.editText.visibility = View.VISIBLE
        }else
        if(curritem.map.containsKey("textview4")){

            holder.editText.visibility = View.GONE
            holder.textView1.visibility = View.VISIBLE
            holder.textView1.text = curritem.map["textview4"].toString()
            holder.imageView.setImageResource(R.drawable.ic_baseline_close_24)
        }


    }

    fun update(Newsarray:ArrayList<datamap>){



        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

    }




}




class rcviewholder6(itemViews: View): RecyclerView.ViewHolder(itemViews){

    val textView1: TextView=  itemViews.findViewById(R.id.textview4)
    val imageView:ImageView= itemViews.findViewById(R.id.removeButton)
    val editText:EditText = itemViews.findViewById(R.id.edittext1)




}


