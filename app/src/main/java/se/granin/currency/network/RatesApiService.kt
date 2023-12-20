package se.granin.currency.network

import io.reactivex.Single
import retrofit2.http.GET

interface RatesApiService {
    @GET("api/latest.json")
    fun loadLatestRates(): Single<RateDataObject>

    @GET("api/currencies.json")
    fun loadCurrenciesNames(): Single<CurrenciesDataObject>
}