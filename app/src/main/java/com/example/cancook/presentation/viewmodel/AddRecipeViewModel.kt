package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cancook.data.local.RecipeDao
import com.example.cancook.data.local.RecipeEntity
import com.example.cancook.domain.repository.RecipesRepository
import com.example.cancook.domain.usecase.FetchDifficultiesUseCase
import kotlinx.coroutines.launch
import com.example.cancook.domain.usecase.FetchMealTypesUseCase
import com.example.cancook.domain.usecase.FetchTagsUseCase

class AddRecipeViewModel(
    private val repository: RecipesRepository,
    private val recipeDao: RecipeDao,
    private val fetchMealTypesUseCase: FetchMealTypesUseCase,
    private val fetchTagsUseCase: FetchTagsUseCase,
    private val fetchDifficultiesUseCase: FetchDifficultiesUseCase
) : ViewModel() {

    private val _mealTypes = MutableLiveData<List<String>>()
    val mealTypes: LiveData<List<String>> get() = _mealTypes

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    private val _difficulties = MutableLiveData<List<String>>()
    val difficulties: LiveData<List<String>> get() = _difficulties

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadMealTypes()
        loadTags()
        loadDifficulties()
    }

    private fun loadMealTypes() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = fetchMealTypesUseCase()
            _mealTypes.value = result
            _isLoading.value = false
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = fetchTagsUseCase()
            _tags.value = result
            _isLoading.value = false
        }
    }

    private fun loadDifficulties() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = fetchDifficultiesUseCase()
            _difficulties.value = result
            _isLoading.value = false
        }
    }

    fun saveRecipe(
        name: String,
        description: String?,
        ingredients: List<String>,
        instructions: List<String>,
        mealTypes: List<String>,
        tags: List<String>,
        difficulty: String?,
        prepTime: Int?,
        cookTime: Int?,
        servings: Int?,
        cuisine: String?,
        rating: Float?,
        calories: Int?,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            val recipe = RecipeEntity(
                name = name,
                description = description,
                ingredients = ingredients,
                instructions = instructions,
                prepTimeMinutes = prepTime,
                cookTimeMinutes = cookTime,
                servings = servings,
                difficulty = difficulty,
                cuisine = cuisine,
                caloriesPerServing = calories,
                tags = tags,
                imageUrl = imageUrl,
                rating = null,
                reviewCount = null,
                mealType = mealTypes,
                isFavorite = false
            )
            recipeDao.insertRecipe(recipe)
        }
    }
}