package common

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

fun <T,V> Deferred<T>.map(f: (T) -> V): Deferred<V> {
    return GlobalScope.async { f(await()) }
}

suspend fun <T,V> Deferred<T>.awaitAndMap(f: (T) -> V): V {
    return f(await())
}