package com.example.cancook.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val description: String? = null,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int?,
    val cookTimeMinutes: Int?,
    val servings: Int?,
    val difficulty: String?,
    val cuisine: String?,
    val caloriesPerServing: Int?,
    val tags: List<String>,
    val imageUrl: String?,
    val rating: Double?,
    val reviewCount: Int?,
    val mealType: List<String>
): Parcelable
