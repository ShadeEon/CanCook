package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cancook.domain.repository.LocalRecipesRepository

class SavedViewModel(private val repository: LocalRecipesRepository) : ViewModel() {

    val myRecipes = repository.getAllLocalRecipesFlow() // LiveData<List<Recipe>>
}