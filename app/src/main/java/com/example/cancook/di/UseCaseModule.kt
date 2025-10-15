package com.example.cancook.di

import com.example.cancook.domain.usecase.FetchMealTypesUseCase
import com.example.cancook.domain.usecase.GetAllRecipesUseCase
import com.example.cancook.domain.usecase.GetRandomRecipesUseCase
import com.example.cancook.domain.usecase.GetPopularRecipesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetRandomRecipesUseCase(get()) }
    factory { GetPopularRecipesUseCase(get()) }
    factory { FetchMealTypesUseCase(get()) }
    factory { GetAllRecipesUseCase(get()) }
}
