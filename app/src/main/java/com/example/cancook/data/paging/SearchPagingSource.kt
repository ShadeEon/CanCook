package com.example.cancook.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cancook.data.api.RecipeApi
import com.example.cancook.data.mapper.toDomain
import com.example.cancook.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow

class SearchPagingSource(
    private val api: RecipeApi,
    private val query: String,
    private val totalCountFlow: MutableStateFlow<Int>? = null
) : PagingSource<Int, Recipe>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val response = api.searchRecipes(
                query = query,
                limit = pageSize,
                skip = page * pageSize
            )

            // Update total count
            totalCountFlow?.value = response.total ?: 0

            val recipes = response.recipes.map { it.toDomain() }

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
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
}