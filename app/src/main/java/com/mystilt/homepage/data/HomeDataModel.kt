package com.mystilt.homepage.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class HomeDataModel(
    var tabsData: ArrayList<TabData>? = null,
    var data: MutableList<ApiData>? = null,
    var tabToLand: Int? = null,
    var topTheming:HomeTopTheming?=null,
    var homeTopBg: String? = null,
    var homeTopItems: ArrayList<HomeTopItem>? = null,
    var shrinkDelay: Long = 2000,
    var expansionDelay: Long = 100,
    var targetExpansionHeight: Int = 500,
    var targetShrinkHeight: Int = 300,
    var welcomeText: String? = null,
)


@Parcelize
data class HomeTopTheming(
    var locationImg: String? = null,
    var nameToShow: String? = null,
    var addressToShow: String? = null,
    var userImg: String? = null,
    var optionalToShow: String? = null,
    var searchBoxFloating: SearchBoxTheming? = null,
    var searchBoxPinned: SearchBoxTheming? = null,
) : Parcelable


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

@Parcelize
data class CustomText(
    var id: String? = null,
    var text: String? = null,
    var size: String? = null,
    var color: String? = null,
    var fontWeight: String?=null
) : Parcelable


@Parcelize
data class SearchBoxTheming(
    var id: String? = null,
    var bgColor: String = "#111111",
    var barBgColor: String="#FFFFFF",
    var barRadius: Float=8F,
    var barBorderColor:String="#FFFFFF",
    var barBorderWidth:Int=2,
    var hints: ArrayList<String> = arrayListOf<String>("Search restaurants...", "Find dishes...", "Discover new places..."),
    var hintColor: String = "#111111",
    var fontWeight: String?=null
) : Parcelable

