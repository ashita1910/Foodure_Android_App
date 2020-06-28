package com.ashita.myandroidapplication.model

import org.json.JSONArray

data class PreviousOrder(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val date: String,
    val foodItems: JSONArray
)