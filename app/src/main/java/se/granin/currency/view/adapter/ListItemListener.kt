package se.granin.currency.view.adapter

interface ListItemListener {

    /**
     * Handles value changes done by the user.
     *
     * @param amount
     */
    fun onAmountChanged(amount: Double)

    /**
     * Handles selection of currency
     *
     * @param currencyName
     */
    fun onSelectBaseCurrency(currencyName: String)
}