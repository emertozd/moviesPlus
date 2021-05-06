package com.emertozd.moviesplus.network

import com.emertozd.moviesplus.data.responses.ListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    @GET("discover/movie?certification&vote_count.gte=5000")
    suspend fun discover(
        @Query("sort_by") sortBy: String,
        @Query("page") page: Int
    ): Response<ListResponse>

}
