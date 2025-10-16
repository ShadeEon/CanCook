package com.example.cancook.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM local_recipes ORDER BY created_at DESC")
    fun getAllLocalRecipesFlow(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM local_recipes ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllLocalRecipes(limit: Int, offset: Int): List<RecipeEntity>

    @Query("SELECT * FROM local_recipes WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteRecipesFlow(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM local_recipes WHERE localId = :localId LIMIT 1")
    suspend fun getRecipeByLocalId(localId: Long): RecipeEntity?

    @Query("SELECT * FROM local_recipes WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getRecipeByRemoteId(remoteId: Int): RecipeEntity?

    @Query("UPDATE local_recipes SET is_favorite = :isFav WHERE localId = :localId")
    suspend fun setFavoriteByLocalId(localId: Long, isFav: Boolean)
}