package com.ourstilt.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.common.loggableFormat
import com.ourstilt.search.data.SearchPageData
import com.ourstilt.search.data.SearchRepository
import com.ourstilt.search.data.SectionType
import com.ourstilt.search.data.TrendingItem
import com.ourstilt.search.data.TrendingSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    BaseViewModel() {

    private val _trendingSections = MutableLiveData<List<TrendingSection>>()
    val trendingSections: LiveData<List<TrendingSection>> = _trendingSections

    private val _searchPageData = MutableLiveData<SearchPageData>()
    val searchPageData: MutableLiveData<SearchPageData> = _searchPageData


    fun fetchTrendingSections() {
        val trendingSectionData = listOf(
            TrendingSection(
                id = "1",
                type = SectionType.CurrentlyTrending,
                title = "Currently Trending Items",
                items = listOf(
                    TrendingItem(id = "1", title = "Item 1", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "1", title = "Item 2", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "3", title = "Item 3", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "4", title = "Item 4", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "5", title = "Item 5", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "6", title = "Item 6", type = SectionType.CurrentlyTrending)
                )
            ), TrendingSection(
                id = "2",
                type = SectionType.MostOrdered,
                title = "Most Ordered Items",
                items = listOf(
                    TrendingItem(id = "1", title = "Item 1", type = SectionType.MostOrdered),
                    TrendingItem(id = "2", title = "Item 2", type = SectionType.MostOrdered),
                    TrendingItem(id = "3", title = "Item 3", type = SectionType.MostOrdered),
                    TrendingItem(id = "4", title = "Item 4", type = SectionType.MostOrdered),
                    TrendingItem(id = "5", title = "Item 5", type = SectionType.MostOrdered),
                    TrendingItem(id = "6", title = "Item 6", type = SectionType.MostOrdered)
                )
            )
        )

        viewModelScope.launch {
            delay(1000)
            _trendingSections.postValue(trendingSectionData)
        }
    }


    fun searchPageData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            searchRepository.getSearchPageData(forceRefresh).onStart {
                _loading.value = true
            }.onCompletion {
                _loading.value = false
            }.collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        Timber.e(">>>>>>>>Error Code: ${result.errorCode}, Message: ${result.message}")
                    }

                    is NetworkResult.Loading -> {
                        Timber.e(">>>>>>>Loading...")
                    }

                    is NetworkResult.Success -> {
                        result.let {
                            _searchPageData.postValue(it.data)
                        }
                    }
                }
            }
        }
    }

}