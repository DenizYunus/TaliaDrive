package com.example.taliadrive_w_cam_kit

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import top.defaults.camera.*
import java.io.File

import java.lang.Error
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*



class CameraActivity : AppCompatActivity() {
    lateinit var photographer:Photographer
    lateinit var photographerHelper:PhotographerHelper
    lateinit var username:String

    var photoModeActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = intent.getStringExtra("com.example.taliadrive.MESSAGE").toString()
        setContentView(R.layout.activity_camera)

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }

        val takePhotoButton = findViewById<ImageButton>(R.id.takePhotoButton)
        takePhotoButton.setOnClickListener { photographer.takePicture() }

        val changeModeButton = findViewById<ImageButton>(R.id.changeModeButton)
        changeModeButton.setOnClickListener {
            photographerHelper.switchMode()
            photoModeActive = !photoModeActive

            if (photoModeActive)
            {
                takePhotoButton.setImageResource(R.drawable.ic_camera_button)
                changeModeButton.setImageResource(R.drawable.ic_video_button)
            } else {
                takePhotoButton.setImageResource(R.drawable.ic_video_button)
                changeModeButton.setImageResource(R.drawable.ic_camera_button)
            }
        }

        val preview = findViewById<CameraView>(R.id.preview)
        photographer = PhotographerFactory.createPhotographerWithCamera2(this, preview)

        photographer.setOnEventListener(object : SimpleOnEventListener() {
            override fun onDeviceConfigured() {}
            override fun onPreviewStarted() {}
            override fun onZoomChanged(zoom: Float) {}
            override fun onPreviewStopped() {}
            override fun onStartRecording() {}
            override fun onFinishRecording(filePath: String) {}
            override fun onShotFinished(filePath: String) {
                photoTaken(filePath)
            }
            fun onError(error: Error?) {}
        })
        photographerHelper = PhotographerHelper(photographer);
        val path = this.getExternalFilesDir(null)
        photographerHelper.setFileDir(path?.absolutePath.plus("/photos"))
    }

    override fun onResume() {
        super.onResume()
        photographer.startPreview()
    }

    override fun onPause() {
        photographer.stopPreview()
        super.onPause()
    }

    fun photoTaken(filePath: String) = runBlocking{
        val dataObject = JSONObject()
        dataObject.put("Name", "Deniz")

        Toast.makeText(this@CameraActivity, "using: " + filePath, Toast.LENGTH_LONG).show()


        try {
            val client = HttpClient()

            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "http://192.168.1.102:8080/uploadImage",
                formData = formData {
                    append("data", dataObject.toString())
                    append("image", File(filePath).readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=".plus(File(filePath).name)
                        )
                    })
                }
            ) {
                onUpload { bytesSentTotal, contentLength ->
                    println("Sent $bytesSentTotal bytes from $contentLength")
                }
            }
            val result = response.receive<String>()

            println("RESPONSE BURDA: " + result)
        } catch (e:Exception) {
            println(e.stackTrace)
        }
        /*val response: HttpResponse = client.submitForm(
            url = "http://192.168.1.102:8080/uploadImage",
            formParameters = Parameters.build {
                append("data", dataObject.toString())
                append("myFile", File(filePath).readBytes().decodeToString())
            },
            encodeInQuery = true
        )*/


        /*var multipart: MultipartUtility =
            MultipartUtility("http://192.168.1.102:8080/uploadImage", "UTF-8")

        multipart.addFormField("Name", "Deniz")

        //multipart.addFilePart("myFile", File(filePath))

        val response = multipart.finish()

        Toast.makeText(this@CameraActivity, response[0], Toast.LENGTH_LONG).show()*/

        /*
        Fuel.upload("http://192.168.1.102:8080/uploadImage", method = Method.POST)
            .add(
                FileDataPart(File(filePath), name = "myFile", filename="contents.json"),
                InlineDataPart(dataObject.toString(), name="data")
            )
            .response { result ->
                Toast.makeText(this@CameraActivity, result.toString(), Toast.LENGTH_LONG).show()
            }*/
    }
}