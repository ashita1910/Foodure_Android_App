package com.ashita.myandroidapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {

    @Insert
    fun insertFoodRestaurant(foodEntity: FoodEntity)

    @Delete
    fun deleteFoodRestaurant(foodEntity: FoodEntity)

    @Query("SELECT * FROM food")
    fun getAllFoodRestaurants(): List<FoodEntity>

    @Query("SELECT * FROM food WHERE id = :id")
    fun getFoodRestaurantByName(id: String): FoodEntity
}