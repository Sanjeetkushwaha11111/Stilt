package com.ourstilt.homepage

import androidx.lifecycle.MutableLiveData
import com.ourstilt.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    val homeData = MutableLiveData<HomeDataModel>()


    fun getHomeActivityData() {
        launch {
            val imgUrl = "https://i.postimg.cc/9XDbsQHG/home.png"
            val tabsData = arrayListOf(
                TabData("Home", imgUrl, null),
                TabData("Home2", imgUrl, null),
                TabData("Home3", imgUrl, null),
                TabData("Home4", imgUrl, null)
            )
            val hd = HomeDataModel(
                null,
                null,
                0,
                false,
                tabsData,
                0,
                "https://i.postimg.cc/xqrc4RDF/home-top-bg.png",
                "What you want to eat today?"
            )

            delay(1000)
            homeData.postValue(hd)
        }
    }
}