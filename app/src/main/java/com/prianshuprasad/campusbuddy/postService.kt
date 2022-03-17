package com.prianshuprasad.campusbuddy

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.common.io.Files
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class postService() : Service() {

    var datai:datamap = datamap(mutableMapOf<String,Any?>())
var uid: String= ""
    var imgurl:String=""
    var j=0;

    var docid =" "

    var arr:ArrayList<datamap> = ArrayList()

    var n=0
    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()


    //notification
    private lateinit var notificationManager: NotificationManagerCompat
    var channelId = "Progress Notification" as String


    //Sets the maximum progress as 100
    val progressMax = 100
    //Creating a notification and setting its various attributes
   lateinit var notification : NotificationCompat.Builder
    var progress = 0







    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        datai = NoteData().toDataMap(intent.getStringExtra("datamap")!! )

        uid= intent.getStringExtra("uid")!!



n=  intent.getIntExtra("n",0)

        genDocid()

        channelId = docid
        createNotificationChannel()
      docid = datai.map["DOCUMENTID"].toString()

        notificationManager = NotificationManagerCompat.from(this)
        startNotification()


        if(n==0)
        {
            uploadtext()
        }



            var i=0;
        while(i<n){

            val uri = "uri-$i"
            val urii = intent.getStringExtra(uri)!!.toUri()

            uploadToFirebase(urii)


   i++;


        }






        return START_STICKY
    }


    override fun onDestroy() {




        onDestroy()




        super.onDestroy()


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }





    private fun uploadToFirebase(uri: Uri) {

        val fileRef: StorageReference =
            reference.child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(uri.toString()))
        fileRef.putFile(uri)
            .addOnSuccessListener( object : OnSuccessListener<UploadTask.TaskSnapshot?> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->

                        imgurl = uri.toString()

                        arr.add(datamap(mutableMapOf("imgview4" to imgurl )))

                        onimageupload()

                    }
                }




            }).addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
                override  fun onProgress(snapshot: UploadTask.TaskSnapshot) {





                }
            }).addOnFailureListener {

                notification.setContentText("Failed")
                    .setSmallIcon(R.drawable.ic_baseline_error_outline_24)
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(1, notification.build())



                onDestroy()


//                Toast.makeText(this, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }


    fun onimageupload(){
        val db = FirebaseFirestore.getInstance()


        j++;

        if(j==n)
        {

            datai.map["rcview2"] = " "




            val db= FirebaseFirestore.getInstance()
            val collection= db.collection("Post").document("rcview2").collection(docid);


            var n = arr.size
            var k =0;

            while(k<n){

                collection.document().set(clouddata( NoteData().toString(arr[k])));

                k++;
            }

    uploadtext()


        }


    }


    fun genDocid(){

        val x= (0 .. 100000000000000000).random()
        val y= System.nanoTime()


         docid=  "$uid-${x.toString()}-${y.toString()}"

        datai.map["DOCUMENTID"]= docid



    }

    fun uploadtext(){



        val db = FirebaseFirestore.getInstance()

        val tempdatamap:datamap = datamap(mutableMapOf<String,Any?>())
        tempdatamap.map["DOCUMENTID"]= docid
        tempdatamap.map["Type"]= "Post"
        db.collection("User-Data").document(uid).collection(uid).document().set(clouddata2(NoteData().toString(tempdatamap),System.nanoTime()))

        datai.map["textview1"]= Firebase.auth.currentUser!!.displayName.toString()
        datai.map["imgview1"]  = Firebase.auth.currentUser!!.photoUrl.toString()
        datai.map["uid"] = Firebase.auth.currentUser!!.uid
        datai.map["time"] = System.currentTimeMillis().toString().toLong()




        val wordlist: ArrayList<String> = NoteData().wordCutter(datai.map["textview2"].toString())

        j=wordlist.size

        datai.map["TagN"] = j;


        var i=0;

        while(i<j){

            datai.map["Tag-$i"] = wordlist[i].toString()


            i++
        }


        val clouddata:clouddata = clouddata(  NoteData().toString(datai) );


        val collection3 = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");

        db.collection("Post").document("Post").collection("ALL").document(docid).set(clouddata)

        i=0;
        while(i<j){

            val wrd= wordlist[i]

            optimizeSearch().postNow(clouddata,wrd,docid)

            db.collection("Post").document("Post").collection(wrd).document(docid).set(clouddata)

            collection3.document(wrd).set(clouddata)
            collection3.document(wrd).collection(wrd).document().set(clouddata("Category"))





            i++;

            destroy(i);

        }

//        SystemClock.sleep(
//            2000
//        )


        notification.setContentText("Uploaded")
            .setSmallIcon(R.drawable.ic_baseline_cloud_done_24)
            .setProgress(0, 0, false)
            .setOngoing(false)

        notificationManager.notify(1, notification.build())


}


    fun destroy(i:Int){

        if(i==j)
        {


            notification.setContentText("Suceessfully Posted")
                .setSmallIcon(R.drawable.ic_baseline_cloud_done_24)
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, notification.build())


//            onDestroy()
        }

    }



    public fun startNotification(){

        val intent = Intent(this, MainActivity::class.java).apply{
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, 0)

        //Sets the maximum progress as 100

        //Creating a notification and setting its various attributes
         notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                .setContentTitle("Campus Buddy")
                .setContentText("Posting")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        //Initial Alert
        notificationManager.notify(1, notification.build())





    }








    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelId
            val descriptionText = "RPPPPPP"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }










}
