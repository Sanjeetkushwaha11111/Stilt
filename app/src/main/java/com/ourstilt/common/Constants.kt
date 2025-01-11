package com.ourstilt.common

import java.util.regex.Pattern

object Constants {
    private const val REG = "^(\\+91[\\-\\s]?)?0?[789]\\d{9}\$"
    var PATTERN: Pattern = Pattern.compile(REG)
    const val tabToLand = "TAB_TO_LAND"
}