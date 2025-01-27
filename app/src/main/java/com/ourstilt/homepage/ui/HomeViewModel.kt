package com.ourstilt.homepage.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.homepage.data.HomeDataModel
import com.ourstilt.homepage.data.HomeRepository
import com.ourstilt.homepage.data.ShopPageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

    private val _homeActivityData = MutableLiveData<HomeDataModel>()
    val homeActivityData: MutableLiveData<HomeDataModel> = _homeActivityData

    fun getHomeActivityData(forceRefresh: Boolean = false) {
        launch {
            homeRepository.getHomeActivityData(forceRefresh).onStart { _loading.value = true }
                .onCompletion { _loading.value = false }.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            result.data.let { pageData ->
                                _homeActivityData.postValue(pageData)
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Error Code: ${result.errorCode}, Message: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            Timber.e("Loading...")
                        }
                    }
                }
        }
    }


    private val _shopPageData = MutableLiveData<ShopPageData>()
    val shopPageData: MutableLiveData<ShopPageData> = _shopPageData

    private val shopPageReLoading = MutableLiveData<Boolean>()


    fun getShopsListData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                shopPageReLoading.value = true
            }
            homeRepository.getShopListData(forceRefresh).onStart { _loading.value = true }
                .onCompletion { _loading.value = false }.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            if (forceRefresh) {
                                shopPageReLoading.value = false
                            }
                            result.data.let { pageData ->
                                _shopPageData.postValue(pageData)
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Error Code: ${result.errorCode}, Message: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            Timber.e("Loading...")
                        }
                    }
                }
            if (forceRefresh) {
                shopPageReLoading.value = false
            }
        }

    }
}