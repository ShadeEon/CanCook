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
import com.example.cancook.domain.model.Recipe

class RecipeListAdapter(
    private val onItemClick: ((Recipe) -> Unit)? = null,
    private val onFavoriteClick: ((Recipe) -> Unit)? = null
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

    private val recipes = mutableListOf<Recipe>()

    fun submitList(newList: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        private val recipeTitle: TextView = itemView.findViewById(R.id.recipe_title)
        private val ratingText: TextView = itemView.findViewById(R.id.rating_text)
        private val favoriteIcon: ImageButton = itemView.findViewById(R.id.favorite_icon)
        private val duration: TextView = itemView.findViewById(R.id.duration_text)

        fun bind(recipe: Recipe) {
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