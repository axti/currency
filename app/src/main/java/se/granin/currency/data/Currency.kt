package se.granin.currency.data

data class Currency(val name: String, val value: Double, val isBase: Boolean = false)