package com.ashita.myandroidapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.ashita.myandroidapplication.R
import kotlinx.android.synthetic.main.activity_welcome.*

class ConfirmOrderActivity : AppCompatActivity() {

    lateinit var btnConfirmClick: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirm)

         btnConfirmClick = findViewById(R.id.btnConfirmClick)

        btnConfirmClick.setOnClickListener(){

            val intent = Intent(this@ConfirmOrderActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        return
    }

}
