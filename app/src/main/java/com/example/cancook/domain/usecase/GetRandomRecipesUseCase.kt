package com.example.cancook.domain.usecase

import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.RecipesRepository
import kotlin.random.Random

class GetRandomRecipesUseCase(private val repository: RecipesRepository) {
    suspend operator fun invoke(limit: Int = 5): List<Recipe> {
        val allRecipes = repository.getAllRecipes(limit = 100, skip = 0)
        return allRecipes.shuffled().take(limit)
    }
}

