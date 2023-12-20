package se.granin.currency.data.repository

import io.reactivex.Flowable

interface RatesRepository {
    fun setBaseCurrency(currency: String)
    fun getLatestRates(): Flowable<RatesResponse>
    fun getCurrenciesNames(): Flowable<Map<String, String>>
}