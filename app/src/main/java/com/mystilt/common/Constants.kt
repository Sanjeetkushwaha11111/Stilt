package com.mystilt.common

import java.util.regex.Pattern

object Constants {
    // Regex for 10 digit number starting with 6,7,8,9
    private const val REG = "^[6-9]\\d{9}$"
    var PATTERN: Pattern = Pattern.compile(REG)
    const val tabToLand = "TAB_TO_LAND"
}