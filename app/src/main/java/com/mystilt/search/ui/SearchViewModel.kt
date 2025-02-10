package com.mystilt.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.ui.BaseViewModel
import com.mystilt.search.data.SearchPageData
import com.mystilt.search.data.SearchRepository
import com.mystilt.search.data.TrendingPageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    BaseViewModel() {

    private val _trendingPageData = MutableLiveData<TrendingPageData>()
    val trendingPageData: LiveData<TrendingPageData> = _trendingPageData

    private val _searchPageData = MutableLiveData<SearchPageData>()
    val searchPageData: MutableLiveData<SearchPageData> = _searchPageData


    fun getTrendingPageData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            searchRepository.getTrendingPageData(forceRefresh).onStart {
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
                        result.let { it ->
                            _trendingPageData.postValue(it.data)
                        }
                    }
                }
            }
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
                        result.let { it ->
                            _searchPageData.postValue(it.data)
                        }
                    }
                }
            }
        }
    }

    }
