package se.granin.currency

import android.app.Application

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import se.granin.currency.di.mainModule
import se.granin.currency.di.networkModule
import se.granin.currency.di.rxModule

class RatesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@RatesApplication)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(listOf(mainModule, networkModule, rxModule))
        }

    }

}