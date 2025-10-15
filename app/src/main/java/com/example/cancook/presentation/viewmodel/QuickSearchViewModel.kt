package com.example.cancook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cancook.domain.usecase.FetchMealTypesUseCase

class QuickSearchViewModel(
    private val fetchMealTypesUseCase: FetchMealTypesUseCase
) : ViewModel() {

    private val _mealTypes = MutableLiveData<List<String>>()
    val mealTypes: LiveData<List<String>> = _mealTypes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadMealTypes() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = fetchMealTypesUseCase()

            val normalized = result.map { mealType ->
                when (mealType.trim().lowercase()) {
                    "snack" -> "Snacks"
                    else -> mealType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }

            _mealTypes.value = normalized.distinct()
            _isLoading.value = false
        }
    }
}