package com.example.cancook.data.api

import com.example.cancook.data.model.RecipesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {
    @GET("recipes")
    suspend fun getRecipes(
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0
    ): RecipesResponseDto

    @GET("recipes/search")
    suspend fun searchRecipes(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0
    ): RecipesResponseDto
}