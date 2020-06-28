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
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var btnSubmit: Button
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sharedPreferences = getSharedPreferences("preference_file_name", Context.MODE_PRIVATE)

        val mobileNumber = sharedPreferences.getString("Mobile Number", "0123456789")

        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etOTP = findViewById(R.id.etOTP)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener() {
            var OTP = etOTP.text.toString()
            var password = etNewPassword.text.toString()
            var confirmPassword = etConfirmPassword.text.toString()

            if (OTP != "" && password != null && confirmPassword != "") {
                if (password != confirmPassword) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Password and Confirm Password mismatched!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", password)
                    jsonParams.put("otp", OTP)

                    if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {

                        val jsonObjectRequest = object :
                            JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                                try {

                                    val response = it.getJSONObject("data")
                                    val success = response.getBoolean("success")

                                    if (success) {

                                        Toast.makeText(this@ResetPasswordActivity,
                                            response.getString("successMessage"), Toast.LENGTH_SHORT
                                        ).show()

                                        val intent = Intent(this@ResetPasswordActivity, LogInActivity::class.java)
                                        sharedPreferences.edit().clear().apply()
                                        startActivity(intent)
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            "Response is $it",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {

                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
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
                            this@ResetPasswordActivity
                        )
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            } else {
                android.widget.Toast.makeText(
                    this@ResetPasswordActivity,
                    "Enter the correct details!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
