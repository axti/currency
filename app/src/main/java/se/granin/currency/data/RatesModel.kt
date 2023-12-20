package se.granin.currency.data

import io.reactivex.Flowable

interface RatesModel {
    fun getLatestRates(): Flowable<CurrencyData>
    fun setBaseCurrency(currency: String)
    fun setBaseMultiplier(multiplier: Double)
    fun getCurrencyNames(): Flowable<Map<String, String>>
}