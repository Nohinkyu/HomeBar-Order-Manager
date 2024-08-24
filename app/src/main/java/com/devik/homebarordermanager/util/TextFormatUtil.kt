package com.devik.homebarordermanager.util

import java.text.DecimalFormat

object TextFormatUtil {

    private val thousandsComma = DecimalFormat("#,###")

    fun priceTextFormat(price: Int, measure: String):String {
        return "${thousandsComma.format(price)} $measure"
    }
}