package se.granin.currency.view.listitem

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import com.squareup.picasso.Picasso
import se.granin.currency.R
import se.granin.currency.data.ListItemData
import se.granin.currency.databinding.NiceListItemBinding
import se.granin.currency.utils.ext.dismissKeyboard

open class NiceCurrencyListItemView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ListItemView(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: NiceListItemBinding =
        NiceListItemBinding.inflate(LayoutInflater.from(context), this)

    override val layoutResource: Int
        get() = R.layout.nice_list_item

    private val textListener = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (baseBinding.currencyRate.isFocused && !s?.toString().isNullOrEmpty()) {
                listItemListener?.onAmountChanged(s.toString().toDouble())
            }
        }
    }

    init {
        setBackground()
        baseBinding.currencyRate.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                v.dismissKeyboard()
        }
        updateBase(false)
    }

    override fun update(dataItem: ListItemData, position: Int) {
        super.update(dataItem, position)
        loadImage(dataItem.iconId)
        dataItem.subTitle?.let(::updateSubtitle)
            ?: dataItem.subTitleId?.let { binding.currencyDescription?.setText(it) }
        updateBase(dataItem.isBase)
    }

    fun updateSubtitle(subTitle: String?) {
        binding.currencyDescription?.setText(subTitle)
    }

    fun updateBase(isBase: Boolean?) {
        if (isBase == true) {
            setOnClickListener(null)
            baseBinding.currencyRate?.setOnClickListener(null)
            baseBinding.currencyRate?.isFocusableInTouchMode = true
            baseBinding.currencyRate?.addTextChangedListener(textListener)
        } else {
            baseBinding.currencyRate?.isFocusable = false
            baseBinding.currencyRate?.setOnClickListener { this.callOnClick() }
            baseBinding.currencyRate?.removeTextChangedListener(textListener)
            setOnClickListener { listItemListener?.onSelectBaseCurrency(itemData.title ?: "") }
        }
    }

    private fun loadImage(@DrawableRes resId: Int) {
        Picasso.get().load(resId)
            .resize(300, 300)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.currencyImage)
    }

    private fun setBackground() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }

    override fun onDetachedFromWindow() {
        baseBinding.currencyRate?.run {
            removeTextChangedListener(textListener)
            if (!hasFocus())
                dismissKeyboard()
        }
        setOnClickListener(null)
        super.onDetachedFromWindow()
    }
}