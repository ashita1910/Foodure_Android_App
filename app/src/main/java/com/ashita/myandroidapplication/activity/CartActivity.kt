package com.ashita.myandroidapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.CartRecyclerAdapter
import com.ashita.myandroidapplication.database.FoodDatabase
import com.ashita.myandroidapplication.database.OrderEntity
import com.ashita.myandroidapplication.model.FoodDetails
import com.ashita.myandroidapplication.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartRecyclerAdapter: CartRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    val orderList = arrayListOf<FoodDetails>()
    var restaurantName: String ?= null
    var restaurantId: String ?= null
    var userId: String ?= null
    lateinit var txtHeaderRestaurantName: TextView
    lateinit var btnPaceOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        userId = sharedPreferences.getString("user_id", "User Id")

        recyclerCart = findViewById(R.id.recyclerCart)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        layoutManager = LinearLayoutManager(this@CartActivity)
        toolbar = findViewById(R.id.toolbar)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        txtHeaderRestaurantName = findViewById(R.id.txtHeaderRestaurantName)
        btnPaceOrder = findViewById(R.id.btnPlaceOrder)

        setUpToolbar()

        if(intent != null){
            val bundle = intent.getBundleExtra("data")
            restaurantName = bundle?.getString("restaurant_name")
            restaurantId = bundle?.getString("restaurant_id")
        }else{
            finish()
            Toast.makeText(
                this@CartActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if(restaurantId == null || restaurantName == null){
            finish()
            Toast.makeText(
                this@CartActivity,
                "Some error occurred!!",
                Toast.LENGTH_SHORT
            ).show()
        }

        txtHeaderRestaurantName.text = "Ordering From: " + restaurantName

        val dbList = GetOrderDetailsAsyncTask(this@CartActivity).execute().get()

        for(data in dbList){
            orderList.addAll(Gson().fromJson(data.foodItems, Array<FoodDetails>::class.java).asList())
        }

        cartRecyclerAdapter = CartRecyclerAdapter(this@CartActivity, orderList)
        recyclerCart.adapter = cartRecyclerAdapter
        recyclerCart.layoutManager = layoutManager

        var totalCost = 0

        for(cost in 0 until orderList.size){
            totalCost += orderList[cost].foodPrice.toInt()
        }

        btnPaceOrder.text = "Place Order(Total: Rs. $totalCost)"

        btnPaceOrder.setOnClickListener() {
            progressLayout.visibility = View.GONE

            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val jsonParams = JSONObject()
                jsonParams.put("user_id", userId)
                jsonParams.put("restaurant_id", restaurantId)
                jsonParams.put("total_cost", totalCost.toString())

                val food = JSONArray()

                for(i in  0 until orderList.size){
                    val foodId = JSONObject()
                    foodId.put("food_item_id", orderList[i].foodId)
                    food.put(i, foodId)
                }

                jsonParams.put("food", food)

            if (ConnectionManager().checkConnectivity(this@CartActivity)) {

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {
                                progressLayout.visibility = View.GONE
                                ClearCart(this@CartActivity, restaurantId.toString()).execute().get()

                                val intent =
                                    Intent(this@CartActivity, ConfirmOrderActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CartActivity,
                                    "Some Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@CartActivity,
                                "Some Unexpected Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@CartActivity,
                            "Volley error occurred!!!",
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
                val dialog = android.app.AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setCancelable(false)
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    class GetOrderDetailsAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<OrderEntity>>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "orders-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity>? {
            return db.orderDao().getAllOrders()
        }
    }

    class ClearCart(val context: Context, val restaurantId: String) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, FoodDatabase::class.java, "orders-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(restaurantId)
            db.close()
            return true
        }

    }

    override fun onBackPressed() {
        ClearCart(this@CartActivity, restaurantId.toString()).execute().get()
        super.onBackPressed()
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}