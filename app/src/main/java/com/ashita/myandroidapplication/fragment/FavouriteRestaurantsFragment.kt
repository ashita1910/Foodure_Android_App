package com.ashita.myandroidapplication.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.FavouriteRestaurantsRecyclerAdapter
import com.ashita.myandroidapplication.database.FoodDatabase
import com.ashita.myandroidapplication.database.FoodEntity

class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavourite : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouriteRestaurantsRecyclerAdapter
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar
    lateinit var rlFavContent: RelativeLayout
    lateinit var rlNoFavContent: RelativeLayout

    var dbFoodList = listOf<FoodEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        rlFavContent = view.findViewById(R.id.rlFavContent)
        rlNoFavContent = view.findViewById(R.id.rlNoFavContent)

        progressLayout.visibility = View.VISIBLE
        rlNoFavContent.visibility = View.GONE
        rlFavContent.visibility = View.GONE

        layoutManager = LinearLayoutManager(activity)

        dbFoodList = RetrieveFavourites(activity as Context).execute().get()

        if(activity != null) {

            if (dbFoodList.isNotEmpty()) {

                progressLayout.visibility = View.GONE
                rlFavContent.visibility = View.VISIBLE
                rlNoFavContent.visibility = View.GONE

                recyclerAdapter =
                    FavouriteRestaurantsRecyclerAdapter(activity as Context, dbFoodList)
                recyclerFavourite.adapter = recyclerAdapter
                recyclerFavourite.layoutManager = layoutManager
            }else{

                progressLayout.visibility = View.GONE
                rlFavContent.visibility = View.GONE
                rlNoFavContent.visibility = View.VISIBLE
            }
        }

        return view
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<FoodEntity>>(){

        override fun doInBackground(vararg params: Void?): List<FoodEntity>? {

            val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

            return db.foodDao().getAllFoodRestaurants()
        }

    }

}
