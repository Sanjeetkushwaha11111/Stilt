package com.ourstilt.search.data

import com.ourstilt.homepage.data.TabData

data class SearchDataModel(
    var data: List<SearchPageData?>? = null,
    var message: String? = null,
    var code: Int? = null,
    var status: Boolean? = null,
)

data class SearchPageData(
    var tabsData: List<TabData>? = null,
    var searchBarHint: String? = null,
    var tabToShow: String? = null,
    var trendingSection: List<TrendingSection?>? = null
)

data class TrendingSection(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val items: List<TrendingItem?>? = null,
    val slug: String? = null,
)

data class TrendingItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val subItems: List<TrendingSubItem?>? = null
)

data class TrendingSubItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val imageUrl: String? = null
)
