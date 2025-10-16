package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.usecase.LocalUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocalRecipesViewModel(
    private val localUseCases: LocalUseCases
) : ViewModel() {

    val localRecipesState = localUseCases.getLocalRecipes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteRecipesState = localUseCases.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            localUseCases.addLocalRecipe(recipe)
        }
    }

    fun toggleFavorite(localId: Long) {
        viewModelScope.launch {
            localUseCases.toggleFavorite(localId)
        }
    }

    fun deleteRecipe(localId: Long) {
        viewModelScope.launch {
            localUseCases.deleteLocalRecipe(localId)
        }
    }
}