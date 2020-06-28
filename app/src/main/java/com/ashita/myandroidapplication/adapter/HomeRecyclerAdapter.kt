package com.ashita.myandroidapplication.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.activity.AllRestaurantsDetailsActivity
import com.ashita.myandroidapplication.database.FoodDatabase
import com.ashita.myandroidapplication.database.FoodEntity
import com.ashita.myandroidapplication.model.Food
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Food>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFoodRating)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtRestaurantFavouriteLogo: TextView = view.findViewById(R.id.txtRestaurantFavouriteLogo)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = itemList[position]
        holder.txtRestaurantName.text = food.foodName
        holder.txtFoodRating.text = food.foodRating
        holder.txtFoodPrice.text = "Rs." + food.foodCostForOne
        Picasso.get().load(food.foodImageUrl).error(R.drawable.default_book_cover).into(holder.imgFoodImage)

        var foodList = GetAllFavAsyncTask(context).execute().get()

        if(foodList.isNotEmpty() && foodList.contains(food.id.toString())){
            holder.txtRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_favourite_fill, 0, 0, 0)
        }
        else{
            holder.txtRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_favourite, 0, 0, 0)
        }

        holder.txtRestaurantFavouriteLogo.setOnClickListener() {

            val foodEntity = FoodEntity(
                food.id.toInt(),
                food.foodName,
                food.foodRating,
                food.foodCostForOne,
                food.foodImageUrl
            )

            if (!DBAsyncTask(context, foodEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.txtRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_action_favourite_fill,
                        0,
                        0,
                        0
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async =
                    DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.txtRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_action_favourite,
                        0,
                        0,
                        0
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }

        holder.llContent.setOnClickListener {

            val intent = Intent(context, AllRestaurantsDetailsActivity::class.java)
            intent.putExtra("restaurant_name", food.foodName)
            intent.putExtra("restaurant_id", food.id)
            intent.putExtra("restaurant_rating", food.foodRating)
            intent.putExtra("restaurant_cost_for_one", food.foodCostForOne)
            intent.putExtra("restaurant_image_url", food.foodImageUrl)
            context.startActivity(intent)
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

}