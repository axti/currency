package se.granin.currency.utils.ext

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.util.Locale

fun Double.format(digits: Int) = "%.${digits}f".format(Locale.US,this)

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(crossinline factory: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        factory(layoutInflater)
    }

inline fun <T : ViewBinding> ViewGroup.viewBinding(factory: (LayoutInflater, ViewGroup) -> T) =
    factory(LayoutInflater.from(context), this)

inline fun View.waitForLayout(crossinline f: () -> Unit) =
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            Log.e("ViewTreeObserver", "onGlobalLayout")
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
            f()
        }
    })

fun View.dismissKeyboard() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(this.windowToken, 0)
}