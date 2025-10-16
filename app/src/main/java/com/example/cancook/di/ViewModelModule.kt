package com.example.cancook.di

import com.example.cancook.presentation.viewmodel.AddRecipeViewModel
import com.example.cancook.presentation.viewmodel.HomeViewModel
import com.example.cancook.presentation.viewmodel.LocalRecipesViewModel
import com.example.cancook.presentation.viewmodel.QuickSearchViewModel
import com.example.cancook.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<HomeViewModel> {
        HomeViewModel(
            repository = get()
        )
    }

    viewModel { QuickSearchViewModel(get()) }

    viewModel<SearchViewModel> {
        SearchViewModel(
            repository = get(),
            getPopularRecipesUseCase = get()
        )
    }

    //New Local Recipes ViewModel (Room Database)
    viewModel<LocalRecipesViewModel> {
        LocalRecipesViewModel(
            localUseCases = get()
        )
    }

    viewModel {
        AddRecipeViewModel(
            repository = get(),
            recipeDao = get(), // provided by localModule
            fetchMealTypesUseCase = get(),
            fetchTagsUseCase = get(),
            fetchDifficultiesUseCase = get()
        )
    }
}
