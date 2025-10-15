package com.example.cancook.presentation.screen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cancook.R
import com.example.cancook.domain.repository.RecipesRepository
import com.example.cancook.presentation.adapter.QuickSearchAdapter
import com.example.cancook.presentation.viewmodel.QuickSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuickSearchFragment : Fragment(R.layout.fragment_quick_search) {
    private val quickSearchViewModel: QuickSearchViewModel by viewModel()
    private lateinit var quickSearchRecyclerView: RecyclerView
    private lateinit var adapter: QuickSearchAdapter

    interface OnQuickSearchSelectedListener {
        fun onQuickSearchSelected(query: String)
    }

    private var listener: OnQuickSearchSelectedListener? = null

    fun setOnQuickSearchSelectedListener(l: OnQuickSearchSelectedListener) {
        listener = l
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickSearchRecyclerView = view.findViewById(R.id.quickSearchRecyclerView)
        adapter = QuickSearchAdapter(emptyList()) { selectedType ->
            listener?.onQuickSearchSelected(selectedType)
        }
        quickSearchRecyclerView.adapter = adapter
        quickSearchRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        quickSearchViewModel.mealTypes.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        quickSearchViewModel.loadMealTypes()
    }
}