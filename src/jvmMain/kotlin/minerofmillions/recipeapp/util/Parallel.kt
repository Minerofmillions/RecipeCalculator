@file:OptIn(ExperimentalTypeInference::class)

package minerofmillions.recipeapp.util

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.experimental.ExperimentalTypeInference

/*
private class ParallelThread(target: Runnable) : Thread(target) {
	val threadId = threadNumber++
	
	companion object {
		private var threadNumber = 0
	}
}
*/

val dispatcher = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1) {
	Thread(it).apply { isDaemon = true }
}.asCoroutineDispatcher()

inline fun <E> Iterable<E>.forEachParallel(crossinline action: (E) -> Unit) = runBlocking(dispatcher) {
	coroutineScope {
		forEach { element -> launch { action(element) } }
	}
}

inline fun <E> Sequence<E>.forEachParallel(crossinline action: (E) -> Unit) = runBlocking(dispatcher) {
	coroutineScope {
		forEach { element -> launch { action(element) } }
	}
}

inline fun <E, R, C : MutableCollection<R>> Iterable<E>.mapParallelTo(output: C, crossinline transform: (E) -> R): C {
	forEachParallel { output.add(transform(it)) }
	return output
}

inline fun <E, R> Iterable<E>.mapParallel(crossinline transform: (E) -> R): List<R> =
	mapParallelTo(CopyOnWriteArrayList(), transform)

@OverloadResolutionByLambdaReturnType
inline fun <E, R, C : MutableCollection<in R>> Iterable<E>.flatMapParallelTo(
	output: C,
	crossinline transform: (E) -> Iterable<R>,
): C {
	forEachParallel { output.addAll(transform(it)) }
	return output
}

@JvmName("flatMapSequenceParallelTo")
@OverloadResolutionByLambdaReturnType
inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapParallelTo(
	destination: C,
	crossinline transform: (T) -> Sequence<R>,
): C {
	forEachParallel { destination.addAll(transform(it)) }
	return destination
}

@OverloadResolutionByLambdaReturnType
inline fun <E, R> Iterable<E>.flatMapParallel(crossinline transform: (E) -> Iterable<R>): List<R> =
	flatMapParallelTo(Collections.synchronizedList(ArrayList()), transform)

@JvmName("flatMapSequenceParallel")
@OverloadResolutionByLambdaReturnType
inline fun <E, R> Iterable<E>.flatMapParallel(crossinline transform: (E) -> Sequence<R>): List<R> =
	flatMapParallelTo(Collections.synchronizedList(ArrayList()), transform)
