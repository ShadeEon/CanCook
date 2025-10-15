package com.example.cancook.domain.usecase

import androidx.paging.PagingData
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.RecipesRepository
import kotlinx.coroutines.flow.Flow

class GetPagedRecipesUseCase(private val repository: RecipesRepository) {
    operator fun invoke(pageSize: Int = 10): Flow<PagingData<Recipe>> =
        repository.getPagedRecipes(pageSize)
}

class GetAllRecipesUseCase(private val repository: RecipesRepository) {
    suspend operator fun invoke(): List<Recipe> {
        return repository.getAllRecipes()
    }
}
