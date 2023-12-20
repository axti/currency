package se.granin.currency.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.granin.currency.data.ListItemData
import se.granin.currency.data.ListItemData.Companion.CURRENCY
import se.granin.currency.data.ListItemData.Companion.ERROR
import se.granin.currency.view.listitem.*
import io.reactivex.functions.Consumer

class RatesAdapter(private val itemListener: ListItemListener) :
    RecyclerView.Adapter<RatesAdapter.ViewHolder>(),
    Consumer<Pair<List<ListItemData>, DiffUtil.DiffResult?>> {
    
    private var items: ArrayList<ListItemData> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        @ListItemData.ItemType viewType: Int
    ): RatesAdapter.ViewHolder {
        return ViewHolder(
            when (viewType) {
                ERROR -> ErrorListItemView(parent.context)
                CURRENCY -> NiceCurrencyListItemView(parent.context)
                else -> throw IllegalStateException("View not implemented yet type = $viewType")
            }
        )
    }

    override fun getItemCount(): Int = items.size

    @ListItemData.ItemType
    override fun getItemViewType(position: Int) = items[position].type

    override fun getItemId(position: Int): Long {
        return items[position].title.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RatesAdapter.ViewHolder, position: Int) {
        (holder.itemView as ListItemView).run {
            update(items[position], position)
            listItemListener = itemListener
        }
    }

    override fun onBindViewHolder(
        holder: RatesAdapter.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val combinedChange = createCombinedPayload(payloads as List<Change<ListItemData>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData
            if (newData.isBase != oldData.isBase) {
                (holder.itemView as NiceCurrencyListItemView).updateBase(newData.isBase)
            }
            if (newData.value != oldData.value) {
                (holder.itemView as ListItemView).updateValue(newData.value)
            }
            if (newData.subTitle != oldData.subTitle) {
                (holder.itemView as NiceCurrencyListItemView).updateSubtitle(newData.subTitle)
            }
        }
    }

    /**
     * View holder class with one holder for itemView itself.
     */
    inner class ViewHolder(itemView: ListItemView) : RecyclerView.ViewHolder(itemView)

    override fun accept(pair: Pair<List<ListItemData>, DiffUtil.DiffResult?>) {
//        items = pair.first
        items.clear()
        items.addAll(pair.first)
        pair.second?.dispatchUpdatesTo(this)
    }
}