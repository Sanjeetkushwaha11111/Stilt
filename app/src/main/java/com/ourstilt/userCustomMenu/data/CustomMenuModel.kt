package com.ourstilt.userCustomMenu.data

import com.ourstilt.homepage.data.ApiData

data class CustomMenuModel(
    var data: MutableList<ApiData>? = null,
    var message: String? = null,
    var code: Int = 0,
    var status: Boolean = false,
    var homeTopBg: String? = null,
    var welcomeText: String? = null,
    var menus: List<CustomMenus> = emptyList()
)

data class CustomMenus(
    var slug: String? = null,
    var type: String? = null,
    var menuName: String? = null,
    var menuItems: List<MenuItems> = emptyList(),
    var menuDescription: String? = null,
    var orderTimeIST: String? = null,
    var orderCount: Int = 0,
    var menuTotalPrice: Double = 0.0
)

data class MenuItems(
    var slug: String? = null,
    var type: String? = null,
    var foodName: String? = null,
    var foodImg: String? = null,
    var foodDescription: String? = null, var foodPrice: Double = 0.0, var itemOrderCount: Int = 0
)


data class MenuState(
    var menuSlug: String,
    var totalPrice: Double = 0.0,
    var totalItemCount: Int = 0,
    var itemCounts: MutableMap<String, Int> = mutableMapOf(),
    var itemPrices: MutableMap<String, Double> = mutableMapOf()
)