package com.ourstilt.userCustomMenu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.userCustomMenu.data.CustomMenus
import com.ourstilt.userCustomMenu.data.MenuItems
import com.ourstilt.userCustomMenu.data.MenuState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomMenuViewModel : BaseViewModel() {
    private val _customMenuData = MutableLiveData<List<CustomMenus>>()
    val customMenuData: LiveData<List<CustomMenus>> = _customMenuData

    fun getMenusData() {
        val breakFastMenuItems = listOf(
            MenuItems(
                "poha_123", "snacks", "Poha", null, "Healthy breakfast and Delicious", 40.0, 1
            ),
            MenuItems(
                "chai_123", "snacks", "Chai", null, "Healthy breakfast and Delicious", 10.0, 1
            ),
            MenuItems(
                "omelette_123",
                "snacks",
                "Omelette",
                null,
                "Healthy breakfast and Delicious",
                40.0,
                1
            )
        )
        val lunchMenuItems = listOf(
            MenuItems("panner_123", "meal", "Panner", null, "Healthy meal and Delicious", 70.0, 1),
            MenuItems("roti_123", "meal", "Roti", null, "Healthy meal and Delicious", 10.0, 1)
        )
        val eveningSnacks = listOf(
            MenuItems(
                "sandwich_123",
                "snacks",
                "Sandwiches",
                null, "Healthy snacks and Delicious", 50.0, 1
            ),
            MenuItems("pasta_123", "pasta", "Pasta", null, "Healthy snacks and Delicious", 70.0, 1)
        )
        val menusData = listOf(
            CustomMenus(
                "breakfast",
                "breakfast",
                "Breakfast",
                breakFastMenuItems,
                "Healthy and delicious breakfast Healthy and delicious breakfast  Healthy and delicious breakfast ",
                null,
                0,
                90.0
            ),
            CustomMenus(
                "lunch",
                "lunch",
                "Lunch",
                lunchMenuItems,
                "Healthy and delicious breakfast Healthy and delicious breakfast  Healthy and delicious breakfast ",
                null,
                0,
                80.0
            ),
            CustomMenus(
                "dinner",
                "dinner",
                "Evening Snacks",
                eveningSnacks,
                "Healthy and delicious breakfast Healthy and delicious breakfast  Healthy and delicious breakfast ",
                null,
                0,
                120.0
            )
        )

        viewModelScope.launch {
            delay(2000)
            _customMenuData.postValue(menusData)
            initializeMenuStates(menusData)
        }
    }

    private val _menuStates = MutableLiveData<Map<String, MenuState>?>()
    val menuStates: MutableLiveData<Map<String, MenuState>?> = _menuStates


    private fun initializeMenuStates(menus: List<CustomMenus>) {
        val initialStates = menus.associate { menu ->
            val menuSlug = menu.slug ?: return@associate null to null // Skip if slug is null

            val itemCounts =
                menu.menuItems.associate { it.itemSlug!! to it.itemOrderCount }.toMutableMap()
            val itemPrices =
                menu.menuItems.associate { it.itemSlug!! to it.foodPrice }.toMutableMap()
            val itemSlug = menu.menuItems.associate { it.itemSlug!! to it.itemSlug }.toMutableMap()

            menuSlug to MenuState(
                menuSlug = menuSlug,
                totalPrice = menu.menuTotalPrice,
                totalItemCount = itemCounts.values.sum(),
                itemCounts = itemCounts,
                itemPrices = itemPrices,
                itemSlug = itemSlug
            )
        }.filterKeys { it != null } // Filter out any null keys

        _menuStates.postValue(initialStates.filterNotNullValues())
    }

    private fun <K, V> Map<K?, V?>.filterNotNullValues(): Map<K, V> {
        return this.filter { it.key != null && it.value != null } as Map<K, V>
    }

    fun updateItemCount(menuSlug: String, itemSlug: String, change: Int) {
        val currentState = _menuStates.value?.toMutableMap() ?: return
        val menuState = currentState[menuSlug] ?: return

        val currentCount = menuState.itemCounts[itemSlug] ?: 0
        val itemPrice = menuState.itemPrices[itemSlug] ?: 0.0

        val newCount = (currentCount + change).coerceAtLeast(0)

        menuState.itemCounts[itemSlug] = newCount

        val priceDifference = (newCount - currentCount) * itemPrice

        menuState.totalPrice += priceDifference

        var totalItemCount = 0
        menuState.itemCounts.values.forEach { totalItemCount += it }
        menuState.totalItemCount = totalItemCount

        menuState.totalPrice = menuState.itemCounts.entries.sumOf { (slug, count) ->
            val price = menuState.itemPrices[slug] ?: 0.0
            count * price
        }

        _menuStates.postValue(currentState)
    }

    // Function to add a new menu item to a specific menu
    fun addMenuItem(menuSlug: String, newItem: MenuItems) {
        val currentMenus = _customMenuData.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.map { menu ->
            if (menu.slug == menuSlug) {
                val updatedItems = menu.menuItems.toMutableList()
                updatedItems.add(newItem)
                menu.copy(menuItems = updatedItems)
            } else {
                menu
            }
        }
        _customMenuData.postValue(updatedMenus)
        updateMenuStateAfterAdd(menuSlug, newItem)
    }

    // Function to remove a menu item from a specific menu
    fun removeMenuItem(menuSlug: String, itemSlug: String) {
        val currentMenus = _customMenuData.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.map { menu ->
            if (menu.slug == menuSlug) {
                val updatedItems = menu.menuItems.filterNot { it.itemSlug == itemSlug }
                menu.copy(menuItems = updatedItems)
            } else {
                menu
            }
        }
        _customMenuData.postValue(updatedMenus)
        updateMenuStateAfterRemove(menuSlug, itemSlug)
    }

    // Function to remove an entire menu
    fun removeMenu(menuSlug: String) {
        val currentMenus = _customMenuData.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.filterNot { it.slug == menuSlug }
        _customMenuData.postValue(updatedMenus)

        // Also remove the menu state
        val currentState = _menuStates.value?.toMutableMap() ?: return
        currentState.remove(menuSlug)
        _menuStates.postValue(currentState)
    }

    private fun updateMenuStateAfterAdd(menuSlug: String, newItem: MenuItems) {
        val currentState = _menuStates.value?.toMutableMap() ?: return
        val menuState = currentState[menuSlug] ?: return

        val currentCount = menuState.itemCounts.getOrDefault(newItem.itemSlug, 0)
        val currentPrice = menuState.itemPrices.getOrDefault(newItem.itemSlug, 0.0)

        menuState.itemCounts[newItem.itemSlug!!] = currentCount + newItem.itemOrderCount
        menuState.itemPrices[newItem.itemSlug!!] = currentPrice + newItem.foodPrice
        menuState.totalPrice += newItem.foodPrice * newItem.itemOrderCount
        menuState.totalItemCount += newItem.itemOrderCount

        currentState[menuSlug] = menuState
        _menuStates.postValue(currentState)
    }

    private fun updateMenuStateAfterRemove(menuSlug: String, itemSlug: String) {
        val currentState = _menuStates.value?.toMutableMap() ?: return
        val menuState = currentState[menuSlug] ?: return

        val itemCount = menuState.itemCounts.remove(itemSlug)
        val itemPrice = menuState.itemPrices.remove(itemSlug)

        if (itemCount != null && itemPrice != null) {
            menuState.totalPrice -= itemCount * itemPrice
            menuState.totalItemCount -= itemCount
        }

        currentState[menuSlug] = menuState
        _menuStates.postValue(currentState)
    }

}