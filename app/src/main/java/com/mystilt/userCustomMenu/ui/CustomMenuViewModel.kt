package com.mystilt.userCustomMenu.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.ui.BaseViewModel
import com.mystilt.userCustomMenu.data.CustomMenuModel
import com.mystilt.userCustomMenu.data.CustomMenuRepository
import com.mystilt.userCustomMenu.data.CustomMenus
import com.mystilt.userCustomMenu.data.MenuItems
import com.mystilt.userCustomMenu.data.MenuState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class CustomMenuViewModel @Inject constructor(
    private val customMenuRepository: CustomMenuRepository
) : BaseViewModel() {

    // contains all list of menus in data keep updating lists on changing
    private val _menuList = MutableLiveData<List<CustomMenus>>()
    val menuList: MutableLiveData<List<CustomMenus>> = _menuList

    // contains whole page data of custom menu
    private val _customMenuPageData = MutableLiveData<CustomMenuModel>()
    val customMenuPageData: MutableLiveData<CustomMenuModel> = _customMenuPageData

    val pageReLoading = MutableLiveData<Boolean>()

    fun getMenuPageData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                pageReLoading.value = true
            }
            customMenuRepository.getMenuPageData(forceRefresh).onStart { _loading.value = true }
                .onCompletion { _loading.value = false }.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            if (forceRefresh) {
                                pageReLoading.value = false
                            }
                            result.data.let { pageData ->
                                _customMenuPageData.postValue(pageData)
                                pageData.menus?.let { menus ->
                                    _menuList.postValue(menus)
                                    initializeMenuStates(menus)
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            Timber.e("Error Code: ${result.errorCode}, Message: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            Timber.e("Loading...")
                        }
                    }
                }
            if(forceRefresh){
                pageReLoading.value=false
            }
        }
    }


    // to handel the changing states of menus item and prices mapping into changing values and remap when done
    private val _menuStates = MutableLiveData<Map<String, MenuState>?>()
    val menuStates: MutableLiveData<Map<String, MenuState>?> = _menuStates

    private fun initializeMenuStates(menus: List<CustomMenus>) {
        val initialStates = menus.associate { menu ->
            val menuSlug = menu.slug ?: return@associate null to null

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

    fun addMenuItem(menuSlug: String, newItem: MenuItems) {
        val currentMenus = _menuList.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.map { menu ->
            if (menu.slug == menuSlug) {
                val updatedItems = menu.menuItems.toMutableList()
                updatedItems.add(newItem)
                menu.copy(menuItems = updatedItems)
            } else {
                menu
            }
        }
        _menuList.postValue(updatedMenus)
        updateMenuStateAfterAdd(menuSlug, newItem)
    }

    fun removeMenuItem(menuSlug: String, itemSlug: String) {
        val currentMenus = _menuList.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.map { menu ->
            if (menu.slug == menuSlug) {
                val updatedItems = menu.menuItems.filterNot { it.itemSlug == itemSlug }
                menu.copy(menuItems = updatedItems)
            } else {
                menu
            }
        }
        _menuList.postValue(updatedMenus)
        updateMenuStateAfterRemove(menuSlug, itemSlug)
    }

    fun removeMenu(menuSlug: String) {
        val currentMenus = _menuList.value?.toMutableList() ?: return
        val updatedMenus = currentMenus.filterNot { it.slug == menuSlug }
        _menuList.postValue(updatedMenus)

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

    private fun getUpdatedMenuDetails(menuSlug: String): CustomMenus? {
        val menu = _menuList.value?.find { it.slug == menuSlug }
        if (menu == null) return null
        val menuState = _menuStates.value?.get(menuSlug)
        val updatedMenuItems = menu.menuItems.map { menuItem ->
            val itemCount = menuState?.itemCounts?.get(menuItem.itemSlug) ?: menuItem.itemOrderCount
            val itemPrice = menuState?.itemPrices?.get(menuItem.itemSlug) ?: menuItem.foodPrice
            menuItem.copy(itemOrderCount = itemCount, foodPrice = itemPrice)
        }
        return menu.copy(
            menuItems = updatedMenuItems,
            menuTotalPrice = menuState?.totalPrice ?: menu.menuTotalPrice,
            orderCount = menuState?.totalItemCount ?: menu.orderCount
        )
    }

    fun getMenuBySlug(menuSlug: String): CustomMenus? {
        val menu = getUpdatedMenuDetails(menuSlug)
        return menu
    }

    fun orderFood(customMenus: CustomMenus, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            customMenuRepository.placeFoodOrder(customMenus, forceRefresh)
                .onStart { _loading.value = true }.onCompletion { _loading.value = false }
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            Timber.d("Order placed successfully: ${result.data}")
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Error Code: ${result.errorCode}, Message: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            Timber.d("Loading...")
                        }
                    }
                }
        }
    }
}