package com.ourstilt.customViews.animatedbottombar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ourstilt.R
import com.ourstilt.common.dpPx
import com.ourstilt.common.getColorResCompat
import com.ourstilt.common.getTextColor
import com.ourstilt.common.MenuParser
import com.ourstilt.common.NavigationComponentHelper
import com.ourstilt.common.Utils


class AnimatedBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var onTabSelectListener: OnTabSelectListener? = null
    private var onTabInterceptListener: OnTabInterceptListener? = null

    var onTabSelected: (Tab) -> Unit = {}
    var onTabReselected: (Tab) -> Unit = {}
    var onTabIntercepted: (Tab) -> Boolean = { true }

    internal val tabStyle = BottomBarStyle.Tab()
    internal val indicatorStyle = BottomBarStyle.Indicator()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TabAdapter
    private lateinit var tabIndicator: TabIndicator

    private var viewPager: ViewPager? = null
    private var viewPager2: ViewPager2? = null

    init {
        initRecyclerView()
        initAdapter()
        initTabIndicator()
        initAttributes(attrs)
    }

    private fun initAttributes(
        attributeSet: AttributeSet?
    ) {
        val textColorPrimary = context.getTextColor(android.R.attr.textColorPrimary)

        tabColorDisabled = context.getTextColor(android.R.attr.textColorSecondary)
        tabColor = textColorPrimary

        rippleColor = android.R.attr.selectableItemBackgroundBorderless

        val colorPrimary = context.getColorResCompat(android.R.attr.colorPrimary)

        tabColorSelected = colorPrimary
        indicatorColor = colorPrimary

        val attr: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.AnimatedBottomBar, 0, 0)
        try {
            // Type
            selectedTabType = TabType.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_selectedTabType,
                    tabStyle.selectedTabType.id
                )
            ) ?: tabStyle.selectedTabType

            // Animations
            tabAnimationSelected = TabAnimation.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_tabAnimationSelected,
                    tabStyle.tabAnimationSelected.id
                )
            ) ?: tabStyle.tabAnimationSelected
            tabAnimation = TabAnimation.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_tabAnimation,
                    tabStyle.tabAnimation.id
                )
            ) ?: tabStyle.tabAnimation
            animationDuration = attr.getInt(
                R.styleable.AnimatedBottomBar_abb_animationDuration,
                tabStyle.animationDuration
            )
            animationInterpolator = Utils.loadInterpolator(
                context, attr.getResourceId(
                    R.styleable.AnimatedBottomBar_abb_animationInterpolator,
                    -1
                ), tabStyle.animationInterpolator
            )

            // Ripple
            rippleEnabled = attr.getBoolean(
                R.styleable.AnimatedBottomBar_abb_rippleEnabled,
                tabStyle.rippleEnabled
            )
            rippleColor = attr.getColor(
                R.styleable.AnimatedBottomBar_abb_rippleColor,
                tabStyle.rippleColor
            )

            // Colors
            tabColorSelected = attr.getColor(
                R.styleable.AnimatedBottomBar_abb_tabColorSelected,
                tabStyle.tabColorSelected
            )
            tabColorDisabled = attr.getColor(
                R.styleable.AnimatedBottomBar_abb_tabColorDisabled,
                tabStyle.tabColorDisabled
            )
            tabColor =
                attr.getColor(R.styleable.AnimatedBottomBar_abb_tabColor, tabStyle.tabColor)

            // Text
            textAppearance =
                attr.getResourceId(
                    R.styleable.AnimatedBottomBar_abb_textAppearance,
                    tabStyle.textAppearance
                )
            val textStyle =
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_textStyle,
                    typeface.style
                )
            typeface = Typeface.create(typeface, textStyle)
            textSize =
                attr.getDimensionPixelSize(
                    R.styleable.AnimatedBottomBar_abb_textSize,
                    tabStyle.textSize
                )

            // Icon
            iconSize =
                attr.getDimension(
                    R.styleable.AnimatedBottomBar_abb_iconSize,
                    tabStyle.iconSize.toFloat()
                ).toInt()

            // Indicator
            indicatorHeight =
                attr.getDimension(
                    R.styleable.AnimatedBottomBar_abb_indicatorHeight,
                    indicatorStyle.indicatorHeight.toFloat()
                ).toInt()
            indicatorMargin =
                attr.getDimension(
                    R.styleable.AnimatedBottomBar_abb_indicatorMargin,
                    indicatorStyle.indicatorMargin.toFloat()
                ).toInt()
            indicatorColor =
                attr.getColor(
                    R.styleable.AnimatedBottomBar_abb_indicatorColor,
                    indicatorStyle.indicatorColor
                )
            indicatorAppearance = IndicatorAppearance.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_indicatorAppearance,
                    indicatorStyle.indicatorAppearance.id
                )
            ) ?: indicatorStyle.indicatorAppearance
            indicatorLocation = IndicatorLocation.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_indicatorLocation,
                    indicatorStyle.indicatorLocation.id
                )
            ) ?: indicatorStyle.indicatorLocation
            indicatorAnimation = IndicatorAnimation.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_indicatorAnimation,
                    indicatorStyle.indicatorAnimation.id
                )
            ) ?: indicatorStyle.indicatorAnimation

            // Badge
            badgeAnimation = BadgeAnimation.fromId(
                attr.getInt(
                    R.styleable.AnimatedBottomBar_abb_badgeAnimation,
                    tabStyle.badge.animation.id
                )
            ) ?: tabStyle.badge.animation
            badgeAnimationDuration = attr.getInt(
                R.styleable.AnimatedBottomBar_abb_badgeAnimationDuration,
                tabStyle.badge.animationDuration
            )
            badgeBackgroundColor =
                attr.getColor(
                    R.styleable.AnimatedBottomBar_abb_badgeBackgroundColor,
                    tabStyle.badge.backgroundColor
                )
            badgeTextColor =
                attr.getColor(
                    R.styleable.AnimatedBottomBar_abb_badgeTextColor,
                    tabStyle.badge.textColor
                )
            badgeTextSize =
                attr.getDimensionPixelSize(
                    R.styleable.AnimatedBottomBar_abb_badgeTextSize,
                    tabStyle.badge.textSize
                )

            // Initials tabs
            val tabsResId = attr.getResourceId(R.styleable.AnimatedBottomBar_abb_tabs, -1)
            val initialIndex = attr.getInt(R.styleable.AnimatedBottomBar_abb_selectedIndex, -1)
            val initialTabId =
                attr.getResourceId(R.styleable.AnimatedBottomBar_abb_selectedTabId, -1)
            initInitialTabs(tabsResId, initialIndex, initialTabId)
        } finally {
            attr.recycle()
        }
    }

    private fun initRecyclerView() {
        recycler = RecyclerView(context)
        recycler.itemAnimator = null
        recycler.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        recycler.overScrollMode = View.OVER_SCROLL_NEVER
        recycler.contentDescription = contentDescription

        val flexLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP)
        recycler.layoutManager = flexLayoutManager
        addView(recycler)
    }

    private fun initAdapter() {

        adapter = TabAdapter(this, recycler)
        adapter.onTabSelected =
            { lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab, animated: Boolean ->
                tabIndicator.setSelectedIndex(lastIndex, newIndex, animated)

                viewPager?.currentItem = newIndex
                viewPager2?.currentItem = newIndex

                onTabSelectListener?.onTabSelected(lastIndex, lastTab, newIndex, newTab)
                onTabSelected.invoke(newTab)
            }
        adapter.onTabReselected =
            { newIndex: Int, newTab: Tab ->
                onTabSelectListener?.onTabReselected(newIndex, newTab)
                onTabReselected.invoke(newTab)
            }
        adapter.onTabIntercepted = { lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab ->
            onTabInterceptListener?.onTabIntercepted(lastIndex, lastTab, newIndex, newTab)
                ?: onTabIntercepted.invoke(newTab)
        }
        recycler.adapter = adapter
    }

    private fun initTabIndicator() {
        tabIndicator = TabIndicator(this, recycler, adapter)
        recycler.addItemDecoration(tabIndicator)
    }

    private fun initInitialTabs(tabsResId: Int, initialIndex: Int, initialTabId: Int) {
        if (tabsResId == -1) {
            return
        }

        val tabs = MenuParser.parse(context, tabsResId, !isInEditMode)
        addTabs(tabs)

        if (initialIndex != -1) {
            if (initialIndex < 0 || initialIndex > adapter.tabs.size - 1) {
                throw IndexOutOfBoundsException("Attribute 'selectedIndex' ($initialIndex) is out of bounds.")
            } else {
                selectTabAt(initialIndex, false)
            }
        }

        if (initialTabId != -1) {
            val tabIndex = indexOfTabWithId(initialTabId)
            if(tabIndex < 0) {
                throw IllegalArgumentException("Attribute 'selectedTabId', tab with this id does not exist.")
            }

            selectTabAt(tabIndex, false)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            if (layoutParams.height == WRAP_CONTENT) MeasureSpec.makeMeasureSpec(
                64.dpPx,
                MeasureSpec.EXACTLY
            ) else heightMeasureSpec
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recycler.postInvalidate()
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            selectedIndex = adapter.selectedIndex
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if(state is SavedState) {
            super.onRestoreInstanceState(state.superState)

            val index = state.selectedIndex

            if(index >= 0 && index < adapter.tabs.size) {
                selectTabAt(index, false)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setOnTabSelectListener(onTabSelectListener: OnTabSelectListener) {
        this.onTabSelectListener = onTabSelectListener
    }

    fun setOnTabInterceptListener(onTabInterceptListener: OnTabInterceptListener) {
        this.onTabInterceptListener = onTabInterceptListener
    }

    private fun applyTabStyle(type: BottomBarStyle.StyleUpdateType) {
        adapter.applyTabStyle(type)
    }

    private fun applyIndicatorStyle() {
        tabIndicator.applyStyle()
    }


    fun createTab(icon: Drawable?, title: String, @IdRes id: Int = -1, iconSize: Int = -1): Tab {
        if (icon == null) {
            throw IllegalArgumentException("Icon drawable cannot be null.")
        }

        if(iconSize < -1 || iconSize == 0) {
            throw IllegalArgumentException("iconSize should be either -1 or positive value")
        }


        return Tab(icon, iconSize, title, id)
    }


    fun createTab(@DrawableRes iconRes: Int, title: String, @IdRes id: Int = -1, iconSize: Int = -1): Tab {
        val icon = ContextCompat.getDrawable(context, iconRes)
        return createTab(icon, title, id, iconSize)
    }

    fun createTab(@DrawableRes iconRes: Int, @StringRes titleRes: Int, @IdRes id: Int = -1, iconSize: Int = -1): Tab {
        val title = context.getString(titleRes)
        return createTab(iconRes, title, id, iconSize)
    }

    fun addTab(tab: Tab) {
        adapter.addTab(tab)
    }

    fun addTabs(tabs: Array<out Tab>) {
        adapter.addTabs(tabs)
    }

    fun addTabs(tabs: Collection<Tab>) {
        adapter.addTabs(tabs)
    }

    fun addTabAt(tabIndex: Int, tab: Tab) {
        adapter.addTab(tab, tabIndex)
    }

    fun removeTabAt(tabIndex: Int) {
        if (tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IndexOutOfBoundsException("Tab index $tabIndex is out of bounds.")
        }

        adapter.removeTabAt(tabIndex)
    }

    fun removeTabById(@IdRes id: Int) {
        val tabIndex = indexOfTabWithId(id)
        if(tabIndex < 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }

        adapter.removeTabAt(tabIndex)
    }

    fun removeTab(tab: Tab) {
        adapter.removeTab(tab)
    }

    fun selectTabAt(tabIndex: Int, animate: Boolean = true) {
        if (tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IndexOutOfBoundsException("Tab index $tabIndex is out of bounds.")
        }

        adapter.selectTabAt(tabIndex, animate)
    }

    fun selectTabById(@IdRes id: Int, animate: Boolean = true) {
        val tabIndex = indexOfTabWithId(id)
        if(tabIndex < 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }
        adapter.selectTabAt(tabIndex, animate)
    }

    fun selectTab(tab: Tab, animate: Boolean = true) {
        adapter.selectTab(tab, animate)
    }

    fun clearSelection(animate: Boolean = true) {
        adapter.clearSelection(animate)
    }

    fun setTabEnabledAt(tabIndex: Int, enabled: Boolean) {
        if (tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IndexOutOfBoundsException("Tab index $tabIndex is out of bounds.")
        }

        adapter.selectTabAt(tabIndex, enabled)
    }

    fun setTabEnabledById(@IdRes id: Int, enabled: Boolean) {
        val index = indexOfTabWithId(id)
        if(index < 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }

        adapter.selectTabAt(index, enabled)
    }

    fun setTabEnabled(tab: Tab, enabled: Boolean) {
        tab.enabled = enabled
        adapter.notifyTabChanged(tab)
    }

    fun setBadgeAtTabIndex(tabIndex: Int, badge: Badge? = null) {
        if (tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IndexOutOfBoundsException("Tab index $tabIndex is out of bounds.")
        }

        val tab = adapter.tabs[tabIndex]
        tab.badge = badge
        adapter.applyTabBadgeAt(tabIndex, badge ?: Badge())
    }

    fun setBadgeAtTabId(@IdRes id: Int, badge: Badge? = null) {
        val index = indexOfTabWithId(id)
        if(index >= 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }
        setBadgeAtTabIndex(index, badge)
    }

    fun setBadgeAtTab(tab: Tab, badge: Badge? = null) {
        tab.badge = badge
        adapter.applyTabBadge(tab, badge ?: Badge())
    }

    fun clearBadgeAtTabIndex(tabIndex: Int) {
        if (tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IndexOutOfBoundsException("Tab index $tabIndex is out of bounds.")
        }

        val tab = adapter.tabs[tabIndex]
        tab.badge = null
        adapter.applyTabBadgeAt(tabIndex, null)
    }

    fun clearBadgeAtTabId(@IdRes id: Int) {
        val index = indexOfTabWithId(id)
        if(index < 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }
        clearBadgeAtTabIndex(index)
    }

    fun clearBadgeAtTab(tab: Tab) {
        tab.badge = null
        adapter.applyTabBadge(tab, null)
    }

    fun setIconSizeAtTab(tab: Tab, iconSize: Int) {
        if(iconSize < -1 || iconSize == 0) {
            throw IllegalArgumentException("iconSize should be either -1 or positive value.")
        }

        tab.iconSize = iconSize
        adapter.applyIconSize(tab, iconSize)
    }

    fun setIconSizeAtTabIndex(tabIndex: Int, iconSize: Int) {
        if(tabIndex < 0 || tabIndex >= adapter.tabs.size) {
            throw IllegalArgumentException("Tab index is out of bounds.")
        }

        if(iconSize < -1 || iconSize == 0) {
            throw IllegalArgumentException("iconSize should be either -1 or positive value.")
        }

        adapter.tabs[tabIndex].iconSize = iconSize
        adapter.applyIconSize(tabIndex, iconSize)
    }

    fun setIconSizeAtTabId(@IdRes id: Int, iconSize: Int) {
        val index = indexOfTabWithId(id)
        if(index < 0) {
            throw IllegalArgumentException("Tab with id $id does not exist.")
        }

        setIconSizeAtTabIndex(index, iconSize)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return recycler.isNestedScrollingEnabled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        recycler.isNestedScrollingEnabled = enabled
    }



    fun setupWithViewPager2(viewPager2: ViewPager2?) {
        this.viewPager2 = viewPager2

        if (viewPager2 != null) {
            selectTabAt(viewPager2.currentItem, false)
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var previousState: Int = ViewPager2.SCROLL_STATE_IDLE
                private var userScrollChange = false

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    // Use Scroll state to detect whether the user is sliding
                    if (previousState == ViewPager2.SCROLL_STATE_DRAGGING
                        && state == ViewPager2.SCROLL_STATE_SETTLING
                    ) {
                        userScrollChange = true
                    } else if (previousState == ViewPager2.SCROLL_STATE_SETTLING
                        && state == ViewPager2.SCROLL_STATE_IDLE
                    ) {
                        userScrollChange = false
                    }
                    previousState = state
                }

                override fun onPageSelected(position: Int) {
                    if (userScrollChange) {
                        // Swap by user's touch, change adapter to new tab.
                        selectTabAt(position)
                    }
                }
            })
        }
    }

    fun setupWithNavController(menu: Menu, navController: NavController) {
        NavigationComponentHelper.setupWithNavController(this, menu, navController)
    }

    private fun indexOfTabWithId(@IdRes id: Int): Int {
        val tabs = adapter.tabs

        for(i in tabs.indices) {
            if(tabs[i].id == id) {
                return i
            }
        }
        return -1
    }

    var selectedTabType
        get() = tabStyle.selectedTabType
        set(value) {
            tabStyle.selectedTabType = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.TAB_TYPE)
        }

    val tabs
        get() = ArrayList(adapter.tabs)

    val tabCount
        get() = adapter.tabs.size

    val selectedTab
        get() = adapter.selectedTab

    val selectedIndex
        get() = adapter.selectedIndex

    var tabAnimationSelected
        get() = tabStyle.tabAnimationSelected
        set(value) {
            tabStyle.tabAnimationSelected = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.ANIMATIONS)
        }

    var tabAnimation
        get() = tabStyle.tabAnimation
        set(value) {
            tabStyle.tabAnimation = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.ANIMATIONS)
        }

    var animationDuration
        get() = tabStyle.animationDuration
        set(value) {
            tabStyle.animationDuration = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.ANIMATIONS)
        }

    var animationInterpolator
        get() = tabStyle.animationInterpolator
        set(value) {
            tabStyle.animationInterpolator = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.ANIMATIONS)
        }

    var animationInterpolatorRes: Int
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = 0
        set(@AnimRes value) {
            animationInterpolator = AnimationUtils.loadInterpolator(context, value)
        }


    var rippleEnabled
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        get() = tabStyle.rippleEnabled
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        set(value) {
            tabStyle.rippleEnabled = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.RIPPLE)
        }

    var rippleColor
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @ColorInt
        get() = tabStyle.rippleColor
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        set(@ColorInt value) {
            tabStyle.rippleColor = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.RIPPLE)
        }

    var rippleColorRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        set(@ColorRes value) {
            rippleColor = ContextCompat.getColor(context, value)
        }


    var tabColorSelected
        @ColorInt
        get() = tabStyle.tabColorSelected
        set(@ColorInt value) {
            tabStyle.tabColorSelected = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.COLORS)
        }

    var tabColorSelectedRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            tabColorSelected = ContextCompat.getColor(context, value)
        }

    var tabColorDisabled
        @ColorInt
        get() = tabStyle.tabColorDisabled
        set(@ColorInt value) {
            tabStyle.tabColorDisabled = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.COLORS)
        }

    var tabColorDisabledRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            tabColorDisabled = ContextCompat.getColor(context, value)
        }

    var tabColor
        @ColorInt
        get() = tabStyle.tabColor
        set(@ColorInt value) {
            tabStyle.tabColor = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.COLORS)
        }

    var tabColorRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            tabColor = ContextCompat.getColor(context, value)
        }

    // Text
    var textAppearance
        @StyleRes
        get() = tabStyle.textAppearance
        set(@StyleRes value) {
            tabStyle.textAppearance = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.TEXT)
        }
    var typeface
        get() = tabStyle.typeface
        set(value) {
            tabStyle.typeface = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.TEXT)
        }
    var textSize
        @Dimension
        get() = tabStyle.textSize
        set(@Dimension value) {
            tabStyle.textSize = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.TEXT)
        }

    // Icon
    var iconSize
        @Dimension
        get() = tabStyle.iconSize
        set(@Dimension value) {
            tabStyle.iconSize = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.ICON)
        }

    // Indicator
    var indicatorHeight
        @Dimension
        get() = indicatorStyle.indicatorHeight
        set(@Dimension value) {
            indicatorStyle.indicatorHeight = value
            applyIndicatorStyle()
        }

    var indicatorMargin
        @Dimension
        get() = indicatorStyle.indicatorMargin
        set(@Dimension value) {
            indicatorStyle.indicatorMargin = value
            applyIndicatorStyle()
        }

    var indicatorColor
        @ColorInt
        get() = indicatorStyle.indicatorColor
        set(@ColorInt value) {
            indicatorStyle.indicatorColor = value
            applyIndicatorStyle()
        }

    var indicatorColorRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            indicatorColor = ContextCompat.getColor(context, value)
        }

    var indicatorAppearance
        get() = indicatorStyle.indicatorAppearance
        set(value) {
            indicatorStyle.indicatorAppearance = value
            applyIndicatorStyle()
        }

    var indicatorLocation
        get() = indicatorStyle.indicatorLocation
        set(value) {
            indicatorStyle.indicatorLocation = value
            applyIndicatorStyle()
        }

    var indicatorAnimation
        get() = indicatorStyle.indicatorAnimation
        set(value) {
            indicatorStyle.indicatorAnimation = value
            applyIndicatorStyle()
        }

    // Badge
    var badgeAnimation
        get() = tabStyle.badge.animation
        set(value) {
            tabStyle.badge.animation = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.BADGE)
        }

    var badgeAnimationDuration
        get() = tabStyle.badge.animationDuration
        set(value) {
            tabStyle.badge.animationDuration = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.BADGE)
        }

    var badgeBackgroundColor
        @ColorInt
        get() = tabStyle.badge.backgroundColor
        set(@ColorInt value) {
            tabStyle.badge.backgroundColor = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.BADGE)
        }

    var badgeBackgroundColorRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            badgeBackgroundColor = ContextCompat.getColor(context, value)
        }

    var badgeTextColor
        @ColorInt
        get() = tabStyle.badge.textColor
        set(@ColorInt value) {
            tabStyle.badge.textColor = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.BADGE)
        }

    var badgeTextColorRes
        @Deprecated("", level = DeprecationLevel.HIDDEN)
        get() = Int.MIN_VALUE
        set(@ColorRes value) {
            badgeTextColor = ContextCompat.getColor(context, value)
        }

    var badgeTextSize
        get() = tabStyle.badge.textSize
        set(@Dimension value) {
            tabStyle.badge.textSize = value
            applyTabStyle(BottomBarStyle.StyleUpdateType.BADGE)
        }

    class Tab internal constructor(
        val icon: Drawable,
        var iconSize: Int = -1,
        val title: String,
        @IdRes val id: Int = -1,
        var badge: Badge? = null,
        var enabled: Boolean = true,
        var contentDescription: String? = null
    )

    class Badge(
        val text: String? = null,
        @ColorInt val backgroundColor: Int? = null,
        @ColorInt val textColor: Int? = null,
        @Dimension val textSize: Int? = null
    ) {
        constructor() : this(null, null, null, null)
        constructor(text: String?) : this(text, null, null, null)
    }

    enum class TabType(val id: Int) {
        TEXT(0),
        ICON(1);

        companion object {
            fun fromId(id: Int): TabType? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    enum class TabAnimation(val id: Int) {
        NONE(0),
        SLIDE(1),
        FADE(2);

        companion object {
            fun fromId(id: Int): TabAnimation? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    enum class IndicatorLocation(val id: Int) {
        TOP(0),
        BOTTOM(1);

        companion object {
            fun fromId(id: Int): IndicatorLocation? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    enum class IndicatorAppearance(val id: Int) {
        INVISIBLE(0),
        SQUARE(1),
        ROUND(2);

        companion object {
            fun fromId(id: Int): IndicatorAppearance? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    enum class IndicatorAnimation(val id: Int) {
        NONE(0),
        SLIDE(1),
        FADE(2);

        companion object {
            fun fromId(id: Int): IndicatorAnimation? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    enum class BadgeAnimation(val id: Int) {
        NONE(0),
        SCALE(1),
        FADE(2);

        companion object {
            fun fromId(id: Int): BadgeAnimation? {
                for (f in values()) {
                    if (f.id == id) return f
                }
                throw IllegalArgumentException()
            }
        }
    }

    interface OnTabSelectListener {
        fun onTabSelected(lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab)

        fun onTabReselected(index: Int, tab: Tab) {
        }
    }

    interface OnTabInterceptListener {
        fun onTabIntercepted(lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab): Boolean
    }
}