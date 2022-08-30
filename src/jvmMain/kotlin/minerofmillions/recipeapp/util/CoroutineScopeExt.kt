package minerofmillions.recipeapp.util

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
	val scope = CoroutineScope(context)
	lifecycle.doOnStop {
		try {
			scope.cancel()
		} catch (_: IllegalStateException) {
		}
	}
	return scope
}

fun LifecycleOwner.coroutineScope(context: CoroutineContext) = CoroutineScope(context, lifecycle)
