package com.ourstilt.homepage.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.homepage.data.HomeDataModel
import com.ourstilt.homepage.data.HomeRepository
import com.ourstilt.homepage.data.ShopPageData
import com.ourstilt.homepage.data.TabData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

    val homeData = MutableLiveData<HomeDataModel>()
    val homeFragmentData = MutableLiveData<HomeDataModel>()


    fun getHomeActivityData() {
        launch {
            val imgUrl = "https://i.postimg.cc/9XDbsQHG/home.png"
            val topUrl=""
            val tabsData = arrayListOf(
                TabData("1", "Home", imgUrl, null),
                TabData("2", "Shops", imgUrl, null),
                TabData("3", "Daily Bite", imgUrl, null),
            )
            val hd = HomeDataModel(
                null,
                null,
                0,
                false,
                tabsData,
                1,
                "https://i.postimg.cc/xqrc4RDF/home-top-bg.png",
                "<span style=\"color: black; font-weight: bold; font-size: 26px;\">What you want to eat today?</span>\n"
            )

            delay(1000)
            homeData.postValue(hd)
        }
    }

    fun getHomeFragmentData() {

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