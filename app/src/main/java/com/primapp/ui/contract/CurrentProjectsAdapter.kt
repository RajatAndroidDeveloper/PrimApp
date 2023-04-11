package com.primapp.ui.contract

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.ItemCurrentProjectsLayoutBinding

class CurrentProjectsAdapter(private var adapterType: String, private var projectList: ArrayList<ProjectData>, private val onItemClick: OnItemClickEvent) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCurrentProjectsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrentProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       var viewHolder = holder as CurrentProjectsViewHolder
        viewHolder.bind(projectList[position])

        viewHolder.itemView.setOnClickListener {
            onItemClick.onItemClick()
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    inner class CurrentProjectsViewHolder(val binding: ItemCurrentProjectsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(projectData: ProjectData) {
            binding.tvProjectTitle.text = projectData.projectTitle
            binding.tvProjectDescription.text = projectData.projectDescription
            if(adapterType == "Ongoing") {
                binding.tvProjectDescription.visibility = View.VISIBLE
            } else {
                binding.tvProjectDescription.visibility = View.GONE
            }
        }
    }
}

interface OnItemClickEvent{
    fun onItemClick()
}