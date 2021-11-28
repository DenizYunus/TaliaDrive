package com.example.taliadrive_w_cam_kit

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.activity_image_popup.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ImagePopup : AppCompatActivity() {
    lateinit var rootRL : RelativeLayout
    lateinit var imageBitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_popup)
        val intentExtras = getIntent().extras
        val imageLink = intentExtras?.getString("image_link").toString()

        rootRL = findViewById<RelativeLayout>(R.id.rootRL)

        val shareButton = findViewById<ImageButton>(R.id.shareButton)
        shareButton.setOnClickListener {
            val bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap,"title", null);
            val bitmapUri = Uri.parse(bitmapPath);

            val intent = Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(Intent.createChooser(intent, "Share"));
        }

        setBitmapFromURL(imageLink)
        findViewById<Button>(R.id.backButton).setOnClickListener{
            finish()
        }
    }

    fun setBitmapFromURL(imageUrl: String?) {
        try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            imageBitmap = BitmapFactory.decodeStream(input)

            rootRL.background = BitmapDrawable(imageBitmap)
        } catch (e: IOException) {
            Log.d("ErrorLog", "Couldn't Set Bitmap From URL: ".plus(imageUrl))
        }
    }
}