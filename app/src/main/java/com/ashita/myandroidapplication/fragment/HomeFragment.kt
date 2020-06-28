package com.ashita.myandroidapplication.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.HomeRecyclerAdapter
import com.ashita.myandroidapplication.database.FoodDatabase
import com.ashita.myandroidapplication.database.FoodEntity
import com.ashita.myandroidapplication.model.Food
import com.ashita.myandroidapplication.util.ConnectionManager
import kotlinx.android.synthetic.main.recycler_home_single_row.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var previousMenuItem: MenuItem? = null
    private var checkedItem: Int = -1

    val foodList = arrayListOf<Food>()

    private val costComparator = Comparator<Food> { food1, food2 ->
        if (food1.foodCostForOne.compareTo(food2.foodCostForOne, true) == 0) {
            nameComparator.compare(food1, food2)
        } else {
            food1.foodCostForOne.compareTo(food2.foodCostForOne, true)
        }
    }

    private val ratingComparator = Comparator<Food> { food1, food2 ->
        if (food1.foodRating.compareTo(food2.foodRating, true) == 0) {
            nameComparator.compare(food1, food2)
        } else {
            food1.foodRating.compareTo(food2.foodRating, true)
        }
    }

    private val nameComparator = Comparator<Food> { food1, food2 ->
        food1.foodName.compareTo(food2.foodName, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {

                        progressLayout.visibility = View.GONE

                        val information = it.getJSONObject("data")

                        val success = information.getBoolean("success")

                        if (success) {
                            val data = information.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = Food(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )

                                foodList.add(foodObject)

                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, foodList)

                                recyclerHome.adapter = recyclerAdapter

                                recyclerHome.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setCancelable(false)
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.sortAction) {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By-")
            dialog.setCancelable(false)
            val options = arrayOf(
                getString(R.string.cost_low_to_high),
                getString(R.string.cost_high_to_low),
                getString(R.string.cost_rating),
                getString(R.string.cost_name)
            )

            dialog.setSingleChoiceItems(options, checkedItem) { _, which ->

                checkedItem = which
            }

            dialog.setPositiveButton("Ok") { _, _ ->
                when (checkedItem) {
                    0 -> {
                        Collections.sort(foodList, costComparator)
                    }
                    1 -> {
                        Collections.sort(foodList, costComparator)
                        foodList.reverse()
                    }
                    2 -> {
                        Collections.sort(foodList, ratingComparator)
                        foodList.reverse()
                    }
                    3 -> {
                        Collections.sort(foodList, nameComparator)
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Cancel") { _, _ ->
                //Do Nothing
            }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}