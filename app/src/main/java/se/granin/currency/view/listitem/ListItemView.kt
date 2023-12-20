package se.granin.currency.view.listitem

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import se.granin.currency.data.ListItemData
import se.granin.currency.utils.ext.format
import se.granin.currency.view.adapter.ListItemListener
import io.reactivex.disposables.CompositeDisposable
import se.granin.currency.databinding.BaseListItemBinding
import kotlin.math.abs

abstract class ListItemView
@JvmOverloads
constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attr, defStyleAttr, defStyleRes) {

    private val disposables = CompositeDisposable()
    protected lateinit var itemData: ListItemData
    var listItemListener: ListItemListener? = null
    val baseBinding: BaseListItemBinding = BaseListItemBinding.inflate(LayoutInflater.from(context),this)

    /**
     * Each view provides its own layout with one method for easy access
     *
     * @return layout resource id
     */
    @get:LayoutRes
    protected abstract val layoutResource: Int

    init {
        inflateView()
    }


    /**
     * Inflation method with one common parameter
     */
    private fun inflateView() {
        View.inflate(context, layoutResource, this)
    }

    /**
     * Updates the list items texts and image.
     *
     * @param dataItem The Item that contains the new texts and image.
     */
    @CallSuper
    open fun update(dataItem: ListItemData, position: Int) {
        itemData = dataItem
        baseBinding.currencyCode?.text = dataItem.title
        updateValue(dataItem.value)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.layoutParams?.apply {
            width = MATCH_PARENT
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposables.dispose()
//        currencyRate?.removeTextChangedListener(textListener)
    }

    fun updateValue(value: Double?) {
        baseBinding.currencyRate?.run {
            value?.let {
                val digitNum = when(it){
                    in 0.001..0.009 -> 3
                    else -> 2
                }
                val str = it.format(digitNum)
                if (this.isFocused) {
                    val newCursorPosition: Int =
                        str.length - abs(this.selectionEnd - (this.text?.length ?: 0))
                    this.setText(str)
                    this.setSelection(0.coerceAtLeast(newCursorPosition).coerceAtMost(str.length))
                } else
                    this.setText(str)
            }
        }
    }
}