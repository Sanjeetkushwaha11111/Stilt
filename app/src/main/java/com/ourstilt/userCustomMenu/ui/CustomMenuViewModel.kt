package com.ourstilt.userCustomMenu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.userCustomMenu.data.CustomMenus
import com.ourstilt.userCustomMenu.data.MenuItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomMenuViewModel : BaseViewModel() {


    private val _customMenuData = MutableLiveData<List<CustomMenus>>()
    val customMenuData: LiveData<List<CustomMenus>> = _customMenuData


    fun getMenusData() {
        val breakFastMenuItems = listOf(
            MenuItems(
                "poha_123", "snacks", "Poha", null, "Healthy breakfast and Delicious", "40"
            ),
            MenuItems(
                "poha_123", "snacks", "Poha", null, "Healthy breakfast and Delicious", "40"
            ),
            MenuItems(
                "poha_123", "snacks", "Poha", null, "Healthy breakfast and Delicious", "40"
            ),
        )
        val lunchMenuItems = listOf(
            MenuItems(
                "panner_123", "meal", "Panner", null, "Healthy breakfast and Delicious", "80"
            ),
            MenuItems(
                "roti_123", "meal", "Roti", null, "Healthy breakfast and Delicious", "10"
            ),
        )
        val eveningSnacks = listOf(
            MenuItems(
                "sandwich_123",
                "snacks",
                "Sandwiches",
                null,
                "Healthy breakfast and Delicious",
                "60"
            ),
            MenuItems(
                "pasta_123", "pasta", "Pasta", null, "Healthy breakfast and Delicious", "50"
            ),
        )

        val menusData = listOf(
            CustomMenus(
                "breakfast",
                "breakfast",
                "Breakfast",
                breakFastMenuItems,
                "Healthy breakfast and Delicious breakfast  Delicious breakfast and Delicious",
                null,
                null,
                "52"
            ),
            CustomMenus(
                "lunch",
                "lunch",
                "Lunch",
                lunchMenuItems,
                "Healthy breakfast and Delicious breakfast  Delicious breakfast and Delicious",
                null,
                null,
                "90"
            ),
            CustomMenus(
                "dinner",
                "dinner",
                "Evening Snacks",
                eveningSnacks,
                "Healthy breakfast and Delicious breakfast  Delicious breakfast and Delicious",
                null,
                null,
                "110"
            ),
            CustomMenus(
                "anytime_eat",
                "anytime_eat",
                "Anytime Eat",
                eveningSnacks,
                "Healthy breakfast and Delicious breakfast  Delicious breakfast and Delicious",
                null,
                null,
                "110"
            ),
            CustomMenus(
                "healthy_one",
                "healthy_one",
                "Healthy One",
                eveningSnacks,
                "Healthy breakfast and Delicious breakfast  Delicious breakfast and Delicious",
                null,
                null,
                "110"
            ),
        )

        viewModelScope.launch {
            delay(2000)
            _customMenuData.postValue(menusData)
        }


    }

}