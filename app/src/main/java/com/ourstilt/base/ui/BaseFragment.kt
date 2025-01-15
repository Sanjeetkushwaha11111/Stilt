package com.ourstilt.base.ui

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), BaseViewInterface {

    private var baseView: BaseViewInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseViewInterface) {
            baseView = context
        }
    }

    override fun showLoading() {
        baseView?.showLoading()
    }

    override fun hideLoading() {
        baseView?.hideLoading()
    }

    override fun showToast(message: String?) {
        baseView?.showToast(message)
    }

    override fun onRetry() {
        baseView?.onRetry()
    }

    override fun onHomeLongPressed() {
        baseView?.onHomeLongPressed()
    }

    override fun onHomePressed() {
        baseView?.onHomePressed()
    }

    override fun onBackPressed() {
        baseView?.onBackPressed()
    }

    override fun onScrollShow() {
        baseView?.onScrollShow()
    }

    override fun onScrollHide() {
        baseView?.onScrollHide()
    }

    override fun showBottomLoader() {
        baseView?.showBottomLoader()
    }

    override fun hideBottomLoader() {
        baseView?.hideBottomLoader()
    }

/*
    override fun showPopup(popup: HomeLiveCourseResponse.Banner?, screenName: String) {
        baseView?.showPopup(popup,screenName)
    }
*/

    override fun hidePopup() {
        baseView?.hidePopup()
    }

//    override fun showFullScreenLoaderWithQuotes(b: Boolean) {
//        baseView?.showFullScreenLoaderWithQuotes(b)
//    }
}