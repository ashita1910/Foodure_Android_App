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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.AllRestaurantsDetailsRecyclerAdapter
import com.ashita.myandroidapplication.database.FoodDatabase
import com.ashita.myandroidapplication.database.FoodEntity
import com.ashita.myandroidapplication.database.OrderEntity
import com.ashita.myandroidapplication.model.FoodDetails
import com.ashita.myandroidapplication.util.ConnectionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_all_restaurants_details.*
import org.json.JSONException

class AllRestaurantsDetailsActivity : AppCompatActivity() {

    lateinit var recyclerAllDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: AllRestaurantsDetailsRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var btnProceedToCart: Button
    val foodDetailsList = arrayListOf<FoodDetails>()
    val orderList = arrayListOf<FoodDetails>()
    lateinit var restaurantName: String
    lateinit var restaurantId: String
    lateinit var restaurantRating: String
    lateinit var restaurantCostForOne: String
    lateinit var restaurantImageUrl: String
    lateinit var imgLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_restaurants_details)

        recyclerAllDetails = findViewById(R.id.recyclerAllDetails)

        imgLogo = findViewById(R.id.imgLogo)

        layoutManager = LinearLayoutManager(this@AllRestaurantsDetailsActivity)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        toolbar = findViewById(R.id.toolbar)

        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        progressLayout.visibility = View.VISIBLE

        progressBar.visibility = View.VISIBLE

        btnProceedToCart.visibility = View.GONE

        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurant_name")
            restaurantId = intent.getStringExtra("restaurant_id")
            restaurantRating = intent.getStringExtra("restaurant_rating")
            restaurantCostForOne = intent.getStringExtra("restaurant_cost_for_one")
            restaurantImageUrl = intent.getStringExtra("restaurant_image_url")
        } else {
            finish()
            Toast.makeText(
                this@AllRestaurantsDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val async = GetAllFavAsyncTask(this).execute()
        val result = async.get()

        if (restaurantId in result) {
            imgLogo.setImageResource(R.drawable.ic_action_favourite_fill)
        } else {
            imgLogo.setImageResource(R.drawable.ic_action_favourite)
        }

        val foodEntity = FoodEntity(
            restaurantId?.toInt(),
            restaurantName,
            restaurantRating,
            restaurantCostForOne,
            restaurantImageUrl
        )

        val checkFav = DBAsyncTask(this@AllRestaurantsDetailsActivity, foodEntity, 1).execute()
        val isFav = checkFav.get()


        if (isFav) {
            val image = R.drawable.ic_action_favourite_fill
            imgLogo.setImageResource(image)
        } else {
            val image = R.drawable.ic_action_favourite
            imgLogo.setImageResource(image)
        }

        imgLogo.setOnClickListener() {

            if (!DBAsyncTask(this@AllRestaurantsDetailsActivity, foodEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(this@AllRestaurantsDetailsActivity, foodEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        this@AllRestaurantsDetailsActivity,
                        "Restaurant added to favourites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    imgLogo.setImageResource(R.drawable.ic_action_favourite_fill)
                } else {
                    Toast.makeText(
                        this@AllRestaurantsDetailsActivity,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async =
                    DBAsyncTask(this@AllRestaurantsDetailsActivity, foodEntity, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        this@AllRestaurantsDetailsActivity,
                        "Restaurant removed from favourites!",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgLogo.setImageResource(R.drawable.ic_action_favourite)

                } else {
                    Toast.makeText(
                        this@AllRestaurantsDetailsActivity,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }


        btnProceedToCart.setOnClickListener(){
            proceedToCart()
        }

        val queue = Volley.newRequestQueue(this@AllRestaurantsDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this@AllRestaurantsDetailsActivity)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {

                        val information = it.getJSONObject("data")

                        val success = information.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE

                            val data = information.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = FoodDetails(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one")
                                )
                                foodDetailsList.add(foodObject)

                                recyclerAdapter =
                                    AllRestaurantsDetailsRecyclerAdapter(this@AllRestaurantsDetailsActivity,
                                        foodDetailsList,
                                        object :
                                            AllRestaurantsDetailsRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(foodItem: FoodDetails) {
                                                orderList.add(foodItem)
                                                if (orderList.size > 0) {
                                                    btnProceedToCart.visibility = View.VISIBLE
                                                    AllRestaurantsDetailsRecyclerAdapter.isCartEmpty =
                                                        false
                                                }
                                            }

                                            override fun onRemoveItemClick(foodItem: FoodDetails) {
                                                orderList.remove(foodItem)
                                                if (orderList.isEmpty()) {
                                                    btnProceedToCart.visibility = View.GONE
                                                    AllRestaurantsDetailsRecyclerAdapter.isCartEmpty =
                                                        true
                                                }
                                            }
                                        })

                                recyclerAllDetails.adapter = recyclerAdapter

                                recyclerAllDetails.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this@AllRestaurantsDetailsActivity,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@AllRestaurantsDetailsActivity,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@AllRestaurantsDetailsActivity,
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
            val dialog = android.app.AlertDialog.Builder(this@AllRestaurantsDetailsActivity)
            dialog.setTitle("Error")
            dialog.setCancelable(false)
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@AllRestaurantsDetailsActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun proceedToCart() {

        /*Here we see the implementation of Gson.
        * Whenever we want to convert the custom data types into simple data types
        * which can be transferred across for utility purposes, we will use Gson*/
        val gson = Gson()

        /*With the below code, we convert the list of order items into simple string which can be easily stored in DB*/
        val orderItems = gson.toJson(orderList)

        val async = ItemsOfCart(
            this@AllRestaurantsDetailsActivity,
            restaurantId.toString(),
            orderItems,
            1
        ).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putString("restaurant_name", restaurantName)
            data.putString("restaurant_id", restaurantId)
            val intent = Intent(this@AllRestaurantsDetailsActivity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText(this@AllRestaurantsDetailsActivity, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    class ItemsOfCart(
        val context: Context,
        val restaurantId: String,
        val orderItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "orders-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, orderItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, orderItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }

   class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.foodDao().getAllFoodRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){

                1 -> {

                    val food: FoodEntity? = db.foodDao().getFoodRestaurantByName(foodEntity.id.toString())
                    db.close()
                    return food != null
                }

                2 -> {

                    db.foodDao().insertFoodRestaurant(foodEntity)
                    db.close()
                    return true
                }

                3 -> {

                    db.foodDao().deleteFoodRestaurant(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    override fun onBackPressed() {

        if (orderList.size > 0) {

            val dialog = AlertDialog.Builder(this@AllRestaurantsDetailsActivity)
            dialog.setTitle("Are you sure?")
            dialog.setMessage("All items in cart will be reset!")
            dialog.setPositiveButton("Yes")
            { text, listener ->
                AllRestaurantsDetailsRecyclerAdapter.isCartEmpty = true
                super.onBackPressed()
            }
            dialog.setNegativeButton("No")
            { text, listener ->
                text.cancel()
            }
            dialog.create()
            dialog.show()
        }else{

            return super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}