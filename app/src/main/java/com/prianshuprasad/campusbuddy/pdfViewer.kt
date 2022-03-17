package com.prianshuprasad.campusbuddy

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.prianshuprasad.campusbuddy.databinding.ActivityMainBinding
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.util.FileUtil
import java.io.File


class pdfViewer : AppCompatActivity(), DownloadFile.Listener {

    lateinit var remotePDFViewPager: RemotePDFViewPager

    lateinit var pdfPagerAdapter: PDFPagerAdapter

    lateinit var url: String

    lateinit var progressBar: ProgressBar

    lateinit var pdfLayout: LinearLayout




    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        supportActionBar?.hide()


        progressBar = findViewById(R.id.progressBar)
        progressBar.setVisibility(View.VISIBLE)

        //initialize the pdfLayout

        //initialize the pdfLayout
        pdfLayout = findViewById(R.id.pdf_layout)

        url = intent.getStringExtra("fileurl")!!
//        url = "file:///mnt/sdcard/aa.pdf"
        //Create a RemotePDFViewPager object

        //Create a RemotePDFViewPager object
        remotePDFViewPager = RemotePDFViewPager(this, url, this)






    }

    override fun onSuccess(url: String?, destinationPath: String?) {


        pdfPagerAdapter = PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url))
        remotePDFViewPager.adapter = pdfPagerAdapter
        updateLayout()
        progressBar.visibility = View.GONE



    }

    override fun onFailure(e: Exception?) {






    }

    override fun onProgressUpdate(progress: Int, total: Int) {

    }

    private fun updateLayout() {
        pdfLayout.addView(remotePDFViewPager,
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (pdfPagerAdapter != null) {
            pdfPagerAdapter.close()
        }
    }









}