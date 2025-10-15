package com.example.cancook.di

import com.example.cancook.presentation.viewmodel.HomeViewModel
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
}

