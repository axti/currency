package se.granin.currency.network

import com.google.gson.annotations.SerializedName

data class RateDataObject(
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("rates")
    val rates: Map<String, Double>,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("disclaimer")
    val privacyTerm: String,
    @SerializedName("license")
    val licenseUrl: String,
)
