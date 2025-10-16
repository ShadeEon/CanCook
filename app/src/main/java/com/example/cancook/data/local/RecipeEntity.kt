package com.example.cancook.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L, // local primary key
    @ColumnInfo(name = "remote_id") val remoteId: Int? = null, // if it came from API
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "ingredients") val ingredients: List<String>,
    @ColumnInfo(name = "instructions") val instructions: List<String>,
    @ColumnInfo(name = "prep_time_minutes") val prepTimeMinutes: Int?,
    @ColumnInfo(name = "cook_time_minutes") val cookTimeMinutes: Int?,
    @ColumnInfo(name = "servings") val servings: Int?,
    @ColumnInfo(name = "difficulty") val difficulty: String?,
    @ColumnInfo(name = "cuisine") val cuisine: String?,
    @ColumnInfo(name = "calories_per_serving") val caloriesPerServing: Int?,
    @ColumnInfo(name = "tags") val tags: List<String>,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "rating") val rating: Float?,
    @ColumnInfo(name = "review_count") val reviewCount: Int?,
    @ColumnInfo(name = "meal_type") val mealType: List<String>,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)