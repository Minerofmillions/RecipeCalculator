package minerofmillions.recipeapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import minerofmillions.recipeapp.state.CalculatorState
import minerofmillions.recipeapp.state.rememberCalculatorState

@Composable
@Preview
fun App() {
	var darkMode by remember { mutableStateOf(true) }
	val density = LocalDensity.current
	val calculatorState = rememberCalculatorState()
	MaterialTheme(colors = if (darkMode) darkColors() else lightColors()) {
		Scaffold(topBar = {
			TopAppBar(
				actions = {
					AppActions(calculatorState)
					IconButton(onClick = { darkMode = !darkMode }) {
//						Icon(Icons.Default.Favorite, if (darkMode) "Dark Mode" else "Light Mode")
						if (darkMode) AsyncImage(
							load = { loadSvgPainter(javaClass.getResource("/light_mode_white.svg"), density) },
							painterFor = { it },
							contentDescription = "Light Mode",
						)
						else AsyncImage(
							load = { loadSvgPainter(javaClass.getResource("/dark_mode_white.svg"), density) },
							painterFor = { it },
							contentDescription = "Dark Mode",
						)
					}
				},
				title = {
					Text("Recipe Calculator")
				}
			)
		}) { padding ->
			Column(Modifier.padding(padding)) {
				Row {
					val state = rememberLazyListState()
					LazyColumn(state = state, modifier = Modifier.weight(1f)) {
						items(calculatorState.recipes) {
							Column {
								Text(it.name)
								Text(it.inputs.joinToString())
								Text(it.outputs.joinToString())
							}
						}
					}
					VerticalScrollbar(rememberScrollbarAdapter(state))
				}
			}
		}
	}
}

@Composable
fun AppActions(state: CalculatorState) {
	if (!state.loadingRecipes) {
		if (state.recipes.isEmpty()) Button(onClick = { state.loadRecipes() }) {
			Text("Load Recipes")
		}
		else Button(onClick = { state.clearRecipes() }) {
			Text("Clear Recipes")
		}
	}
}

fun main() = application {
	Window(onCloseRequest = ::exitApplication) {
		App()
	}
}
