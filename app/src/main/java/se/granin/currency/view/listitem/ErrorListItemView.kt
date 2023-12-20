package se.granin.currency.view.listitem

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import se.granin.currency.R

class ErrorListItemView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ListItemView(context, attrs, defStyleAttr, defStyleRes) {

    override val layoutResource: Int
        get() = R.layout.error_list_item

    init {
        setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                android.R.color.holo_red_light,
                context.theme
            )
        )
    }
}