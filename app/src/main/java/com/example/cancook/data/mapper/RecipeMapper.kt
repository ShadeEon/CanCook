package com.example.cancook.data.mapper

import com.example.cancook.data.model.RecipeDto
import com.example.cancook.data.local.RecipeEntity
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

fun Recipe.toEntity(remoteId: Int? = null) = RecipeEntity(
    remoteId = remoteId,
    name = this.name,
    description = this.description,
    ingredients = this.ingredients,
    instructions = this.instructions,
    prepTimeMinutes = this.prepTimeMinutes,
    cookTimeMinutes = this.cookTimeMinutes,
    servings = this.servings,
    difficulty = this.difficulty,
    cuisine = this.cuisine,
    caloriesPerServing = this.caloriesPerServing,
    tags = this.tags,
    imageUrl = this.imageUrl,
    rating = this.rating,
    reviewCount = this.reviewCount,
    mealType = this.mealType,
    isFavorite = false
)

fun RecipeEntity.toDomain() = Recipe(
    id = (remoteId ?: localId.toInt()),
    name = name,
    ingredients = ingredients,
    instructions = instructions,
    prepTimeMinutes = prepTimeMinutes,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    difficulty = difficulty,
    cuisine = cuisine,
    caloriesPerServing = caloriesPerServing,
    tags = tags,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    mealType = mealType
)