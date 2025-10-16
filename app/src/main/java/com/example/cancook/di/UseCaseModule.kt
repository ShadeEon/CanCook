package com.example.cancook.di

import com.example.cancook.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    // --- API Use Cases ---
    factory { GetRandomRecipesUseCase(get()) }
    factory { GetPopularRecipesUseCase(get()) }
    factory { FetchMealTypesUseCase(get()) }
    factory { GetAllRecipesUseCase(get()) }
    factory { FetchTagsUseCase(get()) }        // <-- add this
    factory { FetchDifficultiesUseCase(get()) } // <-- add this

    // --- ROOM (Local) Use Cases ---
    factory { AddLocalRecipeUseCase(get()) }
    factory { GetLocalRecipesUseCase(get()) }
    factory { GetFavoriteRecipesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { DeleteLocalRecipeUseCase(get()) }

    factory {
        LocalUseCases(
            addLocalRecipe = get(),
            getLocalRecipes = get(),
            getFavoriteRecipes = get(),
            toggleFavorite = get(),
            deleteLocalRecipe = get()
        )
    }
}