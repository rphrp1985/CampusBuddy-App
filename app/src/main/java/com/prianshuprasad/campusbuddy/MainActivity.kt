package com.prianshuprasad.campusbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.prianshuprasad.campusbuddy.databinding.ActivityMainBinding
import java.security.Provider

class MainActivity : AppCompatActivity() {


    var uid =""

    private lateinit var binding: ActivityMainBinding


//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

 supportActionBar?.hide()



        val serintent= Intent(this, notificationServices::class.java)

        startService(serintent)



//        val intent = Intent(this,imgView::class.java)
//        startActivity(intent)


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }




    fun onpostclicked(docID: String, whatis:String){

//        Toast.makeText(this,"$docID   $whatis",Toast.LENGTH_LONG).show();

        if(whatis=="Post"){
            val intent = Intent(this,postView::class.java)
            intent.putExtra("docID",docID);
            startActivity(intent)
        }
        if(whatis=="Comment"){

            val intent = Intent(this,commentView::class.java)
            intent.putExtra("docID",docID);
            intent.putExtra("whatis","Post")
            startActivity(intent)


        }



    }

    fun onrcview2clicked(docID:String,whatis:String){


if(whatis=="BOOK"){

    val intent = Intent(this,bookView::class.java);

    intent.putExtra("docID",docID);
    startActivity(intent)

}


        if(whatis=="imgurl")
        {
            val intent = Intent(this,imgView::class.java);

            intent.putExtra("imgurl",docID);
            startActivity(intent)
        }

//        Toast.makeText(this,"doc id $docID",Toast.LENGTH_LONG).show()

    }


fun addpost(){

    if(uid!="") {


        val intent = Intent(this, addPost::class.java)
        startActivity(intent);
    }else
        makeToast("Please Login First!")


}


    fun addbook(){


        if(uid!="") {

            val intent = Intent(this, postBook::class.java)
            startActivity(intent);
        }else
            makeToast("Please Login First!")

    }


    fun makeToast(str:String)
    {
        Toast.makeText(this,"$str",Toast.LENGTH_LONG).show()
    }


    fun actnoti(docID: String,type:String) {

        var whatis = ""
        if (type == "Comment") {
            whatis = "Post"
            val intent = Intent(this, commentView::class.java)
            intent.putExtra("docID", docID)

            if (docID.substring(0, 4) == "BOOK") {
                whatis = "Book"

            }

            intent.putExtra("whatis", whatis)
            startActivity(intent)
            return
        }

        if (type == "Like") {

            val intent = Intent(this, postView::class.java)
            intent.putExtra("docID", docID)
            startActivity(intent)
            return


        }

        if (type == "Post") {

            val intent = Intent(this, postView::class.java)
            intent.putExtra("docID", docID)
            startActivity(intent)
            return


        }

        if(type=="Book") {
            val intent = Intent(this, bookView::class.java)
            intent.putExtra("docID", docID)
            startActivity(intent)
            return

        }


    }

    fun opensearch(){

        val intent = Intent(this,searchView::class.java)
        startActivity(intent)

    }







}