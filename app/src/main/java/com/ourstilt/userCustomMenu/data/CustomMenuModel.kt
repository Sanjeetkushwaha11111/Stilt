package com.ourstilt.userCustomMenu.data

import com.ourstilt.homepage.data.ApiData

data class CustomMenuModel(
    var data: MutableList<ApiData>? = null,
    var message: String? = null,
    var code: Int = 0,
    var status: Boolean = false,
    var homeTopBg: String? = null,
    var welcomeText: String? = null,
    var menus: List<CustomMenus>? = null
)

data class CustomMenus(
    var slug: String? = null,
    var type: String? = null,
    var menuName: String? = null,
    var menuItems: List<MenuItems>? = null,
    var menuDescription: String? = null,
    var orderTimeIST: String? = null,
    var orderCount: String? = null,
    var menuTotalPrice: String? = null
)

data class MenuItems(
    var slug: String? = null,
    var type: String? = null,
    var foodName: String? = null,
    var foodImg: String? = null,
    var foodDescription: String? = null,
    var foodPrice: String? = null
)
