package com.prianshuprasad.campusbuddy.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.*
import com.prianshuprasad.campusbuddy.databinding.FragmentNotificationsBinding
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.log

class NotificationsFragment : Fragment() {

    private lateinit var adapter: rcviewadapter10
    var RC_SIGN_IN: Int=123
    var TAG="1"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    var uid=""
    val db = FirebaseFirestore.getInstance()




    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        if(Firebase.auth.currentUser!=null)
            uid = Firebase.auth.currentUser!!.uid



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id ))
            .requestEmail()
            .build()



        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth= Firebase.auth


binding.profilepic.setImageDrawable(null)

        binding.profileName.setOnClickListener {
            signIn()
        }


        binding.rcview10.layoutManager= LinearLayoutManager(root.context)



        if(uid!=""){
            binding.profileName.text= Firebase.auth.currentUser!!.displayName.toString()

            Glide.with(binding.profilepic.context).load(Firebase.auth.currentUser!!.photoUrl.toString()).circleCrop().into(binding.profilepic)


        }else
        {
            binding.logout.visibility= View.GONE
        }


        binding.logout.setOnClickListener {

            Firebase.auth.signOut()

            binding.profilepic.setImageDrawable(null)
         binding.profileName.text="Login With Google"
            uid= ""
            binding.logout.visibility = View.GONE

setuprecyclerview()
        }



        if(uid!="") {
            val notidoc =
                db.collection("User-Data").document(uid).collection(uid).document("Notification")
                    .get()



            notidoc.addOnCompleteListener {

                if (it.getResult().exists()) {
                    val clouddata2: clouddata2 = it.getResult()!!.toObject(clouddata2::class.java)!!
                    val datamap: datamap = NoteData().toDataMap(clouddata2.datastr)

                    if (datamap.map.containsKey("whatis")) {

                        if (datamap.map["whatis"].toString() == "1") {
                            binding.switcha.isChecked = true

                        } else
                            binding.switcha.isChecked = false

                    }


                }

            }

        }


        setuprecyclerview()
        val notidatamap: datamap = datamap((mutableMapOf<String, Any?>()))

        notidatamap.map["Type"] = "Notification"

        notidatamap.map["DOCUMENTID"] = "Notification"

        binding.switcha.setOnCheckedChangeListener { buttonView, isChecked ->

            if(uid!="") {
                if (isChecked) {
                    notidatamap.map["whatis"] = "1"
                    val collection = db.collection("User-Data").document(uid).collection(uid)
                        .document("Notification")
                        .set(clouddata2(NoteData().toString(notidatamap), System.nanoTime()))


                } else {
                    notidatamap.map["whatis"] = "0"
                    val collection = db.collection("User-Data").document(uid).collection(uid)
                        .document("Notification")
                        .set(clouddata2(NoteData().toString(notidatamap), System.nanoTime()))

                }

            }
            else
            {
                binding.switcha.isChecked = false
                (activity as MainActivity).makeToast("Please login first")

            }

        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn() {


        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handlesignin(task)
        }
    }

    private fun handlesignin(task: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {

            (activity as MainActivity).makeToast("failed")

            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)

        }


    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        GlobalScope.launch(Dispatchers.IO) {
            val auth= auth.signInWithCredential(credential).await()
            val firebaseUser= auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }



    }


    private fun updateUI(firebaseUser: FirebaseUser?) {



        if(firebaseUser!=null)
        {

            uid = Firebase.auth.currentUser!!.uid.toString()

             binding.profileName.text= Firebase.auth.currentUser!!.displayName.toString()

            Glide.with(binding.profilepic.context).load(Firebase.auth.currentUser!!.photoUrl.toString()).circleCrop().into(binding.profilepic)
     binding.logout.visibility = View.VISIBLE
            setuprecyclerview()

        }
        else
        {

        }

    }

    fun setuprecyclerview()
    {

        if(uid!="") {


            binding.rcview10.visibility = View.VISIBLE
            val collection = db.collection("User-Data").document(uid).collection(uid)

            val query = collection.orderBy("time", Query.Direction.DESCENDING)
            val recyclerViewoptions =
                FirestoreRecyclerOptions.Builder<clouddata2>().setQuery(query, clouddata2::class.java)
                    .build()
            adapter = rcviewadapter10(recyclerViewoptions, this)
            binding.rcview10.adapter = adapter

            adapter.startListening()


        }else
            binding.rcview10.visibility = View.GONE


    }


    fun actNotiClicked(docID:String,type:String){

        (activity as MainActivity).actnoti(docID,type)


    }



}