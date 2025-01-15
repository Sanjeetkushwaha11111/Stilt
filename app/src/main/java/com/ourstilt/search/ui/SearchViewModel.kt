package com.ourstilt.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.homepage.data.TabData
import com.ourstilt.search.data.SearchPageData
import com.ourstilt.search.data.TrendingItem
import com.ourstilt.search.data.TrendingSection
import com.ourstilt.search.data.TrendingSubItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel : BaseViewModel() {

    private val _trendingSections = MutableLiveData<List<TrendingSection>>()
    val trendingSections: LiveData<List<TrendingSection>> = _trendingSections

    val searchPageData = MutableLiveData<SearchPageData>()


    fun fetchTrendingSections() {

        val trendingSectionData = listOf(
            TrendingSection(
                id = "1", type = "trending_one", title = "Trending in JMD", items = listOf(
                    TrendingItem(
                        id = "1", title = "JMD Category 1", slug = "tjmd", subItems = listOf(
                            TrendingSubItem("1", "JMD Sub Item 1"),
                            TrendingSubItem("2", "JMD Sub Item 2")
                        )
                    ), TrendingItem(
                        id = "2", title = "JMD Category 2", slug = "tjmd", subItems = listOf(
                            TrendingSubItem("3", "JMD Sub Item 3"),
                            TrendingSubItem("4", "JMD Sub Item 4")
                        )
                    )
                )
            ), TrendingSection(
                id = "2", type = "trending_two", title = "Trending in Your Mind", items = listOf(
                    TrendingItem(
                        id = "3", title = "Mind Category 1", slug = "tmind", subItems = listOf(
                            TrendingSubItem("5", "Mind Sub Item 1"),
                            TrendingSubItem("6", "Mind Sub Item 2")
                        )
                    ),
                    TrendingItem(
                        id = "4", title = "Mind Category 1", slug = "tmind", subItems = listOf(
                            TrendingSubItem("5", "Mind Sub Item 1"),
                            TrendingSubItem("6", "Mind Sub Item 2")
                        )
                    ),
                )
            )
        )

        viewModelScope.launch {
            _trendingSections.postValue(trendingSectionData)
        }
    }


    fun searchPageData() {
        val searchPageTabs = arrayListOf(
            TabData("1", "Trending", "", null), TabData("2", "Categories", "", null)
        )
        val sPData = SearchPageData(searchPageTabs, "Search to eat...", "1")
        viewModelScope.launch {
            delay(3000)
            searchPageData.postValue(sPData)
        }
    }


}