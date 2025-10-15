package com.example.cancook.domain.repository

import androidx.paging.PagingData
import com.example.cancook.data.paging.RecipesPagingSource
import com.example.cancook.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getPagedRecipes(pageSize: Int = 10): Flow<PagingData<Recipe>>
    suspend fun getAllRecipes(limit: Int = 100, skip: Int = 0): List<Recipe>
    suspend fun fetchMealTypes(limit: Int = 100, skip: Int = 0): List<String>
    suspend fun getLimitedRandomRecipes(limit: Int): List<Recipe>
    suspend fun getLimitedPopularRecipes(limit: Int): List<Recipe>
    fun createPagingSource(filter: RecipesPagingSource.FilterType?): RecipesPagingSource
    fun searchRecipes(query: String, pageSize: Int = 10): Flow<PagingData<Recipe>>
}