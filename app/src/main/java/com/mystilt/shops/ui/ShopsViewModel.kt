package com.mystilt.shops.ui

import com.mystilt.base.ui.BaseViewModel
import com.mystilt.shops.data.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(private val shopRepository: ShopRepository) :
    BaseViewModel() {

}
