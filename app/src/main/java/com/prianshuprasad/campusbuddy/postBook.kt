package com.prianshuprasad.campusbuddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.KeyEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.io.Files
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_post_book.*


class postBook : AppCompatActivity() {

    var imageuri: Uri? = null

    var fileuri: Uri? = null
    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()
    var imgurl=""
    lateinit var filelocation: TextView;
    lateinit var removeSelection: ImageView
    lateinit var attachFile: ImageView
    var uid = ""
    var username = ""

    private lateinit var mAdapter6: rcviewadapter6

    private lateinit var mAdapter7: rcviewadapter7
    val mAdapter6arr:ArrayList<datamap> = ArrayList()

    lateinit var rcview7: RecyclerView

    var IsattachVisible=1



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_book)


        if(Firebase.auth.currentUser!=null) {
            uid = Firebase.auth.currentUser!!.uid
            username = Firebase.auth.currentUser!!.displayName.toString()
        }

        supportActionBar?.hide()

        filelocation = findViewById(R.id.filelocation)
        removeSelection = findViewById(R.id.removeSelection)
        attachFile = findViewById(R.id.attachfile)


        closebutton.setOnClickListener {
            finish()
        }

     attachfile.setOnClickListener {


         val galleryIntent = Intent()
         galleryIntent.action = Intent.ACTION_GET_CONTENT

         // We will be redirected to choose pdf

         // We will be redirected to choose pdf

         galleryIntent.type = "application/pdf"
         startActivityForResult(galleryIntent, 1)

     }

        uploadbutton.setOnClickListener {


            if( description.text.toString() =="" || bookname.text.toString()=="" || fileuri==null || imageuri==null ){

                Toast.makeText(this,"Please fill required fields!",Toast.LENGTH_LONG).show()

            }else
            {

                val serintent= Intent(this, bookService::class.java)

                serintent.putExtra("uid",uid);
                serintent.putExtra("ownerName",username)
                serintent.putExtra("fileuri",fileuri.toString())
                serintent.putExtra("bookName",bookname.text.toString())
                serintent.putExtra("description",description.text.toString())

                    serintent.putExtra("bookLogo",imageuri.toString());


                val TagN:Int= mAdapter6arr.size;

                serintent.putExtra("TagN",TagN)


                var i=1;

                while(i<TagN){
                    serintent.putExtra("Tag-${i-1}","${mAdapter6arr[i].map["textview4"]}")

               i++;

                }

                startService(serintent)



                   finish()

            }




        }

        removeSelection.setOnClickListener {
            fileuri= null
            attachFile.visibility = View.VISIBLE
            removeSelection.visibility = View.GONE
            filelocation.visibility = View.GONE


        }


        val rcview6: RecyclerView = findViewById(R.id.rcview6)
        rcview6.layoutManager = LinearLayoutManager(rcview6.context,
            LinearLayoutManager.HORIZONTAL,false)


        mAdapter6= rcviewadapter6(this)
        rcview6.adapter=mAdapter6



        mAdapter6arr.add(datamap(mutableMapOf<String,Any?>("ADD" to 1)))
        mAdapter6.update(mAdapter6arr);




         rcview7= findViewById(R.id.rcview7)
        rcview7.layoutManager = LinearLayoutManager(rcview6.context,
            LinearLayoutManager.VERTICAL,false)


        mAdapter7= rcviewadapter7(this)
        rcview7.adapter=mAdapter7



        booklogo.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)

        }





    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Toast.makeText(this,"$RC_SIGN_IN",Toast.LENGTH_LONG).show()

        if (requestCode ==1 && resultCode == RESULT_OK && data != null) {
            fileuri = data.data


            Toast.makeText(this,"File Selected",Toast.LENGTH_LONG).show()

            attachFile.visibility = View.GONE
            removeSelection.visibility = View.VISIBLE
            filelocation.visibility = View.VISIBLE
            filelocation.text = getName(fileuri!!)

        }

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){


            imageuri = data.data
            booklogo.setImageURI(imageuri)



        }


    }

    fun getName(uri: Uri): String {
        val returnCursor = contentResolver.query(uri !!, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val fileName = returnCursor?.getString(nameIndex!!)
        returnCursor?.close()
        return fileName.toString()
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



    fun edittextListener(str:String){


        if(str=="")
        {
            rcview7.visibility= View.GONE
        }else
            rcview7.visibility = View.VISIBLE



        val mAdapter7arr:ArrayList<datamap> = ArrayList()
        val datamap:datamap = datamap(mutableMapOf<String,Any?>("Tag" to str))
        mAdapter7arr.add(datamap)
        mAdapter7.update(mAdapter7arr)



        val db = FirebaseFirestore.getInstance()
        var collection = db.collection("Search")

        var n = str.length
        var i=0;


//        Toast.makeText(this,"$n   $str",Toast.LENGTH_LONG).show()

        while(i<n){

            val doc = collection.document("${str[i].toLowerCase()}");

            if(i>=n-1) {
                collection = doc.collection("DATA")
                break;
            }

            collection = doc.collection("${str[i].toLowerCase()}");
            i++;
        }




        collection.get().addOnCompleteListener(
            OnCompleteListener<QuerySnapshot?> { task ->
                if (task.isSuccessful) {

                    for (document in task.result!!) {

                        val clouddata: clouddata = document.toObject(clouddata::class.java)


//                        Toast.makeText(this,"${clouddata.datastr} ",Toast.LENGTH_LONG).show()

                        val datamap:datamap = datamap(mutableMapOf<String,Any?>("Tag" to clouddata.datastr))


                        mAdapter7arr.add(datamap)

                        mAdapter7.update(mAdapter7arr)

                    }


                } else {



                }
            })


 mAdapter7.update(mAdapter7arr)



    }



    fun onSuggestionSelected(str:String){

        val db = FirebaseFirestore.getInstance()
        var collection = db.collection("Search")

        var n = str.length
        var i=0;

        mAdapter6arr.add(datamap(mutableMapOf<String,Any?>("textview4" to  str )))

        mAdapter6.update(mAdapter6arr)

        while(i<n){

            val doc = collection.document("${str[i].toLowerCase()}");

            collection =  doc.collection("DATA");


            collection.document(str).set(clouddata(str));

            collection = doc.collection("${str[i].toLowerCase()}");


            i++;
        }




    }

    fun onRemoveTag(position:Int){

        mAdapter6arr.remove(mAdapter6arr[position])
        mAdapter6.update(mAdapter6arr)


    }







}