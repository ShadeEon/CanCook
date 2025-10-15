package com.example.cancook.domain.usecase

import com.example.cancook.domain.repository.RecipesRepository

class FetchMealTypesUseCase(
    private val repository: RecipesRepository
) {
    suspend operator fun invoke(limit: Int = 100, skip: Int = 0): List<String> {
        return repository.fetchMealTypes(limit, skip)
    }
}