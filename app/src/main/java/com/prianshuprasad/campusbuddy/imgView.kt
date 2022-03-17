package com.prianshuprasad.campusbuddy

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


lateinit var imageView: ImageView
lateinit var scaleGestureDetector: ScaleGestureDetector
private var mScaleFactor = 1.0f

class imgView : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_view)



        supportActionBar?.hide()


        val url = intent.getStringExtra("imgurl")!!

        imageView = findViewById(R.id.idIVImage);
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())


   val x  =    Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).into(imageView);







    }


    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        // inside on touch event method we are calling on
        // touch event method and pasing our motion event to it.

        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }


     class ScaleListener : SimpleOnScaleGestureListener() {

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {


            // inside on scale method we are setting scale
            // for our image in our image view.
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))

            // on below line we are setting
            // scale x and scale y to our image view.
            imageView.setScaleX(mScaleFactor)
            imageView.setScaleY(mScaleFactor)
            return true
        }
    }








}