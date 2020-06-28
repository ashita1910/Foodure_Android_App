package com.ashita.myandroidapplication.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.OrderHistoryRecyclerAdapter
import com.ashita.myandroidapplication.model.PreviousOrder
import com.ashita.myandroidapplication.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var orderHistoryAdapter: OrderHistoryRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var rlNoPreviousOrder: RelativeLayout
    lateinit var rlHaveOrders: RelativeLayout
    lateinit var progressLayout: RelativeLayout
    var itemList = arrayListOf<PreviousOrder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity as Context)
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        rlNoPreviousOrder = view.findViewById(R.id.rlNoPreviousOrder)
        rlHaveOrders = view.findViewById(R.id.rlHaveOrders)
        progressLayout = view.findViewById(R.id.progressLayout)

        rlNoPreviousOrder.visibility = View.GONE
        rlHaveOrders.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE

        val userId = sharedPreferences.getString("user_id", "")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val restaurantArray = data.getJSONArray("data")

                            if (restaurantArray.length() == 0) {
                                rlNoPreviousOrder.visibility = View.VISIBLE
                                rlHaveOrders.visibility = View.GONE
                                progressLayout.visibility = View.GONE
                            } else {

                                rlNoPreviousOrder.visibility = View.GONE
                                rlHaveOrders.visibility = View.VISIBLE
                                progressLayout.visibility = View.GONE

                                for (i in 0 until restaurantArray.length()) {
                                    val order = restaurantArray.getJSONObject(i)

                                    val previousOrder = PreviousOrder(
                                        order.getString("order_id"),
                                        order.getString("restaurant_name"),
                                        order.getString("total_cost"),
                                        order.getString("order_placed_at"),
                                        order.getJSONArray("food_items")
                                    )
                                    itemList.add(previousOrder)

                                    orderHistoryAdapter =
                                        OrderHistoryRecyclerAdapter(activity as Context, itemList)
                                    recyclerOrderHistory.adapter = orderHistoryAdapter
                                    recyclerOrderHistory.layoutManager = layoutManager
                                }
                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "1",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "2",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    Response.ErrorListener {
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Volley error occurred!",
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
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("open setting")
            { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("exit")
            { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }

        return view
    }

}
