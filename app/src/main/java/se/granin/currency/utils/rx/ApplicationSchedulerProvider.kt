package se.granin.currency.utils.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by axti on 05.05.2020.
 */
class ApplicationSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.io()

    override fun ui() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}