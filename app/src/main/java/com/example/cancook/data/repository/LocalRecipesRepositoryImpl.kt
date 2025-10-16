package com.example.cancook.data.repository

import com.example.cancook.data.local.RecipeDao
import com.example.cancook.data.local.RecipeEntity
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.LocalRecipesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRecipesRepositoryImpl(
    private val dao: RecipeDao
) : LocalRecipesRepository {

    override suspend fun addLocalRecipe(recipe: Recipe): Long {
        val entity = recipe.toEntity(remoteId = null)
        return dao.insertRecipe(entity)
    }

    override fun getAllLocalRecipesFlow(): Flow<List<Recipe>> {
        return dao.getAllLocalRecipesFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAllLocalRecipes(limit: Int, offset: Int): List<Recipe> {
        return dao.getAllLocalRecipes(limit, offset).map { it.toDomain() }
    }

    override fun getFavoriteRecipesFlow(): Flow<List<Recipe>> {
        return dao.getFavoriteRecipesFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleFavorite(localId: Long): Boolean {
        val current = dao.getRecipeByLocalId(localId) ?: return false
        val newState = !current.isFavorite
        dao.setFavoriteByLocalId(localId, newState)
        return newState
    }

    override suspend fun getRecipeByLocalId(localId: Long): Recipe? {
        return dao.getRecipeByLocalId(localId)?.toDomain()
    }

    override suspend fun deleteLocalRecipe(localId: Long) {
        val entity = dao.getRecipeByLocalId(localId) ?: return
        dao.deleteRecipe(entity)
    }

    // mapping helpers inside impl (private extension functions)
    private fun Recipe.toEntity(remoteId: Int? = null): RecipeEntity {
        return RecipeEntity(
            remoteId = remoteId,
            name = this.name,
            description = description,
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
            isFavorite = false // new local recipes default to not favorite
        )
    }

    private fun RecipeEntity.toDomain(): Recipe {
        return Recipe(
            id = (remoteId ?: localId.toInt()), // if remoteId exists use it, else use local id as int
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
    }
}