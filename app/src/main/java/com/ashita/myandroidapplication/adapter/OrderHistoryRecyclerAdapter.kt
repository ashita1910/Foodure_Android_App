package com.ashita.myandroidapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.model.FoodDetails
import com.ashita.myandroidapplication.model.PreviousOrder
import kotlinx.android.synthetic.main.recycler_order_history_single_row.view.*

class OrderHistoryRecyclerAdapter(val context: Context, val itemList: ArrayList<PreviousOrder>): RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtNameOfRestaurant: TextView = view.findViewById(R.id.txtNameOfRestaurant)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerListOfOrder: RecyclerView = view.findViewById(R.id.recyclerListOfOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = itemList[position]
        holder.txtNameOfRestaurant.text = order.restaurantName
        holder.txtDate.text = order.date.subSequence(0, 8)
        setUpList(holder.recyclerListOfOrder, order)
    }

    fun setUpList(recyclerListOfOrder: RecyclerView, order: PreviousOrder) {
        val foodList = ArrayList<FoodDetails>()

        for(i in 0 until order.foodItems.length()){
            val foodItems = order.foodItems.getJSONObject(i)
            foodList.add(
                FoodDetails(foodItems.getString("food_item_id"),
                            foodItems.getString("name"),
                            foodItems.getString("cost"))
            )

            recyclerListOfOrder.layoutManager = LinearLayoutManager(context)
            recyclerListOfOrder.adapter = CartRecyclerAdapter(context, foodList)
        }
    }
}