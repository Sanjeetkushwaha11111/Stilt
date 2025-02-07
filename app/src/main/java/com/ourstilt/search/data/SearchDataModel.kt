package com.ourstilt.search.data

import android.os.Parcelable
import com.ourstilt.homepage.data.TabData
import kotlinx.parcelize.Parcelize

enum class SectionType {
    ALL_ITEMS, MOST_ORDERED
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
    var trendingSections: List<Section>? = null
) : Parcelable


@Parcelize
data class Section(
    val sectionId: String? = null,
    val sectionType: SectionType? = null,
    val sectionTitle: String? = null,
    val sectionBg: String? = null,
    val sectionItems: List<SubSection>? = null
) : Parcelable


@Parcelize
data class SubSection(
    val subSectionId: String? = null,
    val subSectionTitle: String? = null,
    val subSectionSlug: String? = null,
    val sectionType: SectionType? = null,
    val subSectionBg: SectionType? = null,
    val subSectionItems: List<SubSectionItem>? = null
) : Parcelable


@Parcelize
data class SubSectionItem(
    val id: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val bg: String? = null,
    val imageUrl: String? = null
) : Parcelable
