package com.example.taliadrive_w_cam_kit

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.DisplayMetrics
import android.util.Log
import android.util.Log.INFO
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File

class GalleryActivity : AppCompatActivity() {

    //final val ipAddr = "192.168.1.102" //benim pc wifi
    final val ipAddr = "18.116.82.71" //sunucu aws windows

    var widX = 200
    var widY = 200

    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentExtras = getIntent().extras
        username = intentExtras?.getString("username").toString()

        setContentView(R.layout.activity_gallery)

        var displayMetrics = DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        widX = displayMetrics.widthPixels / 4
        widY = widX

        getGallery()

        /*addItem("https://www.pixsy.com/wp-content/uploads/2021/04/ben-sweet-2LowviVHZ-E-unsplash-1.jpeg")
        addItem("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")
        addItem("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")*/

    }

    fun addItem(_bmpLink: String, _imageLink: String) {
        val dynamicButton = ImageButton(this)

        dynamicButton.setOnClickListener {/*
            Log.d("infodebug", "Clicked: ".plus(_bmpLink))
            val imageView = ImageView(this)
            // setting height and width of imageview
            imageView.layoutParams = LinearLayout.LayoutParams(400, 400)
            imageView.x = 20F //setting margin from left
            imageView.y = 20F //setting margin from top
            Picasso.get().load(_imageLink).fit().centerCrop().into(imageView)
            val layout = findViewById<ConstraintLayout>(R.id.fullLayout)
            layout?.addView(imageView)*/

            val intent = Intent(this, ImagePopup::class.java)
            val extras = Bundle()
            extras.putString("image_link", _imageLink)
            intent.putExtras(extras)
            startActivity(intent)
        }

        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(
            widX,
            widY
        )

        Picasso.get().load(_bmpLink).fit().centerCrop().into(dynamicButton)

        dynamicButton.setBackgroundColor(Color.GREEN)
        // add Button to LinearLayout
        gridLayout.addView(dynamicButton)
    }

    fun getGallery() = runBlocking {
        try {
            val dataObject = JSONObject()
            dataObject.put("Name", username)

            try {
                val client = HttpClient()

                val response: HttpResponse = client.submitFormWithBinaryData(
                    url = "http://$ipAddr:8080/getGallery",
                    formData = formData {
                        append("data", dataObject.toString())
                    }
                ) {
                    onUpload { bytesSentTotal, contentLength ->
                        println("Sent $bytesSentTotal bytes from $contentLength")
                    }
                }
                val result = response.receive<String>()

                val jsonObject = JSONTokener(result).nextValue() as JSONObject

                val jsonArray = jsonObject.getJSONArray("Images")

                for (i in 0 until jsonArray.length()) {
                    val fileName = jsonArray.getJSONObject(i).getString("Filename")
                    val fileType = jsonArray.getJSONObject(i).getString("Filetype")
                    if (fileType == "video")
                        continue  //TODO: ADD VIDEO SETTINGS TOO
                    val bitmapFileName = jsonArray.getJSONObject(i).getString("BitmapFilename")
                    //Log.d("infodebug", "http://$ipAddr/".plus(bitmapFileName))
                    addItem("http://$ipAddr/".plus(bitmapFileName), "http://$ipAddr/".plus(fileName))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}