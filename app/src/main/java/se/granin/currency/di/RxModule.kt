package se.granin.currency.di

import se.granin.currency.utils.rx.ApplicationSchedulerProvider
import se.granin.currency.utils.rx.SchedulerProvider
import org.koin.dsl.module

/**
 * Created by axti on 05.05.2020.
 */
val rxModule = module {
    single { ApplicationSchedulerProvider() as SchedulerProvider }
}