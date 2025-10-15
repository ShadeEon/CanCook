package com.example.cancook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cancook.R

class QuickSearchAdapter(
    private var mealTypes: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<QuickSearchAdapter.QuickSearchViewHolder>() {

    inner class QuickSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chipText: TextView = itemView.findViewById(R.id.chipText)
        val chipIcon: ImageView = itemView.findViewById(R.id.chipIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_search_chip, parent, false)
        return QuickSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuickSearchViewHolder, position: Int) {
        val mealType = mealTypes[position]
        holder.chipText.text = mealType

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
        holder.chipIcon.setImageResource(iconRes)

        holder.itemView.setOnClickListener { onItemClick(mealType) }
    }

    override fun getItemCount() = mealTypes.size

    fun updateData(newMealTypes: List<String>) {
        mealTypes = newMealTypes
        notifyDataSetChanged()
    }

}
