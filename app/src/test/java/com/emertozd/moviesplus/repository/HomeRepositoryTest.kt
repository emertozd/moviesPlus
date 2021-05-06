@file:Suppress("SpellCheckingInspection")

package com.emertozd.moviesplus.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.emertozd.moviesplus.MainCoroutinesRule
import com.emertozd.moviesplus.MockUtil.mockSearchResponse
import com.emertozd.moviesplus.data.models.SortOptions
import com.emertozd.moviesplus.network.MoviesService
import com.emertozd.moviesplus.ui.home.HomeRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class HomeRepositoryTest {

    private lateinit var repository: HomeRepository
    private val service: MoviesService = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        repository = HomeRepository(service)
    }

    @Test
    fun searchMoviesTest() = runBlocking {
        val mockResponse = mockSearchResponse()
        whenever(service.discover(SortOptions.POPULAR.key, 1)).thenReturn(Response.success(mockResponse))
        val mockModel = mockResponse.results!![0]

        repository.getSortedMovies(
            sortOptions = SortOptions.POPULAR,
            page = 1,
            onStart = {},
            onSuccess = {},
            onError = {}
        ).test {
            assertEquals(expectItem().results?.get(0)?.id, mockModel.id)
            expectComplete()
        }
    }
}
