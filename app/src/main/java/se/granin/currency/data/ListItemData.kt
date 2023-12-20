package se.granin.currency.data

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes

class ListItemData internal constructor(builder: Builder) {

    @DrawableRes
    @get:DrawableRes
    val iconId: Int

    @StringRes
    @get:StringRes
    val titleId: Int

    val title: String?

    @StringRes
    @get:StringRes
    val subTitleId: Int?

    val subTitle: String?

    @ItemType
    @get:ItemType
    val type: Int

    @Retention
    @IntDef(ERROR, CURRENCY)
    annotation class ItemType

    val value: Double?
    val isBase: Boolean?

    init {
        iconId = builder.getIconId()
        titleId = builder.getTitleId()
        title = builder.getTitle()
        subTitleId = builder.getSubTitleId()
        subTitle = builder.getSubTitle()
        type = builder.getType()

        if ((builder.getValue() == null || builder.isBase() == null) && type > ERROR) {
            throw IllegalArgumentException("ListItemData should have configured value for all currency items")
        }
        value = builder.getValue()
        isBase = builder.isBase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val item = other as ListItemData
        return iconId == item.iconId && (titleId == item.titleId || title == item.title)
    }

    override fun hashCode(): Int {
        var result = iconId
        result = 31 * result + titleId
        return result
    }

    /**
     * Builder for ListItemData
     */
    class Builder {

        @DrawableRes
        private var iconId: Int = NO_RESOURCE

        @StringRes
        private var titleId: Int = NO_RESOURCE
        private var title: String? = null
        private var subTitleId: Int = NO_RESOURCE
        private var subTitle: String? = null

        @ItemType
        private var type: Int = 0

        private var value: Double? = null
        private var isBase: Boolean? = null

        /**
         * @param iconId have -1 by default
         * @return Builder object with updated iconId
         */
        fun setIconId(@DrawableRes iconId: Int): Builder {
            this.iconId = iconId
            return this
        }

        /**
         * @param titleId of resource to be used as a title
         * @return Builder object with updated titleId
         */
        fun setTitleId(@StringRes titleId: Int): Builder {
            this.titleId = titleId
            return this
        }

        /**
         * @param title can be null. used one of methods. TitleId is preferred
         * @return Builder object with updated title
         */
        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setSubTitleId(@StringRes subTitleId: Int): Builder {
            this.subTitleId = subTitleId
            return this
        }

        fun setSubTitle(subTitle: String?): Builder {
            this.subTitle = subTitle
            return this
        }

        /**
         * @param type should not be null
         * @return Builder object with updated type
         */
        fun setType(@ItemType type: Int): Builder {
            this.type = type
            return this
        }

        /**
         * @param newValue setting to set
         * @return Builder with updated setting
         */
        fun setValue(newValue: Double): Builder {
            value = newValue
            return this
        }

        /**
         * @param newIsBase setting to set
         * @return Builder with updated setting
         */
        fun setIsBase(newIsBase: Boolean): Builder {
            isBase = newIsBase
            return this
        }

        /**
         * @return a new ListItemData with Builder
         */
        fun build(): ListItemData {
            return ListItemData(this)
        }

        fun getIconId() = iconId
        fun getTitleId() = titleId
        fun getTitle() = title
        fun getSubTitleId() = subTitleId
        fun getSubTitle() = subTitle
        fun getType() = type
        fun getValue() = value
        fun isBase() = isBase
    }

    companion object {
        const val ERROR = 0
        const val CURRENCY = 1
        const val NO_RESOURCE = -1
    }
}