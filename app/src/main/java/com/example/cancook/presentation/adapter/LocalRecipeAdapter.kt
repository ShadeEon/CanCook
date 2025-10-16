package com.example.cancook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.cancook.R
import com.example.cancook.data.local.RecipeEntity

class LocalRecipeAdapter(
    private val recipes: MutableList<RecipeEntity> = mutableListOf(),
    private val onItemClick: ((RecipeEntity) -> Unit)? = null,
    private val onFavoriteClick: ((RecipeEntity) -> Unit)? = null
) : RecyclerView.Adapter<LocalRecipeAdapter.LocalRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalRecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return LocalRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalRecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun submitList(newList: List<RecipeEntity>) {
        recipes.clear()
        recipes.addAll(newList)
        notifyDataSetChanged()
    }

    inner class LocalRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        private val recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
        private val ratingText: TextView = itemView.findViewById(R.id.rating_text)
        private val favoriteIcon: ImageButton = itemView.findViewById(R.id.favorite_icon)
        private val duration: TextView = itemView.findViewById(R.id.duration_text)

        fun bind(recipe: RecipeEntity) {
            recipeTitle.text = recipe.name
            ratingText.text = recipe.rating?.toString() ?: "N/A"
            val totalDuration = (recipe.prepTimeMinutes ?: 0) + (recipe.cookTimeMinutes ?: 0)
            duration.text = "$totalDuration mins"

            Glide.with(itemView.context)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.shimmer_placeholder)
                .error(R.drawable.drawable_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(recipeImage)

            itemView.setOnClickListener { onItemClick?.invoke(recipe) }
            favoriteIcon.setOnClickListener { onFavoriteClick?.invoke(recipe) }
        }
    }
}