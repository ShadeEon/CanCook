package com.example.cancook.domain.usecase

import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.LocalRecipesRepository
import kotlinx.coroutines.flow.Flow

class AddLocalRecipeUseCase(private val repo: LocalRecipesRepository) {
    suspend operator fun invoke(recipe: Recipe): Long = repo.addLocalRecipe(recipe)
}

class GetLocalRecipesUseCase(private val repo: LocalRecipesRepository) {
    operator fun invoke(): Flow<List<Recipe>> = repo.getAllLocalRecipesFlow()
}

class GetFavoriteRecipesUseCase(private val repo: LocalRecipesRepository) {
    operator fun invoke(): Flow<List<Recipe>> = repo.getFavoriteRecipesFlow()
}

class ToggleFavoriteUseCase(private val repo: LocalRecipesRepository) {
    suspend operator fun invoke(localId: Long): Boolean = repo.toggleFavorite(localId)
}

class DeleteLocalRecipeUseCase(private val repo: LocalRecipesRepository) {
    suspend operator fun invoke(localId: Long) = repo.deleteLocalRecipe(localId)
}

data class LocalUseCases(
    val addLocalRecipe: AddLocalRecipeUseCase,
    val getLocalRecipes: GetLocalRecipesUseCase,
    val getFavoriteRecipes: GetFavoriteRecipesUseCase,
    val toggleFavorite: ToggleFavoriteUseCase,
    val deleteLocalRecipe: DeleteLocalRecipeUseCase
)