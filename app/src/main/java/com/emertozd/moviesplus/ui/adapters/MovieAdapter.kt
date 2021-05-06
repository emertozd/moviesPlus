package com.emertozd.moviesplus.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emertozd.moviesplus.R
import com.emertozd.moviesplus.data.models.Movie
import com.emertozd.moviesplus.databinding.ItemMovieBinding
import com.skydoves.bindables.binding


class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val items: MutableList<Movie> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = parent.binding<ItemMovieBinding>(R.layout.item_movie)
        return MovieViewHolder(binding)
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
