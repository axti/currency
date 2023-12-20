package se.granin.currency.di

import se.granin.currency.data.RatesModel
import se.granin.currency.data.RatesModelImpl
import se.granin.currency.data.ResourceManager
import se.granin.currency.data.repository.RatesRepository
import se.granin.currency.data.repository.RatesRepositoryImpl
import se.granin.currency.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by axti on 05.05.2020.
 */
val mainModule = module {

    single { RatesModelImpl(get()) as RatesModel }

    single { RatesRepositoryImpl(get()) as RatesRepository }

    factory { ResourceManager(androidApplication()) }

    viewModel { MainViewModel(get(), get(), get()) }
}