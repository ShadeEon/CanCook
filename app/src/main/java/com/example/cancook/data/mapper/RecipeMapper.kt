package com.example.cancook.data.mapper

import com.example.cancook.data.model.RecipeDto
import com.example.cancook.domain.model.Recipe

fun RecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    name = name,
    ingredients = ingredients ?: emptyList(),
    instructions = instructions ?: emptyList(),
    prepTimeMinutes = prepTimeMinutes,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    difficulty = difficulty,
    cuisine = cuisine,
    caloriesPerServing = caloriesPerServing,
    tags = tags ?: emptyList(),
    imageUrl = image,
    reviewCount = reviewCount,
    rating = rating,
    mealType = mealType ?: emptyList()
)
