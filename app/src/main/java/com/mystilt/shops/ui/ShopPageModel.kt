package com.mystilt.shops.ui

import com.google.gson.annotations.SerializedName

data class ShopPageModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("open_hours") val openHours: String? = null,
    @SerializedName("cuisine_types") val cuisineTypes: List<String>? = null,
    @SerializedName("platform") val platform: String? = null,
    @SerializedName("order_link") val orderLink: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("review_count") val reviewCount: Int? = null,
    @SerializedName("menu") val menu: MenuData? = null
)


data class MenuData(
    @SerializedName("categories") val categories: List<MenuCategory>? = null
)

data class MenuCategory(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("has_subcategories") val hasSubcategories: Boolean? = null,
    @SerializedName("subcategories") val subcategories: List<MenuSubCategory>? = null,
    @SerializedName("items") val items: List<MenuItem>? = null,
    @SerializedName("expanded") var expanded: Boolean? = null,
    @SerializedName("view_orientation") val viewOrientation: String? = null,

)

data class MenuSubCategory(
    @SerializedName("id") val id: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("items") val items: List<MenuItem>? = null,
    @SerializedName("expanded") var expanded: Boolean? = null,
    @SerializedName("view_orientation") val viewOrientation: String? = null,
)

data class MenuItem(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("availability") val availability: Boolean? = null,
    @SerializedName("stock_status") val stockStatus: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("customization_options") val customizationOptions: List<String>? = null,
    @SerializedName("preparation_time") val preparationTime: Int? = null,
    @SerializedName("is_recommended") val isRecommended: Boolean? = null,
    @SerializedName("is_spicy") val isSpicy: Boolean? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("view_orientation") val viewOrientation: String? = null,
)
