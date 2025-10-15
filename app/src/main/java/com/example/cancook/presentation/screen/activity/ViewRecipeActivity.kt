package com.example.cancook.presentation.screen.activity

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cancook.R
import com.example.cancook.databinding.ActivityViewRecipeBinding
import com.example.cancook.domain.model.Recipe
import com.example.cancook.presentation.adapter.InstructionAdapter

class ViewRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("recipe", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("recipe")
        }

        recipe?.let { displayRecipe(it) }

        binding.backButton.setOnClickListener { finish() }

        setupToggle(
            toggleIcon = binding.descriptionToggleIcon,
            contentView = binding.recipeDescriptionInput
        )

        setupToggle(
            toggleIcon = binding.ingredientsToggleIcon,
            contentView = binding.ingredientsCardWrapper
        )

        setupToggle(
            toggleIcon = binding.instructionsToggleIcon,
            contentView = binding.instructionsCardWrapper
        )
    }

    private fun displayRecipe(recipe: Recipe) {
        binding.recipeName.text = recipe.name
        binding.ratingText.text = recipe.rating?.toString() ?: "N/A"
        binding.cuisineTitle.text = recipe.cuisine
        val totalDuration = (recipe.prepTimeMinutes ?: 0) + (recipe.cookTimeMinutes ?: 0)
        binding.prepTime.text = "$totalDuration mins"
        binding.difficulty.text = recipe.difficulty ?: "N/A"
        binding.perServing.text = recipe.servings?.toString() ?: "N/A"
        binding.perServingCalories.text = recipe.caloriesPerServing?.toString() ?: "N/A"

        Glide.with(this)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.shimmer_placeholder)
            .error(R.drawable.drawable_placeholder)
            .into(binding.recipeImage)

        // Description, Ingredients, Instructions
        (binding.recipeDescriptionInput as? TextView)?.text = "No description"
        (binding.recipeIngredientsInput as? TextView)?.text =
            recipe.ingredients.joinToString("\n") { ingredient ->
                val purpleBullet = "●  "
                "$purpleBullet$ingredient"
            }
        binding.instructionsRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.instructionsRecyclerView.adapter = InstructionAdapter(recipe.instructions)

        binding.mealTypeChipGroup.removeAllViews()
        recipe.mealType.forEach { mealType ->
            val iconRes = when (mealType.lowercase()) {
                "breakfast" -> R.drawable.ic_breakfast
                "lunch" -> R.drawable.ic_lunch
                "dinner" -> R.drawable.ic_dinner
                "snacks" -> R.drawable.ic_snack
                "appetizer" -> R.drawable.ic_appetizer
                "beverage" -> R.drawable.ic_beverages
                "side dish" -> R.drawable.ic_sidedish
                "dessert" -> R.drawable.ic_dessert
                else -> R.drawable.ic_food
            }

            val chip = com.google.android.material.chip.Chip(this).apply {
                text = mealType.replaceFirstChar { it.uppercase() }
                isClickable = false
                isCheckable = false
                setChipIconResource(iconRes)
                chipIconSize = 48f
                chipIconTint = null
                chipStartPadding = 8f
                iconStartPadding = 8f
                textStartPadding = 4f
            }
            binding.mealTypeChipGroup.addView(chip)
        }

            binding.tagsChipGroup.removeAllViews()
        recipe.tags.forEach { tag ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = tag
                isClickable = false
                isCheckable = false
            }
            binding.tagsChipGroup.addView(chip)
        }
    }

    private fun setupToggle(toggleIcon: View, contentView: View) {
        toggleIcon.setOnClickListener {
            val isVisible = contentView.visibility == View.VISIBLE
            // Toggle the visibility
            contentView.visibility = if (isVisible) View.GONE else View.VISIBLE

            // Determine the start and end degrees for the rotation
            val fromDegrees: Float
            val toDegrees: Float

            if (isVisible) {
                // Content is about to be HIDDEN (was visible) -> Rotate from 90 degrees back to 0 degrees (left/counter-clockwise rotation)
                fromDegrees = -90f
                toDegrees = 0f
            } else {
                // Content is about to be SHOWN (was hidden) -> Rotate from 0 degrees to 90 degrees (right/clockwise rotation)
                fromDegrees = 0f
                toDegrees = -90f
            }

            val rotate = RotateAnimation(
                fromDegrees,
                toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 200
            rotate.fillAfter = true
            toggleIcon.startAnimation(rotate)
        }
    }
}