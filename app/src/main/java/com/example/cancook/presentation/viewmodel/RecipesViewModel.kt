package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.cancook.domain.usecase.GetAllRecipesUseCase
import com.example.cancook.domain.usecase.GetPagedRecipesUseCase


class RecipesViewModel(
    private val getPagedRecipes: GetPagedRecipesUseCase,
    private val getAllRecipes: GetAllRecipesUseCase
) : ViewModel() {

    val recipesPagingFlow = getPagedRecipes()
        .cachedIn(viewModelScope)
}

