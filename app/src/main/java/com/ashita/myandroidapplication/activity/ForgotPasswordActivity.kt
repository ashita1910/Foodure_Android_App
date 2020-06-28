package com.ashita.myandroidapplication.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etRegisteredEmailAddress : EditText
    lateinit var etRegisteredMobileNumber : EditText
    lateinit var btnNext : Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        sharedPreferences = getSharedPreferences("preference_file_name", Context.MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        etRegisteredEmailAddress = findViewById(R.id.etRegisteredEmailAddress)
        etRegisteredMobileNumber = findViewById(R.id.etRegisteredMobileNumber)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener(){
            var registeredEmailAddress = etRegisteredEmailAddress.text.toString()
            var registeredMobileNumber = etRegisteredMobileNumber.text.toString()

            if(registeredEmailAddress != "" && registeredMobileNumber != "") {

                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", registeredMobileNumber)
                jsonParams.put("email", registeredEmailAddress)

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                    val jsonObjectRequest = object :
                        JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {

                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if (success) {

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit().putString("Mobile Number", registeredMobileNumber).apply()

                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                } else {

                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Response is $it",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {

                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Some unexpected error occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Volley error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content_type"] = "application_json"
                            headers["token"] = "5bc626be3b1740"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(
                        this@ForgotPasswordActivity
                    )
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            else{
                Toast.makeText(this@ForgotPasswordActivity, "Enter the correct details!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
