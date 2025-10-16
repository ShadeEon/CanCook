package com.example.cancook.domain.repository

import com.example.cancook.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface LocalRecipesRepository {
    suspend fun addLocalRecipe(recipe: Recipe): Long
    fun getAllLocalRecipesFlow(): Flow<List<Recipe>>
    suspend fun getAllLocalRecipes(limit: Int = 100, offset: Int = 0): List<Recipe>
    fun getFavoriteRecipesFlow(): Flow<List<Recipe>>
    suspend fun toggleFavorite(localId: Long): Boolean // returns new favorite state
    suspend fun getRecipeByLocalId(localId: Long): Recipe?
    suspend fun deleteLocalRecipe(localId: Long)
}