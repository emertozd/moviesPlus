package com.emertozd.moviesplus.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.emertozd.moviesplus.MainCoroutinesRule
import com.emertozd.moviesplus.MockUtil
import com.emertozd.moviesplus.data.models.SortOptions
import com.emertozd.moviesplus.data.responses.ListResponse
import com.emertozd.moviesplus.network.MoviesService
import com.emertozd.moviesplus.ui.home.HomeRepository
import com.emertozd.moviesplus.ui.home.HomeViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var homeRepository: HomeRepository
    private val moviesService: MoviesService = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        homeRepository = HomeRepository(moviesService)
        viewModel = HomeViewModel(homeRepository)
    }

    @Test
    fun fetchMovieSearchTest() = runBlocking {
        val mockData = MockUtil.mockSearchResponse()
        whenever(moviesService.discover(SortOptions.POPULAR.key,1)).thenReturn(Response.success(mockData))

        val observer: Observer<ListResponse> = mock()
        val fetchedData: LiveData<ListResponse> =
            homeRepository.getSortedMovies(
                sortOptions = SortOptions.POPULAR,
                page = 1,
                onStart = {},
                onSuccess = {},
                onError = {}).asLiveData()
        fetchedData.observeForever(observer)

        viewModel.fetchNextMovies(SortOptions.POPULAR)
        delay(500L)

        verify(observer).onChanged(mockData)
        fetchedData.removeObserver(observer)
    }


}
