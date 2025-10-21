package com.example.cancook.data.repository

import com.example.cancook.data.api.RecipeApi
import com.example.cancook.data.model.RecipeDto
import com.example.cancook.data.model.RecipesResponseDto
import com.example.cancook.domain.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class RecipesRepositoryImplTest {

    @Mock
    lateinit var api: RecipeApi

    private lateinit var repository: RecipesRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = RecipesRepositoryImpl(api)
    }

    @Test
    fun `getLimitedRandomRecipes returns mapped list`() = runTest {
        // Arrange
        val recipeDtos = listOf(
            RecipeDto(
                id = 1,
                name = "Pasta",
                ingredients = listOf("Noodles", "Sauce"),
                instructions = listOf("Boil noodles", "Add sauce"),
                prepTimeMinutes = 10,
                cookTimeMinutes = 20,
                servings = 2,
                difficulty = "Easy",
                cuisine = "Italian",
                caloriesPerServing = 400,
                tags = listOf("Dinner", "Quick"),
                image = "https://example.com/pasta.jpg",
                mealType = listOf("Lunch"),
                rating = 4.5,
                reviewCount = 100
            ),
            RecipeDto(
                id = 2,
                name = "Salad",
                ingredients = listOf("Lettuce", "Tomato"),
                instructions = listOf("Chop veggies", "Mix"),
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                servings = 1,
                difficulty = "Easy",
                cuisine = "American",
                caloriesPerServing = 150,
                tags = listOf("Healthy"),
                image = "https://example.com/salad.jpg",
                mealType = listOf("Lunch"),
                rating = 4.0,
                reviewCount = 50
            )
        )

        val apiResponse = RecipesResponseDto(
            total = 2,
            skip = 0,
            limit = 5,
            recipes = recipeDtos
        )

        // When API is called, return this fake response
        whenever(api.getRecipes(limit = 5, skip = 0)).thenReturn(apiResponse)

        // Act
        val result = repository.getLimitedRandomRecipes(limit = 5)

        // Assert
        assertEquals(2, result.size)
        assertEquals("Salad", result[0].name) // assuming toDomain() keeps the name

        // Verify API call
        verify(api).getRecipes(limit = 5, skip = 0)
    }

    @Test
    fun `getLimitedRandomRecipes returns empty list when api throws exception`() = runTest {
        // Arrange
        whenever(api.getRecipes(limit = 5, skip = 0)).thenThrow(RuntimeException("Network error"))

        // Act
        val result = repository.getLimitedRandomRecipes(limit = 5)

        // Assert
        assertEquals(emptyList<Recipe>(), result)
    }

    @Test
    fun `getLimitedPopularRecipes sorts recipes correctly`() = runTest {
        // Arrange
        val recipeDtos = listOf(
            RecipeDto(
                id = 1,
                name = "Burger",
                ingredients = listOf("Bun", "Beef"),
                instructions = listOf("Grill beef", "Assemble burger"),
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                servings = 1,
                difficulty = "Easy",
                cuisine = "American",
                caloriesPerServing = 800,
                tags = listOf("FastFood"),
                image = "",
                mealType = listOf("Lunch"),
                rating = 4.0,
                reviewCount = 200
            ),
            RecipeDto(
                id = 2,
                name = "Pizza",
                ingredients = listOf("Dough", "Cheese"),
                instructions = listOf("Bake dough", "Add toppings"),
                prepTimeMinutes = 20,
                cookTimeMinutes = 30,
                servings = 2,
                difficulty = "Medium",
                cuisine = "Italian",
                caloriesPerServing = 700,
                tags = listOf("Dinner"),
                image = "",
                mealType = listOf("Dinner"),
                rating = 5.0,
                reviewCount = 100
            ),
            RecipeDto(
                id = 3,
                name = "Salad",
                ingredients = listOf("Lettuce", "Tomato"),
                instructions = listOf("Mix veggies"),
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                servings = 1,
                difficulty = "Easy",
                cuisine = "Healthy",
                caloriesPerServing = 150,
                tags = listOf("Vegetarian"),
                image = "",
                mealType = listOf("Lunch"),
                rating = 4.0,
                reviewCount = 50
            )
        )

        val apiResponse = RecipesResponseDto(
            total = 3,
            skip = 0,
            limit = 5,
            recipes = recipeDtos
        )

        // Mock API
        whenever(api.getRecipes(limit = 5, skip = 0)).thenReturn(apiResponse)

        // Act
        val result = repository.getLimitedPopularRecipes(limit = 5)

        // Assert
        assertEquals(3, result.size)
        assertEquals("Pizza", result[0].name) // Highest rating (5.0)
        assertEquals("Burger", result[1].name) // Same rating as Salad, but more reviews
        assertEquals("Salad", result[2].name)

        verify(api).getRecipes(limit = 5, skip = 0)
    }

}