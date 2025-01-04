package com.ourstilt.homepage

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class HomeDataModel(
    var data: MutableList<ApiData>? = null,
    var message: String? = null,
    var code: Int = 0,
    var status: Boolean = false,
    var tabsData: ArrayList<TabData>? = null,
    var tabToLand: Int? = null,
    var homeTopBg: String? = null,
    var welcomeText: String? = null,
)


@Parcelize
data class TabData(
    var tabName: String? = null,
    var tabImage: String? = null,
    var badgeCount: String? = null,
) : Parcelable


data class ApiData(
    var type: String? = null
)
