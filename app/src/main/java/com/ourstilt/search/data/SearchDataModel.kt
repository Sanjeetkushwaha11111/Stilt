package com.ourstilt.search.data

import android.os.Parcelable
import com.ourstilt.homepage.data.TabData
import kotlinx.parcelize.Parcelize

enum class SectionType {
    CurrentlyTrending, MostOrdered
}

@Parcelize
data class SearchPageData(
    var tabsData: List<TabData>? = null,
    var searchBarHint: String? = null,
    var tabToShow: String? = null,
    var trendingSection: TrendingPageData? = null
) : Parcelable

@Parcelize
data class TrendingPageData(
    var trendingSections: List<TrendingSection>? = null
) : Parcelable


@Parcelize
data class TrendingSection(
    val id: String? = null,
    val type: SectionType? = null,
    val title: String? = null,
    val items: List<TrendingItem>? = null
) : Parcelable


@Parcelize
data class TrendingItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val type: SectionType? = null,
    val subItems: List<TrendingSubItem>? = null
) : Parcelable


@Parcelize
data class TrendingSubItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val imageUrl: String? = null
) : Parcelable
