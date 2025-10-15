package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RecipesRepository
) : ViewModel() {

    private val _randomRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val randomRecipes: StateFlow<List<Recipe>> = _randomRecipes

    private val _popularRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val popularRecipes: StateFlow<List<Recipe>> = _popularRecipes

    init {
        viewModelScope.launch {
            _randomRecipes.value = repository.getLimitedRandomRecipes(limit = 10)
            _popularRecipes.value = repository.getLimitedPopularRecipes(limit = 10)
        }
    }
}
