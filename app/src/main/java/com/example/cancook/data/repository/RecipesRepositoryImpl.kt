package com.example.cancook.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.cancook.data.api.RecipeApi
import com.example.cancook.data.mapper.toDomain
import com.example.cancook.data.paging.RecipesPagingSource
import com.example.cancook.data.paging.SearchPagingSource
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.RecipesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesRepositoryImpl(
    private val api: RecipeApi
) : RecipesRepository {

    override fun getPagedRecipes(pageSize: Int): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { RecipesPagingSource(api, pageSize) }
        ).flow
    }

    override suspend fun getLimitedRandomRecipes(limit: Int): List<Recipe> {
        return try {
            api.getRecipes(limit = limit, skip = 0).recipes
                .map { it.toDomain() }
                .shuffled()
                .take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLimitedPopularRecipes(limit: Int): List<Recipe> {
        return try {
            api.getRecipes(limit = limit, skip = 0).recipes
                .map { it.toDomain() }
                .sortedWith(compareByDescending<Recipe> { it.rating ?: 0.0 }
                    .thenByDescending { it.reviewCount ?: 0 })
                .take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAllRecipes(limit: Int, skip: Int): List<Recipe> {
        return try {
            api.getRecipes(limit = limit, skip = skip).recipes.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun createPagingSource(filter: RecipesPagingSource.FilterType?): RecipesPagingSource {
        return RecipesPagingSource(api, filter = filter)
    }

    override suspend fun fetchMealTypes(limit: Int, skip: Int): List<String> {
        return try {
            val recipes = api.getRecipes(limit = limit, skip = skip).recipes
            recipes.flatMap { it.mealType ?: emptyList() }
                .distinct()
                .sorted()
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    override fun searchRecipes(query: String, pageSize: Int): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchPagingSource(api, query) }
        ).flow
    }

    override suspend fun getMealTypes(): List<String> {
        return try {
            val recipes = api.getRecipes(limit = 100, skip = 0).recipes // adjust limit
            recipes.flatMap { it.mealType ?: emptyList() } // `it` is RecipeDto
                .distinct()
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTags(): List<String> {
        return try {
            val recipes = api.getRecipes(limit = 100, skip = 0).recipes
            recipes.flatMap { it.tags ?: emptyList() }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getDifficulties(): List<String> {
        return try {
            val recipes = api.getRecipes(limit = 100, skip = 0).recipes
            recipes.mapNotNull { it.difficulty }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
