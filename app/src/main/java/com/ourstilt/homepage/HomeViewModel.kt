package com.ourstilt.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.BaseViewModel
import com.ourstilt.homepage.data.HomeDataModel
import com.ourstilt.homepage.data.SearchPageData
import com.ourstilt.homepage.data.TabData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    val homeData = MutableLiveData<HomeDataModel>()
    val homeFragmentData = MutableLiveData<HomeDataModel>()
    val searchPageData = MutableLiveData<SearchPageData>()


    fun getHomeActivityData() {
        launch {
            val imgUrl = "https://i.postimg.cc/9XDbsQHG/home.png"
            val topUrl=""
            val tabsData = arrayListOf(
                TabData("1", "Home", imgUrl, null),
                TabData("2", "Home2", imgUrl, null),
                TabData("3", "Home3", imgUrl, null),
                TabData("4", "Home4", imgUrl, null)
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


    fun searchPageData() {
        val searchPageTabs =
            arrayListOf(
                TabData("2", "Most Ordered", "", null),
                TabData("1", "Categories", "", null)
            )
        val sPData = SearchPageData(searchPageTabs, "Search to eat...", "0")
        viewModelScope.launch {
            delay(3000)
            searchPageData.postValue(sPData)
        }
    }
}