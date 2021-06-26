package ic0der.justtaxi.utils

import android.text.Editable
import uz.koinot.stadion.utils.TextWatcherWrapper

/**
 * Created by mr-shoxruxbek on 12/10/2020.
 */
class AmountFormat(private val validListener: (Boolean, Long) -> Unit) : TextWatcherWrapper() {
    private var lock = false

    override fun afterTextChanged(s: Editable) {
        if (lock) return
        lock = true
        val text = s.toString().replace("\\D".toRegex(), "")

        s.replace(0, s.length, text)
        var i = s.length - 3
        while (i > 0 && i < s.length) {
            if (s.toString()[i] != ' ') {
                s.insert(i, " ")
            }
            i -= 3
        }

        lock = false
        validListener.invoke(
            text.matches("^[1-9][0-9]*$".toRegex()),
            if (text.isNotEmpty()) text.toLong() else 0L
        )
    }
}