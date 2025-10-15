package com.example.cancook.di

import com.example.cancook.data.repository.RecipesRepositoryImpl
import com.example.cancook.domain.repository.RecipesRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<RecipesRepository> { RecipesRepositoryImpl(get()) }
}