package com.sector.travelmanager.fragments.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sector.travelmanager.databinding.ItemStateBinding
import com.sector.travelmanager.entity.Attraction
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class StatesAdapter(
    private val onItemClick: (Attraction) -> Unit
): ListAdapter<Attraction, StatesAdapter.MyViewHolder>(ItemComparator()) {

    class MyViewHolder(
        private val binding: ItemStateBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(state: Attraction) = with(binding) {
            tvName.text = state.name
            Picasso.with(itemView.context)
                .load(state.image)
                .fit()
                .into(ivAttraction, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError() {

                    }
                })
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemStateBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    private class ItemComparator: DiffUtil.ItemCallback<Attraction>() {
        override fun areItemsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            bind(getItem(position))

            itemView.rootView.setOnClickListener {
                onItemClick.invoke(getItem(position))
                /*val action = StatesFragmentDirections.actionListFragmentToAttractionsFragment(getItem(position))
                itemView.findNavController().navigate(action)*/
            }
        }
    }
}