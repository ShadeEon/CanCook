package com.example.cancook.presentation.screen.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cancook.R
import com.example.cancook.data.local.RecipeDao
import com.example.cancook.presentation.adapter.LocalRecipeAdapter
import com.example.cancook.presentation.screen.activity.AddRecipeActivity
import com.example.cancook.presentation.screen.activity.ViewRecipeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SavedFragment : Fragment(R.layout.fragment_saved) {

    private lateinit var myRecipeRecyclerView: RecyclerView
    private lateinit var adapter: LocalRecipeAdapter

    private val recipeDao: RecipeDao by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToAddRecipeButton: FloatingActionButton = view.findViewById(R.id.goToAddRecipeButton)
        goToAddRecipeButton.setOnClickListener {
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
        }

        adapter = LocalRecipeAdapter(
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), AddRecipeActivity::class.java)
                intent.putExtra("local_recipe", recipe) // key changed
                startActivity(intent)
            },
            onFavoriteClick = { recipe ->

            }
        )

        myRecipeRecyclerView = view.findViewById(R.id.myRecipeRecyclerView)
        myRecipeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        myRecipeRecyclerView.adapter = adapter
        myRecipeRecyclerView.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            recipeDao.getAllLocalRecipesFlow().collectLatest { recipes ->
                adapter.submitList(recipes)
            }
        }
    }
}
