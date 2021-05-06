package com.emertozd.moviesplus.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emertozd.moviesplus.data.models.Movie
import com.emertozd.moviesplus.data.models.SortOptions
import com.emertozd.moviesplus.ui.adapters.MovieAdapter
import com.emertozd.moviesplus.ui.home.HomeViewModel
import com.emertozd.moviesplus.util.RecyclerViewPaginator

object RecyclerViewBinding {


    @JvmStatic
    @BindingAdapter("paginator", "sortParameter")
    fun pagination(
        view: RecyclerView,
        viewModel: HomeViewModel,
        sortOptions: SortOptions
    ) {
        RecyclerViewPaginator(
            recyclerView = view,
            isLoading = { viewModel.isLoading },
            loadMore = { viewModel.fetchNextMovies(sortOptions) },
            onLast = { viewModel.isOnLast(sortOptions) }
        )
    }

    @JvmStatic
    @BindingAdapter("adapterMovieList")
    fun bindAdapterMovieList(view: RecyclerView, movieList: List<Movie>?) {
        if (view.adapter == null) {
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            view.adapter = MovieAdapter().apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            }
        }
        if (!movieList.isNullOrEmpty()) {
            (view.adapter as MovieAdapter).setMovieList(movieList)
        }
    }
}
