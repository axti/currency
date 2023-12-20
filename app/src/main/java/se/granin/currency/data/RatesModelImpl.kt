package se.granin.currency.data

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import se.granin.currency.data.repository.RatesRepository
import se.granin.currency.data.repository.RatesResponse
import se.granin.currency.network.RateDataObject

class RatesModelImpl(private val ratesRepository: RatesRepository) : RatesModel {
    private var baseMultiplier: Subject<Double> = BehaviorSubject.createDefault( 1.0)
    private var cachedRateData: RateDataObject? = null
    private val list = mutableListOf<String>()

    override fun getLatestRates(): Flowable<CurrencyData> {
        return Flowable.combineLatest(ratesRepository.getLatestRates(), baseMultiplier.toFlowable(BackpressureStrategy.LATEST)
        ) { response: RatesResponse, multiplier: Double ->
            Pair(response, multiplier)
        }.map { (rateResponse, multiplier) ->
            when (rateResponse) {
                is RatesResponse.SuccessResponse -> {
                    cachedRateData = rateResponse.dataObject
                    return@map CurrencyData(mapToList(cachedRateData, multiplier))
                }
                is RatesResponse.FailedResponse -> {
                    return@map CurrencyData(
                        mapToList(cachedRateData, multiplier),
                        error = "Error: ${rateResponse.errorMessage}"
                    )
                }
            }
        }
    }

    private fun mapToList(data: RateDataObject?, multiplier: Double): List<Currency>? {
        if (data == null)
            return null
        //Set Base Currency first
        list.indexOf(data.baseCurrency)
            .takeIf { it != 0 }
            ?.let { index ->
                if (index > 0)
                    list.remove(data.baseCurrency)
                list.add(0, data.baseCurrency)
            }

        //Add currency if not exist
        data.rates.keys.forEach { name ->
            if (list.indexOf(name) < 0) {
                list.add(name)
            }
        }

        //Remove if not exist in a new list
        list.filterNot { data.rates.keys.contains(it) || data.baseCurrency == it }
            .let { list.removeAll(it) }

        return ArrayList<Currency>().apply {
            add(Currency(data.baseCurrency, multiplier, true))
            addAll(data.rates.entries.filterNot { it.key == data.baseCurrency }.map { Currency(it.key, it.value * multiplier) })
            sortWith(compareBy { list.indexOf(it.name) })
        }
    }

    override fun setBaseCurrency(currency: String) {
        baseMultiplier.onNext(1.0)
        ratesRepository.setBaseCurrency(currency)
    }

    override fun setBaseMultiplier(multiplier: Double) {
        baseMultiplier.onNext(multiplier)
    }

    override fun getCurrencyNames(): Flowable<Map<String, String>> {
        return ratesRepository.getCurrenciesNames()
    }
}