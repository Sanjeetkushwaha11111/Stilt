package com.ourstilt.userlogin.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetails(
    @SerializedName("user_id") val userId: Int? = null,

    @SerializedName("user_name") val userName: String? = null,

    @SerializedName("user_email") val userEmail: String? = null,

    @SerializedName("user_type") val userUserType: String? = null,

    @SerializedName("user_phone") val userPhone: String? = null,

    @SerializedName("user_avatar") val userAvatar: String? = null,

    @SerializedName("hash_id") val userHashId: String? = null,

    @SerializedName("token") val userToken: String? = null,

    @SerializedName("user_unit_number") val userUnitNumber: String? = null,

    @SerializedName("user_floor") val userFloor: String? = null,

    @SerializedName("user_location") val userLocation: String? = null,

    @SerializedName("user_location_icon") val userLocationIcon: String? = null,

    @SerializedName("is_phone_verified") val userIsPhoneVerified: Int? = null,

    @SerializedName("is_subscribed") val userIsSubscribed: Boolean = false,

    @SerializedName("create_profile") val userIsCreateProfile: Boolean = false,

    @SerializedName("show_review_popup") val userIsShowReviewPopup: Boolean = false,

    @SerializedName("notification_count") val userNotificationCount: Int? = null,

    @SerializedName("gender") val userGender:  String? = null,

    @SerializedName("dob") val userDob:  String? = null,
) : Parcelable
