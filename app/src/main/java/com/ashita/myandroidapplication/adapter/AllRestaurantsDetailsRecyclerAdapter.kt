package com.ashita.myandroidapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.model.FoodDetails

class AllRestaurantsDetailsRecyclerAdapter(
    val context: Context, private val foodDetailsList: List<FoodDetails>,
    private val listener: OnItemClickListener): RecyclerView.Adapter<AllRestaurantsDetailsRecyclerAdapter.DetailsViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    class DetailsViewHolder(view:View): RecyclerView.ViewHolder(view){

        val txtFoodId: TextView = view.findViewById(R.id.txtFoodId)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_all_restaurants_details_single_row, parent, false)

        return DetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodDetailsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: FoodDetails)
        fun onRemoveItemClick(foodItem: FoodDetails)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        val foodDetails = foodDetailsList[position]

        holder.txtFoodId.text = "${position + 1}"
        holder.txtFoodName.text = foodDetails.foodName
        holder.txtFoodPrice.text = "Rs." + foodDetails.foodPrice.toString()

        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(foodDetails)
        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(foodDetails)
        }
    }
}