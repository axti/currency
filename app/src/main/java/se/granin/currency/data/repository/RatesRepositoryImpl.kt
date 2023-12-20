package se.granin.currency.data.repository

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import se.granin.currency.network.RatesApiService
import java.util.concurrent.TimeUnit

class RatesRepositoryImpl(private val apiService: RatesApiService) : RatesRepository {
    private val baseCurrency: Subject<String> = BehaviorSubject.createDefault("USD")
    private val currencyNames: Observable<Map<String, String>> = apiService.loadCurrenciesNames()
        .toObservable()
        .cache()

    override fun getLatestRates(): Flowable<RatesResponse> {
        return baseCurrency.toFlowable(BackpressureStrategy.LATEST)
            .switchMap { base ->
                Flowable.interval(0, POLLING_30_MIN_IN_MS, TimeUnit.MILLISECONDS)
                    .flatMapSingle {
                        apiService.loadLatestRates()
                            .map { currentRates ->
                                val originalBase = currentRates.baseCurrency
                                val baseMultiplier = currentRates.rates[base] ?: 1.0
                                currentRates.copy(baseCurrency = base, rates = currentRates.rates.mapValues { it.value / baseMultiplier })
                            }
                            .map<RatesResponse> { RatesResponse.SuccessResponse(it) }
                            .onErrorReturn { RatesResponse.FailedResponse(it.localizedMessage) }
                    }
            }
    }


    override fun getCurrenciesNames(): Flowable<Map<String, String>> {
        return currencyNames.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun setBaseCurrency(currency: String) {
        baseCurrency.onNext(currency)
    }

    companion object {
        internal const val POLLING_INTERVAL_IN_MS = 10000L // 10 sec
        internal const val POLLING_30_MIN_IN_MS = 30 * 60 * 60 * 1000L // 10 sec
    }
}