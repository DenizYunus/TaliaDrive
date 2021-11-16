package com.example.taliadrive_w_cam_kit


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.NetworkSecurityPolicy
import android.view.View
import android.widget.*

//const val EXTRA_MESSAGE = "com.example.taliadrive.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Toast.makeText(this@MainActivity, NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted.toString() ,Toast.LENGTH_LONG).show()
        var et_user_name = findViewById(R.id.et_user_name) as EditText
        var et_password = findViewById(R.id.et_password) as EditText
        var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        btn_reset.setOnClickListener {
            //et_user_name.setText("")
            et_password.setText("")
        }

        btn_submit.setOnClickListener {
            val user_name = et_user_name.text;
            val password = et_password.text;
            Toast.makeText(this@MainActivity, "Entered with: " + user_name, Toast.LENGTH_LONG).show()


            val intent = Intent(this, CameraActivity::class.java)
            val extras = Bundle()
            extras.putString("username", user_name.toString())
            intent.putExtras(extras)
            //intent.putExtra("username", user_name)

            startActivity(intent)
        }
    }
}