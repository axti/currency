package se.granin.currency.view.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import se.granin.currency.databinding.ActivityMainBinding
import se.granin.currency.utils.rx.SchedulerProvider
import se.granin.currency.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.granin.currency.utils.ext.viewBinding
import se.granin.currency.view.adapter.ListDiffUtilCallback
import se.granin.currency.view.adapter.ListItemListener
import se.granin.currency.view.adapter.RatesAdapter
import se.granin.currency.view.adapter.RxDiffUtil

class MainActivity : AppCompatActivity(), ListItemListener {
    private val disposables = CompositeDisposable()
    private val viewModel: MainViewModel by viewModel()
    private val listItemsAdapter = RatesAdapter(this)
    private lateinit var itemsLayoutManager: LinearLayoutManager
    private var needScroll = false
    private val schedulerProvider: SchedulerProvider by inject()
    private val transformer = RxDiffUtil.calculateDiff(ListDiffUtilCallback.Companion::create)
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        disposables.add(
            viewModel.getLatestCurrencyList()
                .observeOn(schedulerProvider.io())
                .compose(transformer)
                .observeOn(schedulerProvider.ui())
                .doAfterNext {
                    if (needScroll) {
                        binding.recyclerView.scrollToPosition(0)
                        needScroll = false
                    }
                }
                .subscribe(listItemsAdapter)
        )
        disposables.add(viewModel.shouldScrollToTop()
            .observeOn(schedulerProvider.ui())
            .subscribe { needScroll = true }
        )
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    private fun setupRecyclerView() {
        itemsLayoutManager = LinearLayoutManager(this)
        with(binding.recyclerView) {
            setHasFixedSize(true)
            adapter = listItemsAdapter
            layoutManager = itemsLayoutManager
        }
    }

    override fun onAmountChanged(amount: Double) {
        viewModel.setBaseMultiplier(amount)
    }

    override fun onSelectBaseCurrency(currencyName: String) {
        viewModel.setBaseCurrency(currencyName)
    }
}
