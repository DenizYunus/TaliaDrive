package com.example.taliadrive_w_cam_kit


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.NetworkSecurityPolicy
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File

//const val EXTRA_MESSAGE = "com.example.taliadrive.MESSAGE"

class MainActivity : AppCompatActivity() {
    //final val ipAddr = "192.168.1.102" //benim pc wifi
    final val ipAddr = "18.116.82.71" //sunucu aws windows

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
        private const val RECORD_AUDIO_PERMISSION_CODE = 102
    }

    var cameraPerm = false
    var storagePerm = false
    var micPerm = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Toast.makeText(this@MainActivity, NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted.toString() ,Toast.LENGTH_LONG).show()
        var et_user_name = findViewById(R.id.et_user_name) as EditText
        var et_password = findViewById(R.id.et_password) as EditText
        var btn_register = findViewById(R.id.btn_register) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        btn_register.setOnClickListener {
            val user_name = et_user_name.text.toString();
            val password = et_password.text.toString();

            if (registerUsernamePassword(user_name, password))
            {
                findViewById<TextView>(R.id.errorMessage).setTextColor(Color.green(255))
                findViewById<TextView>(R.id.errorMessage).text = "Signup Successful. You can login now."
            }
        }

        btn_submit.setOnClickListener {
            val user_name = et_user_name.text.toString();
            val password = et_password.text.toString();

            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
            checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE)

            if (cameraPerm && storagePerm && micPerm) {
                if (checkUsernamePassword(user_name, password)) {
                    Toast.makeText(
                        this@MainActivity,
                        "Entered with: " + user_name,
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, CameraActivity::class.java)
                    val extras = Bundle()
                    extras.putString("username", user_name.toString())
                    intent.putExtras(extras)

                    startActivity(intent)
                } else {
                    findViewById<TextView>(R.id.errorMessage).setTextColor(Color.RED)
                    findViewById<TextView>(R.id.errorMessage).text = "Login Failed."
                }
            } else
            {
                findViewById<TextView>(R.id.errorMessage).setTextColor(Color.RED)
                findViewById<TextView>(R.id.errorMessage).text = "Permissions Not Granted."
            }
        }
    }

    fun checkUsernamePassword(_username:String, _password:String):Boolean = runBlocking {
        val client = HttpClient()

        Log.d("DEBUGINFO", _username)
        Log.d("DEBUGINFO", _password)
        val response: HttpResponse = client.submitFormWithBinaryData (
            url = "http://$ipAddr:8080/login",
            formData = formData {
                append("username", _username)
                append("password", _password)
            }
        ) {
            onUpload { bytesSentTotal, contentLength ->

            }
        }
        val result = response.receive<String>()

        return@runBlocking result.equals("success")
    }

    fun registerUsernamePassword(_username:String, _password:String):Boolean = runBlocking {
        val client = HttpClient()

        val response: HttpResponse = client.submitFormWithBinaryData (
            url = "http://$ipAddr:8080/signup",
            formData = formData {
                append("username", _username)
                append("password", _password)
            }
        ) {
            onUpload { bytesSentTotal, contentLength ->

            }
        }
        val result = response.receive<String>()

        return@runBlocking result.equals("success")
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            when (permission) {
                Manifest.permission.CAMERA -> cameraPerm = true
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> storagePerm = true
                Manifest.permission.RECORD_AUDIO -> micPerm = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPerm = true
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storagePerm = true
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                micPerm = true
            } else {
                Toast.makeText(this@MainActivity, "Microphone Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}