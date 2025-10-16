package com.example.cancook.di

import android.content.Context
import androidx.room.Room
import com.example.cancook.data.local.AppDatabase
import com.example.cancook.data.repository.LocalRecipesRepositoryImpl
import com.example.cancook.domain.repository.LocalRecipesRepository
import com.example.cancook.domain.usecase.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "cancook_db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().recipeDao() }

    single<LocalRecipesRepository> { LocalRecipesRepositoryImpl(get()) }

    // domain use cases
    factory { AddLocalRecipeUseCase(get()) }
    factory { GetLocalRecipesUseCase(get()) }
    factory { GetFavoriteRecipesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
}