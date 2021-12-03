package com.example.taliadrive_w_cam_kit

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView

class VideoPopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_popup)

        val intentExtras = getIntent().extras
        val videoLink = intentExtras?.getString("video_link").toString()

        val videoViewer = findViewById<VideoView>(R.id.videoView)
        //videoViewer.setVideoPath(videoLink)
        videoViewer.setVideoURI(Uri.parse(videoLink))
        videoViewer.start()
    }
}