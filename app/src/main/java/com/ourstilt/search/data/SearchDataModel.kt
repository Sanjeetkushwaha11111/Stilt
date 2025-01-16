package com.ourstilt.search.data

import com.ourstilt.homepage.data.TabData

sealed class SectionType {
    data object CurrentlyTrending : SectionType()
    data object MostOrdered : SectionType()
}

data class SearchDataModel(
    var data: List<SearchPageData?>? = null,
    var message: String? = null,
    var code: Int? = null,
    var status: Boolean? = null,
)

data class SearchPageData(
    var tabsData: List<TabData>? = null,
    var searchBarHint: String? = null,
    var tabToShow: String? = null, var trendingSection: TrendingPageData? = null
)

data class TrendingPageData(
    var trendingSections: List<TrendingSection>? = null
)

data class TrendingSection(
    val id: String? = null, val type: SectionType? = null,
    val title: String? = null, val items: List<TrendingItem>? = null
)

data class TrendingItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null, val type: SectionType, val subItems: List<TrendingSubItem>? = null
)


data class TrendingSubItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val imageUrl: String? = null
)
