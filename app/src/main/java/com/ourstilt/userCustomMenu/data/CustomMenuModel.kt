package com.ourstilt.userCustomMenu.data

import com.google.gson.annotations.SerializedName

data class CustomMenuModel(
    @SerializedName("homeTopBg") var homeTopBg: String? = null,
    @SerializedName("welcomeText") var welcomeText: String? = null,
    @SerializedName("menus") var menus: List<CustomMenus>? = null
)

data class CustomMenus(
    @SerializedName("menu_slug") var slug: String? = null,
    @SerializedName("menu_type") var menuType: String? = null,
    @SerializedName("menu_name") var menuName: String? = null,
    @SerializedName("menu_items") var menuItems: List<MenuItems> = emptyList(),
    @SerializedName("menu_description") var menuDescription: String? = null,
    @SerializedName("menu_order_ist") var orderTimeIST: String? = null,
    @SerializedName("menu_order_count") var orderCount: Int = 0,
    @SerializedName("menu_total_price") var menuTotalPrice: Double = 0.0
)

data class MenuItems(
    @SerializedName("menu_item_slug") var itemSlug: String? = null,
    @SerializedName("menu_item_type") var itemMenuType: String? = null,
    @SerializedName("menu_item_name") var foodName: String? = null,
    @SerializedName("menu_item_image") var foodImg: String? = null,
    @SerializedName("menu_item_description") var foodDescription: String? = null,
    @SerializedName("menu_item_price") var foodPrice: Double = 0.0,
    @SerializedName("menu_item_quantity") var itemOrderCount: Int = 0
)


data class MenuState(
    var menuSlug: String,
    var totalPrice: Double = 0.0,
    var totalItemCount: Int = 0,
    var itemCounts: MutableMap<String, Int> = mutableMapOf(),
    var itemPrices: MutableMap<String, Double> = mutableMapOf(),
    var itemSlug: MutableMap<String, String?> = mutableMapOf(),
)