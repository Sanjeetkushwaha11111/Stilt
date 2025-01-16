package com.ourstilt.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.homepage.data.TabData
import com.ourstilt.search.data.SearchPageData
import com.ourstilt.search.data.SectionType
import com.ourstilt.search.data.TrendingItem
import com.ourstilt.search.data.TrendingSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel : BaseViewModel() {

    private val _trendingSections = MutableLiveData<List<TrendingSection>>()
    val trendingSections: LiveData<List<TrendingSection>> = _trendingSections

    val searchPageData = MutableLiveData<SearchPageData>()


    fun fetchTrendingSections() {
        val trendingSectionData = listOf(
            TrendingSection(
                id = "1",
                type = SectionType.CurrentlyTrending,
                title = "Currently Trending Items",
                items = listOf(
                    TrendingItem(id = "1", title = "Item 1", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "1", title = "Item 2", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "3", title = "Item 3", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "4", title = "Item 4", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "5", title = "Item 5", type = SectionType.CurrentlyTrending),
                    TrendingItem(id = "6", title = "Item 6", type = SectionType.CurrentlyTrending)
                )
            ), TrendingSection(
                id = "2",
                type = SectionType.MostOrdered,
                title = "Most Ordered Items",
                items = listOf(
                    TrendingItem(id = "1", title = "Item 1", type = SectionType.MostOrdered),
                    TrendingItem(id = "2", title = "Item 2", type = SectionType.MostOrdered),
                    TrendingItem(id = "3", title = "Item 3", type = SectionType.MostOrdered),
                    TrendingItem(id = "4", title = "Item 4", type = SectionType.MostOrdered),
                    TrendingItem(id = "5", title = "Item 5", type = SectionType.MostOrdered),
                    TrendingItem(id = "6", title = "Item 6", type = SectionType.MostOrdered)
                )
            )
        )

        viewModelScope.launch {
            delay(1000)
            _trendingSections.postValue(trendingSectionData)
        }
    }


    fun searchPageData() {
        val searchPageTabs = arrayListOf(
            TabData("1", "Trending", "", null), TabData("2", "Categories", "", null)
        )
        val sPData = SearchPageData(searchPageTabs, "Search to eat...", "1")
        viewModelScope.launch {
            delay(2000)
            searchPageData.postValue(sPData)
        }
    }


}