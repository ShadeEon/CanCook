package com.example.cancook.data.model

data class RecipesResponseDto(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val recipes: List<RecipeDto>
)