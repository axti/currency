package se.granin.currency.view.listitem

import android.content.Context
import android.util.AttributeSet
import se.granin.currency.R

class SimpleCurrencyItem
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ListItemView(context, attrs, defStyleAttr, defStyleRes) {

    override val layoutResource: Int
        get() = R.layout.base_list_item
}