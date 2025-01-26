package com.ourstilt.homepage.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ShopPageData(
    @SerializedName("welcomeText") var welcomeText: String? = null,
    @SerializedName("shops") var shops: List<Shop>? = null
) : Parcelable

@Parcelize
data class Shop(
    val id: String? = null,
    val slug: String? = null,
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val cuisine: List<String>? = null,
    val address: Address? = null,
    val contact: Contact? = null,
    val operationalDetails: OperationalDetails? = null,
    val reviews: List<Review>? = null,
    val offers: String? = null,
    val analytics: Analytics? = null,
    val services: Services? = null,
    val rating: Double? = null,
    val shopMainImages: List<String>? = null
) : Parcelable

@Parcelize
data class Address(
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable

@Parcelize
data class Contact(
    val phone: String? = null, val email: String? = null, val website: String? = null
) : Parcelable

@Parcelize
data class OperationalDetails(
    val openingHours: Map<String, String>? = null,
    val isOpen: Boolean? = null,
    val holidays: List<Holiday>? = null
) : Parcelable

@Parcelize
data class Holiday(
    val date: String? = null, val reason: String? = null
) : Parcelable

@Parcelize
data class Review(
    val userId: String? = null, // Changed to camelCase
    val userName: String? = null,
    val rating: Double? = null,
    val comment: String? = null,
    val reactionCount: String? = null // Changed to camelCase
) : Parcelable

@Parcelize
data class Analytics(
    val averageMonthlyOrders: Int? = null,
    val averageOrderValue: Double? = null,
    val customerRetentionRate: Double? = null,
    val peakHours: List<String>? = null,
    val dailyRevenue: Map<String, Double>? = null,
    val topPerformingCategories: List<String>? = null,
    val topSellingItems: List<TopSellingItem>? = null
) : Parcelable

@Parcelize
data class TopSellingItem(
    val name: String? = null, val orders: Int? = null
) : Parcelable

@Parcelize
data class Services(
    val delivery: Delivery? = null, val pickup: Pickup? = null
) : Parcelable

@Parcelize
data class Delivery(
    val isAvailable: Boolean? = null,
    val deliveryRadiusInKm: Int? = null,
    val averageDeliveryTimeInMinutes: Int? = null,
    val partners: List<String>? = null
) : Parcelable

@Parcelize
data class Pickup(
    val isAvailable: Boolean? = null, val pickupInstructions: String? = null
) : Parcelable
