@file:Suppress("SpellCheckingInspection")

package com.emertozd.moviesplus.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.emertozd.moviesplus.MainCoroutinesRule
import com.emertozd.moviesplus.MockUtil.mockDetailResponse
import com.emertozd.moviesplus.network.MoviesService
import com.emertozd.moviesplus.ui.detail.DetailRepository
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
class DetailRepositoryTest {

    private lateinit var repository: DetailRepository
    private val service: MoviesService = mock()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        repository = DetailRepository(service)
    }


    @Test
    fun fetchMovieDetailTest() = runBlocking {
        val mockData = mockDetailResponse()
        whenever(service.detail(122)).thenReturn(Response.success(mockData))

        repository.detail(
            movieId = 122,
            onStart = {},
            onSuccess = {},
            onError = {}
        ).test {
            assertEquals(expectItem().runtime, 201)
            expectComplete()
        }
    }

}
