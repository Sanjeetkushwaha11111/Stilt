package com.ourstilt.base

interface BaseViewInterface {

    fun onRetry()
    fun showLoading()
    fun hideLoading()
    fun showBottomLoader()
    fun hideBottomLoader()
    fun showToast(message: String?)
    fun onHomePressed()
    fun onHomeLongPressed()
    fun onBackPressed()
    fun onScrollHide()
    fun onScrollShow()
    //fun showPopup(popup: Banner?, screenName: String)
    fun hidePopup()
}