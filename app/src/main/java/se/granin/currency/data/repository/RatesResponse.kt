package se.granin.currency.data.repository

import se.granin.currency.network.RateDataObject

sealed class RatesResponse {
    data class SuccessResponse(val dataObject: RateDataObject): RatesResponse()
    data class FailedResponse(val errorMessage: String?): RatesResponse()
}