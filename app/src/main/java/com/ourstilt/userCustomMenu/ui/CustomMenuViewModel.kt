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
            MenuItems("poha_123", "snacks", "Poha", null, "Easy Pizzy", "40"),
            MenuItems("chai_123", "snacks", "Chai", null, "Sweet and Desi", "12")
        )
        val lunchMenuItems = listOf(
            MenuItems("panner_123", "meal", "Panner", null, "Healthy and Tasty ", "80"),
            MenuItems("roti_123", "meal", "Roti", null, "Fill your appetite", "10"),
        )
        val eveningSnacks = listOf(
            MenuItems("sandwich_123", "snacks", "Sandwiches", null, "Light and Enough", "60"),
            MenuItems("pasta_123", "pasta", "Pasta", null, "Delightful", "50"),
        )

        val menusData = listOf(
            CustomMenus(
                "breakfast", "breakfast", "Breakfast", breakFastMenuItems, null, null, "52"
            ),
            CustomMenus("lunch", "lunch", "Lunch", lunchMenuItems, null, null, "90"),
            CustomMenus("dinner", "dinner", "Evening Snacks", eveningSnacks, null, null, "110"),
            CustomMenus("dinner", "dinner", "Evening Snacks", eveningSnacks, null, null, "110"),
            CustomMenus("dinner", "dinner", "Evening Snacks", eveningSnacks, null, null, "110"),
        )

        viewModelScope.launch {
            delay(2000)
            _customMenuData.postValue(menusData)
        }


    }

}