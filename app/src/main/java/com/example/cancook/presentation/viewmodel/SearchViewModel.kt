package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cancook.data.paging.RecipesPagingSource
import com.example.cancook.domain.model.Recipe
import com.example.cancook.domain.repository.RecipesRepository
import com.example.cancook.domain.usecase.GetPopularRecipesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class SearchViewModel(
    private val repository: RecipesRepository,
    private val getPopularRecipesUseCase: GetPopularRecipesUseCase
) : ViewModel() {

    val totalCountFlow = MutableStateFlow(0)

    fun searchRecipes(query: String): Flow<PagingData<Recipe>> {
        return repository.searchRecipes(query, pageSize = 10)
            .cachedIn(viewModelScope)
    }

    fun updateTotalCount(count: Int) {
        totalCountFlow.value = count
    }

    fun getDefaultRecipes(): Flow<PagingData<Recipe>> {
        return repository.searchRecipes(query = "", pageSize = 10).cachedIn(viewModelScope)
    }

    fun getPopularRecipes() = liveData {
        val popular = getPopularRecipesUseCase(limit = 10)
        emit(popular)
    }

    fun createPagingSource(filter: RecipesPagingSource.FilterType?): RecipesPagingSource {
        return repository.createPagingSource(filter)
    }
}