package com.example.cancook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cancook.R

class InstructionAdapter(private val instructions: List<String>) :
    RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder>() {

    inner class InstructionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepNumber: TextView = view.findViewById(R.id.stepNumber)
        val stepDescription: TextView = view.findViewById(R.id.stepDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instruction_step, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) {
        holder.stepNumber.text = "Step ${position + 1}"
        holder.stepDescription.text = instructions[position]
    }

    override fun getItemCount() = instructions.size
}
