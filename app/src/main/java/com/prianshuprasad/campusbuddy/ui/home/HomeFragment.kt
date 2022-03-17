package com.prianshuprasad.campusbuddy.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.prianshuprasad.campusbuddy.*
import com.prianshuprasad.campusbuddy.databinding.FragmentHomeBinding


class HomeFragment() : Fragment() {
    private lateinit var mAdapter: rcviewadapter1

    val db = FirebaseFirestore.getInstance()

    var Interesttemp: datamap = datamap(mutableMapOf<String,Any?>());


    var LocalTotalInterest:Long =0;
    var GlobalTotalInterest:Long =0;

    var InterestDataMap: datamap = datamap(mutableMapOf<String,Any?>())

    var InterestMap: MutableMap<String,Long> = mutableMapOf<String,Long>()
    var GlobalInterestMap: MutableMap<String,Long> = mutableMapOf<String,Long>()


    private var _binding: FragmentHomeBinding? = null
    lateinit var viewmodel3: NoteViewModel3
    lateinit var viewmodel: NoteViewModel
    val notedata= NoteData()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rcview1: RecyclerView = binding.rcview1



        rcview1.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <0) {
                    binding.postadd.visibility = View.GONE
                    binding.searchButton.visibility = View.GONE

                } else {
                    // Scrolling down
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    binding.postadd.visibility = View.GONE
                    binding.searchButton.visibility = View.GONE
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    binding.postadd.visibility = View.VISIBLE
                    binding.searchButton.visibility = View.VISIBLE

                    // Do something
                } else {
                    binding.postadd.visibility = View.VISIBLE
                    binding.searchButton.visibility = View.VISIBLE
                }
            }
        })



        binding.searchButton.setOnClickListener {
            (activity as MainActivity).opensearch()
        }

         swipeRefreshLayout = binding.refreshLayout

        rcview1.layoutManager= LinearLayoutManager(root.context)

        mAdapter= rcviewadapter1(this)
        rcview1.adapter= mAdapter



        viewmodel3= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( requireActivity().getApplication()  ) ).get(NoteViewModel3::class.java)

        viewmodel3.allnotes.observe( viewLifecycleOwner, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = NoteData().NotetoData(notelist)

                if(datalist.size>=1) {
                    InterestDataMap = datalist[0];
                    Interesttemp = datalist[0]

                }

            }
        })







        viewmodel= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( requireActivity().getApplication()  ) ).get(NoteViewModel::class.java)



        viewmodel.allnotes.observe(viewLifecycleOwner, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = notedata.NotetoData(notelist)


//                updateAdapter(datalist)


                mAdapter.update(datalist)
            }
        })




        Handler().postDelayed({

            fetchInterestMap()
        },1500)







        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
          fetch()


        }



        binding.postadd.setOnClickListener {



            (activity as MainActivity).addpost();

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun onitemclicked( str:String,whatis : String){

        (activity as MainActivity).onpostclicked(str,whatis)

    }

    fun onrcview2clicked(documentID : String,whtatis:String)
    {

        (activity as MainActivity).onrcview2clicked(documentID, whtatis)

    }


    fun updatelist()
    {
        val arr: ArrayList<datamap> = ArrayList()
        mAdapter.update(arr)
    }





    fun fetch(){


   viewmodel.deleteALL()



        val IsVisited: MutableMap<String,Int> = mutableMapOf()

        val Interest = InterestMap.toList().sortedBy { (k, v) -> v }.reversed().toMap()

        val Global = GlobalInterestMap.toList().sortedBy { (k, v) -> v }.reversed().toMap()


//        (activity as MainActivity).makeToast(Interest.toString())


        val db= FirebaseFirestore.getInstance()

        val collection = db.collection("Post").document("Post")




            for ((key, value) in Interest) {




                //book implemetation

                    if(!IsVisited.containsKey(key)) {

                        val collectionBook =
                            db.collection("Book").document("Book").collection("$key")

                        collectionBook.get().addOnCompleteListener {

                            if (!it.getResult().isEmpty) {

                                val datamapBook: datamap = datamap(mutableMapOf<String, Any?>())

                                datamapBook.map["CategoryBook"] = key
//                                (activity as MainActivity).makeToast("$key")
                                viewmodel.InsertNote(Note(NoteData().toString(datamapBook)));

                            }


                        }

                        IsVisited[key] = 1
                    }







                collection.collection("$key").get().addOnCompleteListener(
                    OnCompleteListener<QuerySnapshot?> { task ->
                        if (!task.getResult().isEmpty) {

                            for (document in task.result!!) {


                                val clouddata: clouddata = document.toObject(clouddata::class.java)
                                val datamap = NoteData().toDataMap(clouddata.datastr)





//                                    viewmodel.InsertNote(Note(clouddata.datastr));

                                if (datamap.map.containsKey("DOCUMENTID")) {

                                    if (!IsVisited.containsKey(datamap.map["DOCUMENTID"].toString())) {

                                        viewmodel.InsertNote(Note(clouddata.datastr));

                                        IsVisited[datamap.map["DOCUMENTID"].toString()] = 1
                                    }
                                }


                            }


                        }
                    })
            }





            for ((key, value) in Global) {

                collection.collection("$key").get().addOnCompleteListener(
                    OnCompleteListener<QuerySnapshot?> { task ->
                        if (!task.getResult().isEmpty) {

                            for (document in task.result!!) {


                                val clouddata: clouddata = document.toObject(clouddata::class.java)
                                val datamap = NoteData().toDataMap(clouddata.datastr)
//                            viewmodel.InsertNote(Note(clouddata.datastr));

                                if (datamap.map.containsKey("DOCUMENTID")) {

                                    if (!IsVisited.containsKey(datamap.map["DOCUMENTID"].toString())) {

                                        viewmodel.InsertNote(Note(clouddata.datastr));

                                        IsVisited[datamap.map["DOCUMENTID"].toString()] = 1
                                    }
                                }


                            }


                        }
                    })
            }


        }




fun refreshingdone()
{
    swipeRefreshLayout.isRefreshing = false
}



    fun UpdateInterest(percentage:Int,datamap: datamap){


        var all : Long =0;



        val TagList:ArrayList<String> = ArrayList()
        var TagN:Int =0;

        if(datamap.map.containsKey("TagN"))
            TagN = datamap.map["TagN"] as Int



var i =0;

        while(i<TagN){




            if(datamap.map.containsKey("Tag-$i")) {
                TagList.add(datamap.map["Tag-$i"].toString())


            }
            i++;
        }







        if( InterestDataMap.map.containsKey("ALL") )
            all = InterestDataMap.map["ALL"].toString().toLong()

        var n = TagList.size-1;


        val collection3 = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");



        while(n>=0){

            var current: Long =0;

            if(InterestDataMap.map.containsKey( "${TagList[n]}" ))
            {
                current = InterestDataMap.map["${TagList[n]}"].toString().toLong()

            }


            InterestDataMap.map["${TagList[n]}"]= current+ (all/100*percentage)+1;




            collection3.document(TagList[n]).set(clouddata("cloudata"))
            collection3.document(TagList[n]).collection(TagList[n]).document().set(clouddata("Category"))


            n--;


        }



        viewmodel3.deleteALL()
        viewmodel3.InsertNote(Note(NoteData().toString(InterestDataMap)))


    }




    fun fetchInterestMap(){

        val db = FirebaseFirestore.getInstance()

LocalTotalInterest =0


        for ((k,v) in Interesttemp.map){

            InterestMap[k] = v.toString().toLong()

            LocalTotalInterest += v.toString().toLong()


        }




   GlobalTotalInterest=0
        val collection = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");

        collection.get().addOnCompleteListener(
            OnCompleteListener<QuerySnapshot?> { task ->
                if (task.isSuccessful) {

             val n = task.getResult().size()
                    var i=0

                    for (document in task.result!!) {



                        val cat: String = document.id.toString()
                        val collection2 = collection.document(cat).collection(cat);


                        collection2.get().addOnCompleteListener(
                            OnCompleteListener<QuerySnapshot?> { task ->
                                if (task.isSuccessful) {

                                    GlobalInterestMap[cat] = task.result!!.size().toLong()
                                    GlobalTotalInterest+= GlobalInterestMap[cat]!!

                                    i++;
                                    check(n,i);

                                }

                            })

                    }



                }
            })









    }


    fun check(n:Int,i:Int) {
        if (i == n) {
            GlobalInterestMap["ALL"]=0;
            fetch()
        }
    }


    override fun onDestroy() {
        super.onDestroy()




    }

fun makeToast(str:String){
    (activity as MainActivity).makeToast(str)
}




}