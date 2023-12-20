package se.granin.currency.utils.ext

import se.granin.currency.utils.rx.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by axti on 05.05.2020.
 */
fun <T> Single<T>.with(schedulerProvider: SchedulerProvider): Single<T> = observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Observable<T>.with(schedulerProvider: SchedulerProvider): Observable<T> = observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Flowable<T>.with(schedulerProvider: SchedulerProvider): Flowable<T> = observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun Completable.with(schedulerProvider: SchedulerProvider): Completable = observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())