package com.emertozd.moviesplus.ui.adapters

import android.os.Bundle
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.emertozd.moviesplus.R
import com.emertozd.moviesplus.data.models.Movie
import com.emertozd.moviesplus.databinding.ItemMovieBinding
import com.emertozd.moviesplus.ui.MainActivity
import com.emertozd.moviesplus.ui.home.HomeFragmentDirections
import com.emertozd.moviesplus.util.Constants
import com.skydoves.bindables.binding
import dagger.hilt.android.internal.managers.ViewComponentManager


class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val items: MutableList<Movie> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = parent.binding<ItemMovieBinding>(R.layout.item_movie)
        return MovieViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { pos -> pos != NO_POSITION }
                    ?: return@setOnClickListener
                if (it.resources.getBoolean(R.bool.isTablet)) {
                    val detailNavHost =
                        (((it.context) as ViewComponentManager.FragmentContextWrapper).baseContext as MainActivity).supportFragmentManager.findFragmentById(
                            R.id.detail_nav_container
                        ) as NavHostFragment
                    val graph =
                        detailNavHost.navController.navInflater.inflate(R.navigation.nav_graph)
                    graph.startDestination = R.id.detailFragment
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.argKeyMovie, items[position])
                    detailNavHost.navController.setGraph(graph, bundle)
                } else {
                    it.findNavController().navigate(
                        HomeFragmentDirections.actionSearchFragmentToDetailFragment(items[position])
                    )
                }
            }
        }
    }

    fun setMovieList(movieList: List<Movie>) {
        val previousItemSize = items.size
        items.clear()
        items.addAll(movieList)
        if (previousItemSize == 0) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(previousItemSize, movieList.size)
        }
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.binding.apply {
            movie = items[position]
            executePendingBindings()
        }
    }

    override fun getItemCount() = items.size

    class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)
}
