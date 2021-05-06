package com.emertozd.moviesplus.data.responses

import android.os.Parcelable
import com.emertozd.moviesplus.data.models.Movie
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ListResponse(
    val page: Int?,
    val results: List<Movie>?,
    val total_pages: Int?,
    val total_results: Int?,
    val status_code: String?,
    val status_message: String?,
    val success: Boolean?
) : Parcelable