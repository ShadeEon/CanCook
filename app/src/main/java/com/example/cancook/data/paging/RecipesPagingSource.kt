package com.example.cancook.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cancook.data.api.RecipeApi
import com.example.cancook.data.mapper.toDomain
import com.example.cancook.domain.model.Recipe

class RecipesPagingSource(
    private val api: RecipeApi,
    private val pageSize: Int = 10,
    private val filter: FilterType? = null
) : PagingSource<Int, Recipe>() {

    enum class FilterType { POPULAR, RANDOM }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        val page = params.key ?: 0

        return try {
            val response = api.getRecipes(limit = pageSize, skip = page * pageSize)
            var recipes = response.recipes.map { it.toDomain() }

            recipes = when (filter) {
                FilterType.POPULAR -> recipes
                    .filter { it.rating!! >= 4.5 && it.reviewCount!! >= 50 }
                    .sortedWith(compareByDescending<Recipe> { it.rating }
                        .thenByDescending { it.reviewCount })
                    .take(10)
                FilterType.RANDOM -> recipes.shuffled().take(10)
                null -> recipes
            }

            LoadResult.Page(
                data = recipes,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (recipes.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? =
        state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
}