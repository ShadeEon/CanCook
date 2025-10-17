package com.example.cancook.presentation.screen.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cancook.R
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cancook.data.paging.RecipesPagingSource
import com.example.cancook.databinding.FragmentSearchBinding
import com.example.cancook.presentation.adapter.RecipeAdapter
import com.example.cancook.presentation.screen.activity.ViewRecipeActivity
import com.example.cancook.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var searchJob: Job? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModel()
    private lateinit var recipesAdapter: RecipeAdapter

    private var currentFilter: RecipesPagingSource.FilterType? = null
    private var currentQuery: String = ""
    private var isInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesAdapter = RecipeAdapter(
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), ViewRecipeActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            },
            onFavoriteClick = { recipe -> /* favorite logic */ }
        )
        binding.searchRecipeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.searchRecipeRecyclerView.adapter = recipesAdapter

        setupSearchView()

        // Observe savedStateHandle from HomeFragment (See More)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("filterType")
            ?.observe(viewLifecycleOwner) { filter ->
                currentFilter = if (filter == "popular") RecipesPagingSource.FilterType.POPULAR else null
                currentQuery = "" // reset query for filtered search
                initializePaging()
            }

        // Observe search queries from other fragments
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("searchQuery")
            ?.observe(viewLifecycleOwner) { query ->
                query?.let {
                    currentQuery = it
                    binding.searchSearchView.setQuery(it, false)
                    initializePaging()
                }
            }

        // Check arguments only on first launch
        if (!isInitialized) {
            arguments?.let {
                val filterArg = it.getString("filterType")
                currentFilter = if (filterArg == "popular") RecipesPagingSource.FilterType.POPULAR else null
                currentQuery = it.getString("query").orEmpty()
            }
            initializePaging()
            isInitialized = true
        }
    }

    private fun initializePaging() {
        val pagingFlow = if (currentQuery.isBlank()) {
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                pagingSourceFactory = { searchViewModel.createPagingSource(currentFilter) }
            ).flow
        } else {
            searchViewModel.searchRecipes(currentQuery)
        }

        lifecycleScope.launch {
            pagingFlow.collectLatest { pagingData: PagingData<*> ->
                recipesAdapter.submitData(pagingData as PagingData<com.example.cancook.domain.model.Recipe>)
                updateResultText()
            }
        }
    }

    private fun setupSearchView() {
        binding.searchSearchView.apply {
            isIconified = false
            clearFocus()
            val closeButton: View? = findViewById(androidx.appcompat.R.id.search_close_btn)
            closeButton?.visibility = View.GONE

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        currentQuery = it
                        initializePaging()
                    }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    closeButton?.visibility = if (!newText.isNullOrBlank()) View.VISIBLE else View.GONE
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        currentQuery = newText.orEmpty()
                        initializePaging()
                    }
                    return true
                }
            })
        }
    }

    private fun updateResultText() {
        binding.searchResultNumber.text = when {
            currentQuery.isNotBlank() -> "Search for \"$currentQuery\" returned ${recipesAdapter.itemCount} results"
            currentFilter == RecipesPagingSource.FilterType.POPULAR -> "Search Sorted By 'Popularity'"
            else -> ""
        }
        binding.searchResultNumber.visibility =
            if (binding.searchResultNumber.text.isBlank()) View.GONE else View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchSearchView.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}