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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.util.ConnectionManager
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {

    lateinit var etMobileNumber : EditText
    lateinit var etPassword : EditText
    lateinit var txtRegisterYourself : TextView
    lateinit var txtForgotPassword : TextView
    lateinit var btnLogIn : Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if(isLoggedIn){
            val intent = Intent(this@LogInActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        btnLogIn = findViewById(R.id.btnLogIn)

        btnLogIn.setOnClickListener(){

            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            if(mobileNumber != "" && password != "") {

                val queue = Volley.newRequestQueue(this@LogInActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result/"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)

                if (ConnectionManager().checkConnectivity(this@LogInActivity)) {

                    val jsonRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if (success) {

                                    val user_data = response.getJSONObject("data")

                                    savePreferences(user_data)

                                    val intent =
                                        Intent(this@LogInActivity, WelcomeActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this@LogInActivity,
                                        "Response is $it",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LogInActivity,
                                    "Some error occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, Response.ErrorListener {

                            Toast.makeText(
                                this@LogInActivity,
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
                    queue.add(jsonRequest)
                } else {
                    val dialog = AlertDialog.Builder(
                        this@LogInActivity
                    )
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@LogInActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }else{
                Toast.makeText(
                    this@LogInActivity,
                    "Enter the Details for Logging In!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        txtForgotPassword.setOnClickListener(){
            val intent = Intent(this@LogInActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtRegisterYourself.setOnClickListener(){
            val intent = Intent(this@LogInActivity, RegisterYourselfActivity::class.java)
            startActivity(intent)
        }

    }

    fun savePreferences(user_data: JSONObject){
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("user_id", user_data.getString("user_id")).apply()
        sharedPreferences.edit().putString("Name", user_data.getString("name")).apply()
        sharedPreferences.edit().putString("Email Address", user_data.getString("email")).apply()
        sharedPreferences.edit().putString("Mobile Number", user_data.getString("mobile_number")).apply()
        sharedPreferences.edit().putString("Delivery Address", user_data.getString("address")).apply()
    }

}
