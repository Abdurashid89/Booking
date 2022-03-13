package uz.koinot.stadion.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun Number.toMoneyFormat(): String {
    return String.format(
            DecimalFormat(
                    "#,###.##",
                    DecimalFormatSymbols(Locale("ru", "RU"))
            ).format(this)
    )
}

fun String.toMoneyFormat(): String {
    return try {
        val number = this.toFloat()
        number.toMoneyFormat()
    } catch (e: Exception) {
        this
    }
}




