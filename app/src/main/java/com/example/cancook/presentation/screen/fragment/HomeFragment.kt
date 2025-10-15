package com.example.cancook.presentation.screen.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cancook.R
import com.example.cancook.databinding.FragmentHomeBinding
import com.example.cancook.presentation.adapter.RecipeListAdapter
import com.example.cancook.presentation.screen.activity.ViewRecipeActivity
import com.example.cancook.presentation.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var recommendationAdapter: RecipeListAdapter
    private lateinit var popularAdapter: RecipeListAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerViews()
        observeViewModel()
        setupSearchViewNavigation()

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.quickSearchFragmentContainerH, QuickSearchFragment())
                .commit()
        }

        setupSeeMoreButtons()
    }

    private fun setupSeeMoreButtons() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavContainer)

        binding.recommendationSeeMore.setOnClickListener {
            bottomNav.selectedItemId = R.id.menuSearchID
        }

        binding.popularSeeMore.setOnClickListener {
            bottomNav.selectedItemId = R.id.menuSearchID

            // Pass filter via savedStateHandle
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set("filterType", "popular")
        }
    }


    private fun setupSearchViewNavigation() {
        binding.homeSearchView.apply {
            isIconified = false
            clearFocus()

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.homeSearchView.windowToken, 0)
                        clearFocus()

                        lifecycleScope.launch {
                            delay(100)
                            val bundle = Bundle().apply { putString("query", it) }

                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavContainer)
                            bottomNav.selectedItemId = R.id.menuSearchID

                            findNavController().currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("searchQuery", it)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(300)
                    }
                    return true
                }
            })

            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
        }
    }

    private fun setupRecyclerViews() {
        recommendationAdapter = RecipeListAdapter(
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), ViewRecipeActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            },
            onFavoriteClick = { recipe ->
                // handle favorite
            }
        )

        popularAdapter = RecipeListAdapter(
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), ViewRecipeActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            }
        )

        binding.recommendationRecyclerView.apply {
            adapter = recommendationAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.popularRecyclerView.apply {
            adapter = popularAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.randomRecipes.collectLatest { recipes ->
                recommendationAdapter.submitList(recipes)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.popularRecipes.collectLatest { recipes ->
                popularAdapter.submitList(recipes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.homeSearchView.isFocusable = true
        binding.homeSearchView.isFocusableInTouchMode = true
    }
}