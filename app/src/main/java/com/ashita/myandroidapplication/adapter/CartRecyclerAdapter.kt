package com.ashita.myandroidapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.model.FoodDetails

class CartRecyclerAdapter(val context: Context, val itemList: List<FoodDetails>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart =itemList[position]

        holder.txtFoodName.text = cart.foodName
        holder.txtFoodPrice.text = "Rs. " + cart.foodPrice
    }
}