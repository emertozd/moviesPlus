package com.emertozd.moviesplus.ui.home

import androidx.annotation.MainThread
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.emertozd.moviesplus.data.models.Movie
import com.emertozd.moviesplus.data.models.SortOptions
import com.emertozd.moviesplus.data.responses.ListResponse
import com.emertozd.moviesplus.util.plusAssign
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BindingViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()


    val popularListResponse: LiveData<ListResponse>
    var popularListLiveData: MutableLiveData<MutableList<Movie>> =
        MutableLiveData<MutableList<Movie>>()

    val topRatedListResponse: LiveData<ListResponse>
    var topRatedListLiveData: MutableLiveData<MutableList<Movie>> =
        MutableLiveData<MutableList<Movie>>()

    val revenueListResponse: LiveData<ListResponse>
    var revenueListLiveData: MutableLiveData<MutableList<Movie>> =
        MutableLiveData<MutableList<Movie>>()

    val releaseDateListResponse: LiveData<ListResponse>
    var releaseDateListLiveData: MutableLiveData<MutableList<Movie>> =
        MutableLiveData<MutableList<Movie>>()

    val popularIndex: MutableStateFlow<Int> = MutableStateFlow(1)
    val topRatedIndex: MutableStateFlow<Int> = MutableStateFlow(1)
    val revenueIndex: MutableStateFlow<Int> = MutableStateFlow(1)
    val releaseDateIndex: MutableStateFlow<Int> = MutableStateFlow(1)

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set

    @get:Bindable
    var toast: String? by bindingProperty(null)

    init {

        popularListResponse = getResponse(popularIndex, SortOptions.POPULAR)
        topRatedListResponse = getResponse(topRatedIndex, SortOptions.TOP_RATED)
        revenueListResponse = getResponse(revenueIndex, SortOptions.REVENUE)
        releaseDateListResponse = getResponse(releaseDateIndex, SortOptions.RELEASE_DATE)

        collectResponse(popularListResponse, popularListLiveData)
        collectResponse(topRatedListResponse, topRatedListLiveData)
        collectResponse(revenueListResponse, revenueListLiveData)
        collectResponse(releaseDateListResponse, releaseDateListLiveData)

    }

    private fun collectResponse(
        response: LiveData<ListResponse>,
        list: MutableLiveData<MutableList<Movie>>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            response.asFlow().collect {
                if (it.results.isNullOrEmpty()) {
                    if (it.status_message != null) {
                        toast = it.status_message.toString()
                        notifyPropertyChanged(::toast)
                    } else {
                        eventChannel.send(Event.ShowNoDataWarning)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        list += it.results
                    }
                }
            }
        }
    }

    private fun getResponse(
        index: MutableStateFlow<Int>,
        sortOptions: SortOptions
    ): LiveData<ListResponse> {
        return index.asLiveData().switchMap { page ->
            homeRepository.getSortedMovies(
                sortOptions = sortOptions,
                page = page,
                onStart = { isLoading = true },
                onSuccess = { isLoading = false },
                onError = { toast = it }
            ).asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        }
    }

    @MainThread
    fun fetchNextMovies(sortOptions: SortOptions) {
        if (!isLoading) {
            when (sortOptions) {
                SortOptions.POPULAR -> popularIndex.value++
                SortOptions.TOP_RATED -> topRatedIndex.value++
                SortOptions.REVENUE -> revenueIndex.value++
                SortOptions.RELEASE_DATE -> releaseDateIndex.value++
            }
        }
    }

    fun isOnLast(sortOptions: SortOptions): Boolean {
        val result = when (sortOptions) {
            SortOptions.POPULAR -> popularListResponse.value?.page!! >= popularListResponse.value?.total_pages!!
            SortOptions.TOP_RATED -> topRatedListResponse.value?.page!! >= topRatedListResponse.value?.total_pages!!
            SortOptions.REVENUE -> revenueListResponse.value?.page!! >= revenueListResponse.value?.total_pages!!
            SortOptions.RELEASE_DATE -> releaseDateListResponse.value?.page!! >= releaseDateListResponse.value?.total_pages!!
        }
        if (result) {
            viewModelScope.launch(Dispatchers.Main) {
                eventChannel.send(Event.ShowNoDataWarning)
            }
        }
        return result
    }


    sealed class Event {
        object ShowNoDataWarning : Event()
    }
}