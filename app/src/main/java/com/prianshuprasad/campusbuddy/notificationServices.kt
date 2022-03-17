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
import kotlin.concurrent.thread

class notificationServices() : Service() {



    private val reference: StorageReference = FirebaseStorage.getInstance().getReference()

    var uid = ""

val db = FirebaseFirestore.getInstance()
    //notification
    private lateinit var notificationManager: NotificationManagerCompat
    var channelId = "Progress Notification" as String

    var notitext=""

    lateinit var notification : NotificationCompat.Builder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {






        channelId = "${(0..20000000).random()}"
        createNotificationChannel()

        notificationManager = NotificationManagerCompat.from(this)
        startNotification()

        thread {

            while (true) {

                if (Firebase.auth.currentUser != null)
                    uid = Firebase.auth.currentUser!!.uid



                if (uid != "") {

                    val collection = db.collection("User-Data").document(uid).collection(uid);

                    collection.get().addOnCompleteListener {

                        if (!it.getResult().isEmpty) {


                            for (document in it.result!!) {



                                var count: Long = "0".toLong()
                                val clouddata2: clouddata2 =
                                    document.toObject(clouddata2::class.java)

                                val data: datamap = NoteData().toDataMap(clouddata2.datastr)

                                if(data.map["Type"]=="Notification")
                                    continue


                                var docid = ""

                                if (data.map.containsKey("DOCUMENTID")) {

                                    docid = data.map["DOCUMENTID"].toString()

                                }
                                if (docid != "") {

                                    val doc2 = db.collection("Comments").document("Comments")
                                        .collection(docid)

                                    doc2.get().addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            count = it.getResult().size().toString().toLong()



                                            if (data.map.containsKey("count")) {

                                                val tempcount = data.map["count"].toString().toLong()

                                                if (tempcount != count) {


                                                    data.map["count"] = count;

                                                    db.collection("User-Data").document(uid)
                                                        .collection(uid).document(docid)
                                                        .set(clouddata2(NoteData().toString(data),
                                                            clouddata2.time))

                                                }

                                                if (count > tempcount) {

                                                    val newdata: datamap =
                                                        datamap(mutableMapOf<String, Any?>())


                                                    newdata.map["DOCUMENTID"] = docid;

                                                    if (docid.substring(0, 4) == "BOOK") {

                                                        newdata.map["Type"] = "NewFeedback"
                                                        notitext = "Check out these new Feedbacks"

                                                    } else {
                                                        newdata.map["Type"] = "NewComment"
                                                        notitext = "check out new comments"

                                                    }


                                                    val notidoc =
                                                        db.collection("User-Data").document(uid).collection(uid).document("Notification")
                                                            .get()



                                                    notidoc.addOnCompleteListener {

                                                        if (it.getResult().exists()) {
                                                            val clouddata2: clouddata2 =
                                                                it.getResult()!!.toObject(
                                                                    com.prianshuprasad.campusbuddy.clouddata2::class.java)!!
                                                            val datamap: datamap =
                                                                NoteData().toDataMap(clouddata2.datastr)

                                                            if (datamap.map.containsKey("whatis")) {

                                                                if (datamap.map["whatis"].toString() == "1") {

                                                                    startNotification()


                                                                }


                                                            }

                                                        }

                                                    }


                                                    db.collection("User-Data").document(uid)
                                                        .collection(uid).document(docid)
                                                        .set(clouddata2(NoteData().toString(newdata),
                                                            System.nanoTime()))


                                                }


                                            } else {
                                                data.map["count"] = count as Long;

                                                db.collection("User-Data").document(uid)
                                                    .collection(uid).document(docid).set(clouddata2(
                                                    NoteData().toString(data),
                                                    clouddata2.time))

                                            }


                                        }


                                    }


                                }


                            }


                        }


                    }

                }


                SystemClock.sleep(60000)

            }

        }


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }






//    fun destroy(i:Int){
//
//        if(i==j)
//        {
//
//
//
//            notification.setContentText("Uploaded")
//                .setSmallIcon(R.drawable.ic_baseline_cloud_done_24)
//                .setProgress(0, 0, false)
//                .setOngoing(false)
//
//            notificationManager.notify(1, notification.build())
//
//
//            onDestroy()
//        }
//
//    }



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
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("$notitext")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        //Initial Alert
        notificationManager.notify((0..1000000).random(), notification.build())





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
