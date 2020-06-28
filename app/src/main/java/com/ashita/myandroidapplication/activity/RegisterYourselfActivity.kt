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

class RegisterYourselfActivity : AppCompatActivity() {

    lateinit var btnRegister : Button
    lateinit var etName : EditText
    lateinit var etEmailAddress : EditText
    lateinit var etMobileNumber : EditText
    lateinit var etDeliveryAddress : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_yourself)

        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        btnRegister = findViewById(R.id.btnRegister)
        etName = findViewById(R.id.etName)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister.setOnClickListener() {
            var name = etName.text.toString()
            var emailAddress = etEmailAddress.text.toString()
            var mobileNumber = etMobileNumber.text.toString()
            var deliveryAddress = etDeliveryAddress.text.toString()
            var password = etPassword.text.toString()
            var confirmPassword = etConfirmPassword.text.toString()

            if (name != "" && emailAddress != "" && mobileNumber != "" &&
                deliveryAddress != "" && password != "" && confirmPassword != "") {

                if (password != confirmPassword) {
                    Toast.makeText(
                        this@RegisterYourselfActivity,
                        "Password and Confirm Password mismatched!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val queue = Volley.newRequestQueue(this@RegisterYourselfActivity)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", password)
                    jsonParams.put("address", deliveryAddress)
                    jsonParams.put("email", emailAddress)

                    if (ConnectionManager().checkConnectivity(this@RegisterYourselfActivity)) {

                        val jsonObjectRequest = object :
                            JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                                try {

                                    val response = it.getJSONObject("data")
                                    val success = response.getBoolean("success")

                                    if (success) {

                                        Toast.makeText(
                                            this@RegisterYourselfActivity,
                                            "You have been registered successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val user_data = response.getJSONObject("data")

                                        val intent = Intent(
                                            this@RegisterYourselfActivity,
                                            WelcomeActivity::class.java
                                        )

                                        savePreferences(user_data)

                                        startActivity(intent)
                                        finish()

                                    } else {

                                        Toast.makeText(
                                            this@RegisterYourselfActivity,
                                            "Response is $it",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {

                                    Toast.makeText(
                                        this@RegisterYourselfActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }, Response.ErrorListener {

                                Toast.makeText(
                                    this@RegisterYourselfActivity,
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
                    }
                    else{
                        val dialog = AlertDialog.Builder(
                            this@RegisterYourselfActivity
                        )
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings"){text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){text, listener ->
                            ActivityCompat.finishAffinity(this@RegisterYourselfActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }else {
                Toast.makeText(
                    this@RegisterYourselfActivity,
                    "Enter The Details For Registration!",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
