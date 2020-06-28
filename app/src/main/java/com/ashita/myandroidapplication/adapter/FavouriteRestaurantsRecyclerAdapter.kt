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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_favorite_single_row.view.*

class FavouriteRestaurantsRecyclerAdapter(val context: Context, val foodList: List<FoodEntity>): RecyclerView.Adapter<FavouriteRestaurantsRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtRestaurantName: TextView = view.findViewById(R.id.txtFavRestaurantName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFavFoodPrice)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFavFoodRating)
        val txtFavRestaurantFavouriteLogo: TextView = view.findViewById(R.id.txtFavRestaurantFavouriteLogo)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFavFoodImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favorite_single_row, parent, false)

        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val food = foodList[position]

        holder.txtRestaurantName.text = food.foodName
        holder.txtFoodPrice.text = "Rs." + food.foodPrice
        holder.txtFoodRating.text = food.foodRating
        holder.txtFavRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_favourite_fill, 0, 0, 0)
        Picasso.get().load(food.foodImage).error(R.drawable.default_book_cover).into(holder.imgFoodImage)

        holder.llContent.setOnClickListener {

            val intent = Intent(context, AllRestaurantsDetailsActivity::class.java)
            intent.putExtra("restaurant_name", food.foodName)
            intent.putExtra("restaurant_id", food.id.toString())
            intent.putExtra("restaurant_rating", food.foodRating)
            intent.putExtra("restaurant_cost_for_one", food.foodPrice)
            intent.putExtra("restaurant_image_url", food.foodImage)
            context.startActivity(intent)
        }

        holder.txtFavRestaurantFavouriteLogo.setOnClickListener(){

            if (!FavouriteRestaurantsRecyclerAdapter.DBAsyncTask(context, food, 1).execute()
                    .get()
            ) {
                val async =
                    FavouriteRestaurantsRecyclerAdapter.DBAsyncTask(context, food, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.txtFavRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_favourite_fill, 0, 0, 0)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else {

                val async =
                    FavouriteRestaurantsRecyclerAdapter.DBAsyncTask(context, food, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.txtFavRestaurantFavouriteLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_action_favourite, 0, 0, 0)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

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
}