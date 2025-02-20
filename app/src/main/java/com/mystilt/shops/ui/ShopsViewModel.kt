package com.mystilt.shops.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.ui.BaseViewModel
import com.mystilt.shops.data.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(private val shopRepository: ShopRepository) :
    BaseViewModel() {

    private val _shopPageData = MutableLiveData<ShopPageModel>()
    val shopPageData: MutableLiveData<ShopPageModel> = _shopPageData

    val pageReLoading = MutableLiveData<Boolean>()

    fun getShopPageData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                pageReLoading.value = true
            }
            shopRepository.getShopPageData(forceRefresh).onStart { _loading.value = true }
                .onCompletion { _loading.value = false }.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            if (forceRefresh) {
                                pageReLoading.value = false
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
                pageReLoading.value = false
            }
        }
    }

}
