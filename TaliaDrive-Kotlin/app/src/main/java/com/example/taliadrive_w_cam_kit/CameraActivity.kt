package com.example.taliadrive_w_cam_kit

import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.Toast
import org.json.JSONObject
import top.defaults.camera.*
import java.io.File

import java.lang.Error
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.annotation.Nullable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.*


class CameraActivity : AppCompatActivity() {
    final val ipAddr = "192.168.1.102"

    lateinit var photographer: Photographer
    lateinit var photographerHelper: PhotographerHelper
    lateinit var username: String
    var recordingNow = false

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

        val takePhotoVideoButton = findViewById<ImageButton>(R.id.takePhotoButton)
        takePhotoVideoButton.setOnClickListener {
            if (photoModeActive) photographer.takePicture()
            else {
                if (!recordingNow) {
                    photographer.startRecording(null)
                    recordingNow = true
                } else {
                    photographer.finishRecording()
                    recordingNow = false
                }
            }
        }

        val goToGalleryButton = findViewById<ImageButton>(R.id.galleryButton)
        goToGalleryButton.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java).apply {
            }
            startActivity(intent)
        }

        val changeModeButton = findViewById<ImageButton>(R.id.changeModeButton)
        changeModeButton.setOnClickListener {
            photographerHelper.switchMode()
            photoModeActive = !photoModeActive

            if (photoModeActive) {
                takePhotoVideoButton.setImageResource(R.drawable.ic_camera_button)
                changeModeButton.setImageResource(R.drawable.ic_video_button)
            } else {
                takePhotoVideoButton.setImageResource(R.drawable.ic_video_button)
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
            override fun onFinishRecording(filePath: String) {
                videoEnded(filePath)
            }

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

    val photoJob = Job()
    val photoCoroutineScope = CoroutineScope(Dispatchers.Default + photoJob)

    fun photoTaken(filePath: String) = runBlocking {
        photoCoroutineScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    val dataObject = JSONObject()
                    dataObject.put("Name", "Deniz")

                    //Toast.makeText(this@CameraActivity, "using: " + filePath, Toast.LENGTH_LONG)
                    //  .show()


                    try {
                        val client = HttpClient()

                        val response: HttpResponse = client.submitFormWithBinaryData(
                            url = "http://$ipAddr:8080/uploadImage",
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
                                File(filePath).delete()
                            }
                        }
                        val result = response.receive<String>()

                        println("RESPONSE BURDA: " + result)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                println("AAAAAA")
                e.printStackTrace()
            }
        }
    }

    val videoJob = Job()
    val videoCoroutineScope = CoroutineScope(Dispatchers.Default + videoJob)

    fun videoEnded(filePath: String) = runBlocking {
        videoCoroutineScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    val dataObject = JSONObject()
                    dataObject.put("Name", "Deniz")

                    try {
                        val client = HttpClient()

                        val response: HttpResponse = client.submitFormWithBinaryData(
                            url = "http://$ipAddr:8080/uploadVideo",
                            formData = formData {
                                append("data", dataObject.toString())
                                append("video", File(filePath).readBytes(), Headers.build {
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
                                File(filePath).delete()
                            }
                        }
                        val result = response.receive<String>()

                        println("RESPONSE BURDA: " + result)
                    } catch (e: Exception) {
                        println(e.stackTrace)
                    }
                }
            }
            catch (e: Exception)
            {
                println("AAAAAAAA")
                e.printStackTrace()
            }
        }
    }
}