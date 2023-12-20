package se.granin.currency.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import se.granin.currency.R
import se.granin.currency.data.ListItemData
import se.granin.currency.data.ListItemData.Companion.CURRENCY
import se.granin.currency.data.ListItemData.Companion.ERROR
import se.granin.currency.data.RatesModel
import se.granin.currency.data.ResourceManager
import se.granin.currency.utils.rx.SchedulerProvider

class MainViewModel(
    private val ratesModel: RatesModel,
    private val schedulerProvider: SchedulerProvider,
    private val resourceManager: ResourceManager,
) : ViewModel() {

    private var currencyChanged = false
    private var receivedError = false
    private val onScroll: Subject<Unit> = PublishSubject.create()

    private val currencyNames =
        ratesModel.getCurrencyNames().startWith(emptyMap()).onErrorReturn { emptyMap() }

    fun getLatestCurrencyList(): Flowable<List<ListItemData>> {
        return ratesModel.getLatestRates()
            //.with(schedulerProvider)
            .subscribeOn(schedulerProvider.io())
            .flatMap { currencyData ->  currencyNames.map { Pair(currencyData, it) } }
            .map { (currencyData, names) ->
                val list = ArrayList<ListItemData>()
                if (currencyData.error != null) {
                    list.add(
                        ListItemData.Builder()
                            .setTitle(currencyData.error)
                            .setType(ERROR)
                            .build()
                    )
                    if (!receivedError) {
                        receivedError = true
                        onScroll.onNext(Unit)
                    }
                } else {
                    if (receivedError) {
                        receivedError = false
                        onScroll.onNext(Unit)
                    }
                }
                if (currencyData.list != null) {
                    list.addAll(currencyData.list.map { item ->
                        ListItemData.Builder()
                            .setTitle(item.name)
                            .setValue(item.value)
                            .setIconId(getIconIdByCurrencyTag(item.name))
                            .setSubTitleId(getSubTitleIdByTag(item.name))
                            .setSubTitle(names[item.name])
                            .setType(CURRENCY)
                            .setIsBase(item.isBase)
                            .build()
                    })
                    if (currencyChanged) {
                        currencyChanged = false
                        onScroll.onNext(Unit)
                    }
                }

                list
            }
    }

    @DrawableRes
    private fun getIconIdByCurrencyTag(name: String): Int {
        if (name.length > 2) {
            val resId =
                resourceManager.getDrawableResourceByName(name.substring(0, 2).toLowerCase())
            if (resId > 0)
                return resId
        }
        return R.drawable.placeholder
    }

    @StringRes
    private fun getSubTitleIdByTag(name: String): Int {
        val resId = resourceManager.getStringResourceByName("currency_${name.toLowerCase()}_name")
        if (resId > 0)
            return resId
        return R.string.unknown_currency_description
    }

    fun setBaseCurrency(currencyName: String) {
        ratesModel.setBaseCurrency(currencyName)
        currencyChanged = true
    }

    fun setBaseMultiplier(multiplier: Double) = ratesModel.setBaseMultiplier(multiplier)

    fun shouldScrollToTop(): Observable<Unit> = onScroll
}