package se.granin.currency.view.adapter

import androidx.recyclerview.widget.DiffUtil
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

object RxDiffUtil {
    fun <T> calculateDiff(
        diffCallbacks: (List<T>, List<T>) -> DiffUtil.Callback
    ): FlowableTransformer<List<T>, Pair<List<T>, DiffUtil.DiffResult?>> {
        var initialPair: Pair<List<T>, DiffUtil.DiffResult?> = emptyList<T>() to null
        return FlowableTransformer { upstream: Flowable<List<T>> ->
            upstream.scan(initialPair) { (first, _), nextItems ->
                val callback =
                    diffCallbacks(first, nextItems)
                val result = DiffUtil.calculateDiff(callback, true)
                initialPair = Pair(nextItems, result)
                Pair(nextItems, result)
            }
                .skip(1)
        } // downstream shouldn't receive initialPair.
    }
}