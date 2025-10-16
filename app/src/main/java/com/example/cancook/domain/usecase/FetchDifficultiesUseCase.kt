package com.example.cancook.domain.usecase

import com.example.cancook.domain.repository.RecipesRepository

class FetchDifficultiesUseCase(private val repository: RecipesRepository) {
    suspend operator fun invoke(): List<String> {
        return repository.getDifficulties() // you will implement this in repository
    }
}
