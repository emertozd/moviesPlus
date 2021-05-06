package com.emertozd.moviesplus.network

import com.emertozd.moviesplus.MainCoroutinesRule
import com.emertozd.moviesplus.data.models.SortOptions
import com.emertozd.moviesplus.data.responses.DetailResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class MoviesServiceTest : ApiAbstract<MoviesService>() {

    private lateinit var service: MoviesService

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @Before
    fun initService() {
        service = createService(MoviesService::class.java)
    }

    @Test
    fun listTest() = runBlocking {
        enqueueResponse("/SearchResponse.json")
        val response = service.discover(SortOptions.POPULAR.key, 1)
        val responseBody = requireNotNull(response.body())
        mockWebServer.takeRequest()

        assertThat(responseBody.total_results, `is`(13))
        assertThat(
            responseBody.results!![2].title,
            `is`("The Lord of the Rings: The Return of the King")
        )
        assertThat(responseBody.results!![2].poster_path, `is`("/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg"))
    }

    @Throws(IOException::class)
    @Test
    fun detailTest() = runBlocking {
        enqueueResponse("/ReturnOfTheKing.json")
        val response = service.detail(122)
        val responseBody = requireNotNull(response.body()) as DetailResponse
        mockWebServer.takeRequest()

        assertThat(responseBody.id, `is`(122))
        assertThat(responseBody.imdb_id, `is`("tt0167260"))
        assertThat(responseBody.poster_path, `is`("/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg"))
    }
}
