package com.example.taliadrive_w_cam_kit

import android.app.ActionBar
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.*
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

    final val ipAddr = "192.168.1.102"

    var widX = 200
    var widY = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var displayMetrics = DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        widX = displayMetrics.widthPixels / 4
        widY = widX

        getGallery()

        addItem("https://www.pixsy.com/wp-content/uploads/2021/04/ben-sweet-2LowviVHZ-E-unsplash-1.jpeg")
        addItem("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")
        addItem("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")

    }

    fun addItem(_link: String) {
        val dynamicButton = ImageButton(this)
        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(
            widX,
            widY
        )
        Picasso.get().load(_link).fit().centerCrop().into(dynamicButton)

        dynamicButton.setBackgroundColor(Color.GREEN)
        // add Button to LinearLayout
        gridLayout.addView(dynamicButton)
    }

    fun getGallery() = runBlocking {
        try {
            val dataObject = JSONObject()
            dataObject.put("Name", "Deniz")

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

                val jsonArray = jsonObject.getJSONArray("data")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            println("AAAAAA")
            e.printStackTrace()
        }
    }
}