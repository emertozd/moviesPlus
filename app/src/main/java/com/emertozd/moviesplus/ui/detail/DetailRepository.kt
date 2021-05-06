package com.emertozd.moviesplus.ui.detail

import androidx.annotation.WorkerThread
import com.emertozd.moviesplus.data.responses.DetailResponse
import com.emertozd.moviesplus.network.MoviesService
import com.emertozd.moviesplus.network.NetworkRepository
import com.emertozd.moviesplus.network.NetworkResult
import com.emertozd.moviesplus.util.Constants.emptyErrorText
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


class DetailRepository @Inject constructor(private val moviesService: MoviesService) :
    NetworkRepository {

    @WorkerThread
    fun detail(
        movieId: Int,
        onStart: () -> Unit,

        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = flow<DetailResponse> {

        val result: NetworkResult<DetailResponse> = wrapNetworkResult(call = {
            moviesService.detail(movieId = movieId)
        }, emptyErrorText)

        when (result) {
            is NetworkResult.SuccessfulNetworkResult -> {
                emit(result.body)
                onSuccess()
            }
            is NetworkResult.EmptyNetworkResult -> onError(result.customErrorMessage)
            is NetworkResult.ErrorNetworkResult -> {
                try {
                    val moshi = Moshi.Builder().build()
                    val jsonAdapter: JsonAdapter<DetailResponse> =
                        moshi.adapter(DetailResponse::class.java)
                    jsonAdapter.fromJson(result.errorMessage)?.let { emit(it) }

                } catch (e: Exception) {
                    onError(result.errorMessage)
                }
            }
            is NetworkResult.ExceptionResult -> onError(result.errorMessage)
        }

    }.onStart { onStart() }.flowOn(Dispatchers.IO)

}