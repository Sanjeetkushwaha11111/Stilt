package com.mystilt.homepage.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class HomeDataModel(
    var tabsData: ArrayList<TabData>? = null,
    var data: MutableList<ApiData>? = null,
    var tabToLand: Int? = null,
    var homeTopBg: String? = null,
    var homeTopItems: ArrayList<HomeTopItem>? = null,
    var shrinkDelay: Long = 2000,
    var expansionDelay: Long = 100,
    var targetExpansionHeight: Int = 500,
    var targetShrinkHeight: Int = 300,
    var welcomeText: String? = null,
)


@Parcelize
data class TabData(
    var tabSlug: String? = null,
    var tabName: String? = null,
    var tabImage: String? = null,
    var badgeCount: String? = null,
) : Parcelable


@Parcelize
data class HomeTopItem(
    var itemSlug: String? = null,
    var itemImg: String? = null,
    var itemText: String? = null
) : Parcelable


data class ApiData(
    var type: String? = null,
    var filterData: ArrayList<FilterData>? = null
)

@Parcelize
data class FilterData(
    var slug: String? = null,
    var text: String? = null,
    var icon: String? = null,
    var showCross: Boolean? = false
) : Parcelable