package com.ourstilt.homepage.data

data class SearchDataModel(
    var data: MutableList<SearchPageData>? = null,
    var message: String? = null,
    var code: Int = 0,
    var status: Boolean = false,
)

data class SearchPageData(
    var tabsData: ArrayList<TabData>? = null,
    var searchBarHint: String? = null,
    var tabToShow: String? = null
)
