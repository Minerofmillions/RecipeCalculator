package minerofmillions.recipeapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
			TopAppBar(actions = {
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
			}, title = {
				Text("Recipe Calculator")
			})
		}) { padding ->
			Column(Modifier.padding(padding)) {
				var tab by remember { mutableStateOf(0) }
				TabRow(tab) {
					Tab(tab == 0, onClick = { tab = 0 }, text = { Text("Recipes") })
					Tab(tab == 1, onClick = { tab = 1 }, text = { Text("Items") })
				}
				when (tab) {
					0 -> {
						var searchTerm by remember { mutableStateOf("") }
						val shownRecipes by remember {
							derivedStateOf {
								if (calculatorState.loadingRecipes) emptyList()
								else calculatorState.recipes.filter { r ->
									if (searchTerm.isEmpty()) true
									else when {
										searchTerm.startsWith("=>") -> r.outputs.any { it.item == searchTerm.substring(2) }
										searchTerm.startsWith("<=") -> r.inputs.any { it.item == searchTerm.substring(2) }
										searchTerm.startsWith('>') -> r.outputs.any { searchTerm.substring(1) in it.item }
										searchTerm.startsWith('<') -> r.inputs.any { searchTerm.substring(1) in it.item }
										else -> r.name.contains(searchTerm, true)
									}
								}
							}
						}
						TextField(searchTerm,
							{ searchTerm = it },
							Modifier.fillMaxWidth(),
							label = { Text("Search Recipes") })
						ListView(shownRecipes) {
							RecipeView(it)
						}
					}
					1 -> {
						var searchTerm by remember { mutableStateOf("") }
						val shownItems by remember {
							derivedStateOf {
								if (calculatorState.loadingRecipes) emptyList()
								else calculatorState.items.filter {
									when {
										searchTerm.startsWith('=') -> searchTerm.substring(1) == it.type.toString()
										searchTerm.startsWith('@') -> searchTerm.substring(1) == it.mod
										else -> Regex(searchTerm, RegexOption.IGNORE_CASE).containsMatchIn(it.name)
									}
								}
							}
						}
						TextField(searchTerm,
							{ searchTerm = it },
							Modifier.fillMaxWidth(),
							label = { Text("Search Items") })
						ListView(shownItems) {
							ItemView(it)
						}
					}
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
	} else {
		Button(onClick = { state.stopLoadingRecipes() }) {
			Text("Stop Loading")
		}
	}
}

fun main() = application {
	Window(onCloseRequest = ::exitApplication, title = "Recipe Calculator") {
		App()
	}
}
