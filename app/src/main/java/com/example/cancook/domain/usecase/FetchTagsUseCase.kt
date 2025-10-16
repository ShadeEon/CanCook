package com.example.cancook.domain.usecase

import com.example.cancook.domain.repository.RecipesRepository

class FetchTagsUseCase(private val repository: RecipesRepository) {
    suspend operator fun invoke(): List<String> {
        return repository.getTags() // implement API call in repository
    }
}
