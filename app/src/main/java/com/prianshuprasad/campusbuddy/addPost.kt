package com.prianshuprasad.campusbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.io.Files
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_post.*

class addPost : AppCompatActivity() {


    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()
    private var imageUri: Uri? = null
var imgurl:String=""
    private lateinit var mAdapter3: rcviewadapter3
    val imagearr: ArrayList<datamap> = ArrayList()


var uid=""

    var CHANNEL_ID = "RP";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        supportActionBar?.hide()


        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid


        createNotificationChannel()

        val rcview3= findViewById<RecyclerView>(R.id.rcview3)

        rcview3.layoutManager = LinearLayoutManager(rcview3.context,
            LinearLayoutManager.HORIZONTAL,false)


        mAdapter3= rcviewadapter3(this)
        rcview3.adapter=mAdapter3

        mAdapter3.update(imagearr);



        addimagefromgallery.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)

        })


        closebutton.setOnClickListener {
            finish()
        }





    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Toast.makeText(this,"$RC_SIGN_IN",Toast.LENGTH_LONG).show()
        val tempmapi: MutableMap<String,Any?> = mutableMapOf<String,Any?>()
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            tempmapi["imgview4"] = imageUri

        imagearr.add( datamap(tempmapi) );

//            imageUri?.let { uploadToFirebase(it) }

//            addimagefromcamera.setImageURI(imageUri)
            mAdapter3.update(imagearr);

        }
    }


    private fun uploadToFirebase(uri: Uri) {
        val fileRef: StorageReference =
            reference.child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(uri.toString()))
        fileRef.putFile(uri)
            .addOnSuccessListener( object : OnSuccessListener<UploadTask.TaskSnapshot?> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->

                        imgurl = uri.toString()
                        }
                }




            }).addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
                override  fun onProgress(snapshot: UploadTask.TaskSnapshot) {

                  //on progess



                }
            }).addOnFailureListener {

//                Toast.makeText(this, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getFileExtension(mUri: Uri): String? {
            val cr = contentResolver
            val mime = MimeTypeMap.getSingleton()
            return mime.getExtensionFromMimeType(cr.getType(mUri))
        }






    fun deleteImage(index:Int){

        imagearr.remove(imagearr[index]);

        mAdapter3.update(imagearr);

    }

    fun post(view: View) {



        val tempmap: MutableMap<String,Any?> = mutableMapOf<String,Any?>()

//        tempmap["imgview1"]="";
        tempmap["textview2"]= posttext.text.toString()

        if(posttext.text.toString()=="")
        {
            Toast.makeText(this,"Please write your doubt",Toast.LENGTH_LONG).show()

            return
        }

        Toast.makeText(this,"Posting",Toast.LENGTH_LONG).show()



        val serintent= Intent(this, postService::class.java)
        serintent.putExtra("uid",uid);
        serintent.putExtra( "datamap", "${NoteData().toString(datamap(tempmap))}" )

        var i=0;

        val n = imagearr.size

        serintent.putExtra("n",n)

        while(i<n){

            val uri = "uri-$i"
            val value = imagearr[i].map["imgview4"]
            serintent.putExtra(uri.toString(),"$value");
            i++;

        }



        startService(serintent)

  finish()
    }






    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "RPPPPPP"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }








}