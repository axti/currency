package se.granin.currency

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Test
import se.granin.currency.data.Currency
import se.granin.currency.data.CurrencyData
import se.granin.currency.data.RatesModelImpl
import se.granin.currency.data.repository.RatesRepository
import se.granin.currency.data.repository.RatesResponse
import se.granin.currency.network.RateDataObject

class RatesModelTest {
    private val ratesRepository: RatesRepository = mockk<RatesRepository>()
    private val ratesModel = RatesModelImpl(ratesRepository)

    private val latestRates: Subject<RatesResponse> = BehaviorSubject.create<RatesResponse>()

    @Test
    fun testGetLatestRates() {
        every { ratesRepository.getLatestRates() } returns latestRates.toFlowable(
            BackpressureStrategy.LATEST
        )

        val eurCurrency = Currency("EUR", 1.0, true)
        val usdCurrency = Currency("USD", 1.1, false)
        val btcCurrency = Currency("BTC", 35000.0, false)
        val data: Map<String, Double> = hashMapOf(
            eurCurrency.name to eurCurrency.value,
            usdCurrency.name to usdCurrency.value,
            btcCurrency.name to btcCurrency.value
        )
        val rateDataObject = mockk<RateDataObject>()
        every { rateDataObject.baseCurrency } returns "EUR"
        every { rateDataObject.rates } returns data
        val ratesResponse = RatesResponse.SuccessResponse(rateDataObject)
        latestRates.onNext(ratesResponse)

        val expectedValue = CurrencyData(listOf(eurCurrency, btcCurrency, usdCurrency))

        ratesModel.getLatestRates().test()
            .assertSubscribed()
            .assertNoErrors()
            .assertValue(expectedValue)

        verify { ratesRepository.getLatestRates() }

        confirmVerified(ratesRepository)

    }

    @Test
    fun testGetLatestRateError() {
        every { ratesRepository.getLatestRates() } returns latestRates.toFlowable(
            BackpressureStrategy.LATEST
        )

        val errorString = "ErrorString"
        val ratesResponse = RatesResponse.FailedResponse(errorString)
        latestRates.onNext(ratesResponse)

        val expectedValue = CurrencyData(error = "Error: $errorString")

        ratesModel.getLatestRates().test()
            .assertSubscribed()
            .assertNoErrors()
            .assertValue(expectedValue)

        verify { ratesRepository.getLatestRates() }

        confirmVerified(ratesRepository)

    }

    @Test
    fun testGetLatestRatesWithMultiplier() {
        every { ratesRepository.getLatestRates() } returns latestRates.toFlowable(
            BackpressureStrategy.LATEST
        )

        val multiplier = 100.0
        val eurCurrency = Currency("EUR", 1.0 * multiplier, true)
        val usdCurrency = Currency("USD", 1.1 * multiplier, false)
        val btcCurrency = Currency("BTC", 35000.0 * multiplier, false)
        val data: Map<String, Double> = hashMapOf(
            eurCurrency.name to eurCurrency.value/multiplier,
            usdCurrency.name to usdCurrency.value/multiplier,
            btcCurrency.name to btcCurrency.value/multiplier
        )
        val rateDataObject = mockk<RateDataObject>()
        every { rateDataObject.baseCurrency } returns "EUR"
        every { rateDataObject.rates } returns data
        val ratesResponse = RatesResponse.SuccessResponse(rateDataObject)

        ratesModel.setBaseMultiplier(multiplier)
        latestRates.onNext(ratesResponse)

        val expectedValue = CurrencyData(listOf(eurCurrency, btcCurrency, usdCurrency))

        ratesModel.getLatestRates().test()
            .assertSubscribed()
            .assertNoErrors()
            .assertValue(expectedValue)

        verify { ratesRepository.getLatestRates() }

        confirmVerified(ratesRepository)

    }
}