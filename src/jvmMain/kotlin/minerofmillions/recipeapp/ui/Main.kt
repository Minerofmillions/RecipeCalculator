package minerofmillions.recipeapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em
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
	MaterialTheme(
		colors = if (darkMode) darkColors() else lightColors(),
		typography = Typography(h1 = TextStyle(fontSize = 2.em), h2 = TextStyle(fontSize = 1.5.em))
	) {
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
				var recipeSearchTerm by remember { mutableStateOf("") }
				var itemSearchTerm by remember { mutableStateOf("") }
				when (tab) {
					0 -> {
						TextField(recipeSearchTerm,
							{ recipeSearchTerm = it },
							Modifier.fillMaxWidth(),
							label = { Text("Search Recipes") })
						FilteredListView(calculatorState.recipes, { r ->
							if (calculatorState.loadingRecipes) false else if (recipeSearchTerm.isEmpty()) true else when {
								recipeSearchTerm.startsWith("=>") -> r.outputs.any {
									it.item == recipeSearchTerm.substring(2)
								}
								recipeSearchTerm.startsWith("=<") -> r.inputs.any {
									it.item == recipeSearchTerm.substring(2)
								}
								recipeSearchTerm.startsWith('>') -> r.outputs.any { recipeSearchTerm.substring(1) in it.item }
								recipeSearchTerm.startsWith('<') -> r.inputs.any { recipeSearchTerm.substring(1) in it.item }
								else -> r.name.contains(recipeSearchTerm, true)
							}
						}, onItemLongClick = { r ->
							itemSearchTerm = "=" + (r.inputs.maxByOrNull { it.amount }?.item ?: "")
							tab = 1
						}, content = {
							RecipeView(it)
						})
					}
					1 -> {
						TextField(itemSearchTerm,
							{ itemSearchTerm = it },
							Modifier.fillMaxWidth(),
							label = { Text("Search Items") })
						FilteredListView(calculatorState.items, {
							if (calculatorState.loadingRecipes) false else when {
								itemSearchTerm.startsWith('=') -> itemSearchTerm.substring(1) == calculatorState.itemSelector(
									it
								)
								itemSearchTerm.startsWith('@') -> itemSearchTerm.substring(1) in it.mod
								else -> it.name.contains(itemSearchTerm, true)
							}
						}, onItemLongClick = {
							recipeSearchTerm = "=<${calculatorState.itemSelector(it)}"
							tab = 0
						}, content = {
							ItemView(it)
						})
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
