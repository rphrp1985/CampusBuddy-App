package com.prianshuprasad.campusbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
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

class bookService() : Service() {

    var datamap:datamap = datamap(mutableMapOf<String,Any?>())
    var uid: String= ""
    var imgurl:String=""
    var fileurl:String=""
    var ownername:String=""
    var bookname:String=""
    var description: String =""

    var docid =" "
    var j=0;
    var k=0;

    var TagN=0;
    val TagList:ArrayList<String> = ArrayList()



    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()



    //notification
    private lateinit var notificationManager: NotificationManagerCompat
    var channelId = "Progress Notification" as String


    //Sets the maximum progress as 100
    val progressMax = 100
    //Creating a notification and setting its various attributes
    lateinit var notification : NotificationCompat.Builder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {




        if(Firebase.auth.currentUser!=null)
            uid= Firebase.auth.currentUser!!.uid


        uid= intent.getStringExtra("uid")!!
        imgurl = intent.getStringExtra("bookLogo")!!



        fileurl = intent.getStringExtra("fileuri")!!
         ownername = intent.getStringExtra("ownerName")!!
        bookname =intent.getStringExtra("bookName")!!
         description= intent.getStringExtra("description")!!


         TagN = intent.getIntExtra("TagN",-1)!!

        datamap.map["TagN"] = TagN-1;

        var i=1;

        while(i<TagN){
           TagList.add( intent.getStringExtra("Tag-${i-1}")!! )

            datamap.map["Tag-${i-1}"] = TagList[i-1];


            i++;

        }

        datamap.map["uid"] = uid
   datamap.map["ownername"] = ownername
        datamap.map["bookname"] = bookname;
        datamap.map["description"] = description
        datamap.map["uid"] = Firebase.auth.currentUser!!.uid.toString()


        genDocid()

        docid = datamap.map["DOCUMENTID"].toString()

channelId = docid
        createNotificationChannel()

        notificationManager = NotificationManagerCompat.from(this)
        startNotification()



            uploadToFirebase(imgurl.toUri(),1)

         uploadToFirebase(fileurl.toUri(),2)


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }





    private fun uploadToFirebase(uri: Uri, x:Int) {

        val fileRef: StorageReference =
            reference.child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(uri.toString()))
        fileRef.putFile(uri)
            .addOnSuccessListener( object : OnSuccessListener<UploadTask.TaskSnapshot?> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->

                        if(x==1)
                        datamap.map["booklogo"] = uri.toString()

                        if(x==2)
                            datamap.map["fileurl"] = uri.toString()


                           uploadtext()

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



//                onDestroy()

            }
    }



    private fun getFileExtension(mUri: Uri): String? {

        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }




    fun genDocid(){

        val x= (0 .. 100000000000000000).random()
        val y= System.nanoTime()


        docid=  "BOOK-$uid-${x.toString()}-${y.toString()}"

        datamap.map["DOCUMENTID"]= docid




    }

    fun uploadtext(){


            if(!datamap.map.containsKey("booklogo"))
            return;





        if(!datamap.map.containsKey("fileurl"))
            return;


        val db = FirebaseFirestore.getInstance()

        val tempdatamap:datamap = datamap(mutableMapOf<String,Any?>())

        tempdatamap.map["DOCUMENTID"]= docid
      tempdatamap.map["Type"]= "Book"
        db.collection("User-Data").document(uid).collection(uid).document().set(clouddata2(NoteData().toString(tempdatamap),System.nanoTime()))


        val clouddata:clouddata = clouddata(  NoteData().toString(datamap) );


        db.collection("Book").document("Book").collection("ALL").document(docid).set(clouddata)


        var i = 1;

        val collection3 = db.collection("Global-Intereset").document("Global-Intereset").collection("Data");


        while(i<TagN)
        {

            optimizeSearch().postNow(clouddata,TagList[i-1],docid)

            db.collection("Book").document("Book").collection(TagList[i-1]).document(docid).set(clouddata)

            collection3.document(TagList[i-1]).set(clouddata)
         collection3.document(TagList[i-1]).collection(TagList[i-1]).document().set(clouddata("Category"))



        i++;
        }





        notification.setContentText("Uploaded")
            .setSmallIcon(R.drawable.ic_baseline_cloud_done_24)
            .setProgress(0, 0, false)
            .setOngoing(false)

        notificationManager.notify(1, notification.build())



    }


    fun destroy(i:Int){

        if(i==j)
        {



            notification.setContentText("Uploaded")
                .setSmallIcon(R.drawable.ic_baseline_cloud_done_24)
                .setProgress(0, 0, false)
                .setOngoing(false)

            notificationManager.notify(1, notification.build())


            onDestroy()
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
                .setContentText("Uploading")
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
