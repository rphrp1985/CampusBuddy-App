package com.prianshuprasad.campusbuddy.ui.dashboard

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.*
import com.prianshuprasad.campusbuddy.databinding.FragmentDashboardBinding
import com.prianshuprasad.campusbuddy.databinding.FragmentHomeBinding
import java.io.DataOutputStream

class DashboardFragment : Fragment() {


    val db =  FirebaseFirestore.getInstance()
    private lateinit var mAdapter: rcviewadapter4

    lateinit var viewmodel: NoteViewModel2

    lateinit var viewmodel3: NoteViewModel3

    lateinit var viewmodel4: NoteViewModel4

    val notedata= NoteData()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    var uid =""

    var RecentSize =0
    var RecentBookList:ArrayList<Note> = ArrayList()

    var InterestMap: MutableMap<String,Long> = mutableMapOf<String,Long>()
    var GlobalInterestMap: MutableMap<String,Long> = mutableMapOf<String,Long>()

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid




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


        if(Firebase.auth.currentUser!=null)
            uid = Firebase.auth.currentUser!!.uid



        viewmodel3= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( requireActivity().getApplication()  ) ).get(NoteViewModel3::class.java)

        swipeRefreshLayout = binding.refreshLayout

        rcview1.layoutManager= LinearLayoutManager(root.context)

        mAdapter = rcviewadapter4(this)
        rcview1.adapter= mAdapter


        viewmodel= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( requireActivity().getApplication()  ) ).get(NoteViewModel2::class.java)


        viewmodel4= ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance( requireActivity().getApplication()  ) ).get(NoteViewModel4::class.java)


        viewmodel.allnotes.observe( viewLifecycleOwner, Observer{
            it?.let {



                var position =0;
                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = ArrayList()


               if(uid!="") {
                   val collectiontemp =
                       db.collection("User-fav-Book").document("$uid").collection("$uid")


                   collectiontemp.get().addOnCompleteListener(
                       OnCompleteListener<QuerySnapshot?> { task ->

                           if (task.getResult().size() > 0 && uid != "") {


                               val temparr: ArrayList<datamap> = ArrayList()
                               datalist.add(0,
                                   datamap(mutableMapOf<String, Any?>("Category" to "Favourite")));
                               position++;

                               for (document in task.result!!) {

                                   val bookid = document.id.toString()

                                   val docBook =
                                       db.collection("Book").document("Book").collection("ALL")
                                           .document(bookid).get()

                                   docBook.addOnCompleteListener {

                                       if (it.getResult().exists()) {

                                           val clouddata =
                                               it.getResult().toObject(clouddata::class.java)!!


                                           val datamap1: datamap =
                                               NoteData().toDataMap(clouddata.datastr)

                                           val datamap2: datamap =
                                               datamap(mutableMapOf<String, Any?>())

                                           if (datamap1.map.containsKey("DOCUMENTID")) {
                                               datamap2.map["DOCUMENTID"] =
                                                   datamap1.map["DOCUMENTID"]
                                           }
                                           if (datamap1.map.containsKey("bookname")) {
                                               datamap2.map["rcviewitem2text1"] =
                                                   datamap1.map["bookname"]
                                           }
                                           if (datamap1.map.containsKey("booklogo")) {
                                               datamap2.map["imgview4"] = datamap1.map["booklogo"]

                                           }

                                           temparr.add(datamap2)
                                           datalist[0].map["fav"] = temparr

                                           mAdapter.update(datalist)


                                       }

                                   }


                               }

                           }


                           ////////////////////////////////////////////////////////

                       })

               }
                if (RecentSize > 0) {
                    datalist.add(0,datamap(mutableMapOf<String, Any?>("Category" to "Recent")))

                    val temparr: ArrayList<datamap> = ArrayList()

                    var n = RecentSize;
                    var i = 0;

                    while (i < n) {

                        val tempdocid = RecentBookList[i].data;

                        val doc = db.collection("Book").document("Book").collection("ALL")
                            .document(tempdocid).get()

                        doc.addOnCompleteListener {

                            if (it.getResult().exists()) {
                                val clouddata: clouddata =
                                    it.getResult().toObject(clouddata::class.java)!!


                                val datamap1: datamap =
                                    NoteData().toDataMap(clouddata.datastr)

                                val datamap2: datamap =
                                    datamap(mutableMapOf<String, Any?>())

                                if (datamap1.map.containsKey("DOCUMENTID")) {
                                    datamap2.map["DOCUMENTID"] = datamap1.map["DOCUMENTID"]
                                }
                                if (datamap1.map.containsKey("bookname")) {
                                    datamap2.map["rcviewitem2text1"] =
                                        datamap1.map["bookname"]
                                }
                                if (datamap1.map.containsKey("booklogo")) {
                                    datamap2.map["imgview4"] = datamap1.map["booklogo"]

                                }

                                temparr.add(datamap2)



                                datalist[position].map["Recent"] = temparr
                                mAdapter.update(datalist)
                            }

                        }
                        i++;
                    }


                }



                val temparr:ArrayList<datamap> = notedata.NotetoData(notelist)


                for(datamap in temparr){


                    if(datamap.map.containsKey("Category")) {
                        val collection =  db.collection("Book").document("Book").collection(datamap.map["Category"].toString())

                        collection.get().addOnCompleteListener {

                            if(!it.getResult().isEmpty){

                                datalist.add(datamap)

                                mAdapter.update(datalist)
                            }


                        }





                    }
                }


                mAdapter.update(datalist)



            }
        })


        viewmodel4.allnotes.observe(viewLifecycleOwner, Observer{
            it?.let {


                RecentBookList= it as ArrayList<Note>
                RecentSize = RecentBookList.size

            }
        })





        fetch()

        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
            fetch()


        }


        binding.postadd.setOnClickListener {

            (activity as MainActivity).addbook();

        }

        binding.searchButton.setOnClickListener {
            (activity as MainActivity).opensearch()
        }


        fetchInterestMap()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun onitemclicked( position:Int,whatis : String){

//        (activity as MainActivity).onpostclicked(position,whatis)

    }

    fun onrcview2clicked(documentID : String)
    {

        (activity as MainActivity).onrcview2clicked(documentID,"BOOK")
        viewmodel4.InsertNote(Note(documentID))


    }


    fun updatelist()
    {
        val arr: ArrayList<datamap> = ArrayList()
        mAdapter.update(arr)
    }



    fun fetchInterestMap(){

        val db = FirebaseFirestore.getInstance()


        var Interesttemp: datamap = datamap(mutableMapOf<String,Any?>())

        viewmodel.allnotes.observe( viewLifecycleOwner, Observer{
            it?.let {


                var notelist= it as ArrayList<Note>
                var datalist: ArrayList<datamap> = NoteData().NotetoData(notelist)

                if(datalist.size>=1)
                    Interesttemp= datalist[0];
            }
        })


        for ((k,v) in Interesttemp.map){

            InterestMap[k] = v as Long

        }
        fetch()



        val collection = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");

        collection.get().addOnCompleteListener(
            OnCompleteListener<QuerySnapshot?> { task ->

                val n = task.getResult().size()
                var i =0;


                if (task.isSuccessful) {



                    for (document in task.result!!) {



                        val cat: String = document.id.toString()
                        val collection2 = collection.document(cat).collection(cat);




                        collection2.get().addOnCompleteListener(
                            OnCompleteListener<QuerySnapshot?> { task ->
                                if (task.isSuccessful) {

                                    GlobalInterestMap[cat] = task.result!!.size().toLong()

                                    i++;
                                    check(n,i)

                                }
                            })

                    }



                }
            })


//fetch()


    }



    fun check(n:Int,i:Int){
        if(i==n) {

            fetch()

        }
    }



    fun fetch(){

        viewmodel.deleteALL()


        viewmodel.InsertNote(Note( NoteData().toString(datamap(mutableMapOf<String,Any?>("Category" to "ALL")))   ));

        val IsVisited: MutableMap<String,Int> = mutableMapOf()

        val Interest = InterestMap.toList().sortedBy { (k, v) -> v }.reversed().toMap()

        val Global = GlobalInterestMap.toList().sortedBy { (k, v) -> v }.reversed().toMap()


        for ((key, value) in Interest) {

            viewmodel.InsertNote( Note( NoteData().toString(datamap(mutableMapOf<String,Any?>("Category" to "$key")))   ));

            IsVisited["$key"]=1;

        }

        for ((key, value) in Global) {

            if(  !(IsVisited.containsKey("$key")) ) {
                viewmodel.InsertNote(  Note(NoteData().toString(datamap(mutableMapOf<String, Any?>("Category" to "$key"))))  );

            }

        }




    }


    fun refreshingdone()
    {
        swipeRefreshLayout.isRefreshing = false
    }


    fun clearRecent(){
        viewmodel4.deleteALL()
        fetch()
    }

}