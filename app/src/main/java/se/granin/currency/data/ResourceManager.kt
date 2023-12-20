package se.granin.currency.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {

    @DrawableRes
    fun getDrawableResourceByName(name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    @StringRes
    fun getStringResourceByName(name: String): Int {
        return context.resources.getIdentifier(name, "string", context.packageName)
    }
}