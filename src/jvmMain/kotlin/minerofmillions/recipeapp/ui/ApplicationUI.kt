package minerofmillions.recipeapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import minerofmillions.recipeapp.components.IApplication

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ApplicationUI(application: IApplication) {
	var navigationIcon: @Composable () -> Unit by remember { mutableStateOf({}) }
	var titleExtra: @Composable () -> Unit by remember { mutableStateOf({}) }
	
	fun BackArrowButton(onClick: () -> Unit): @Composable () -> Unit = {
		IconButton(onClick = onClick) {
			Icon(Icons.Default.ArrowBack, "Go back")
		}
	}
	
	fun ExtraTitle(title: String): @Composable () -> Unit = {
		Text(" | $title")
	}
	MaterialTheme {
		Scaffold(topBar = {
			TopAppBar(title = {
				Row {
					Text("Recipe Calculator")
					titleExtra()
				}
			}, navigationIcon = navigationIcon)
		}) {
			Children(application.childStack) {
				when (val child = it.instance) {
					is IApplication.Child.MainScreen -> {
						MainScreen(child.component)
						navigationIcon = {}
						titleExtra = {}
					}
					
					is IApplication.Child.CalculatorSetup -> {
						CalculatorSetup(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = ExtraTitle("Calculator Setup")
					}
					
					is IApplication.Child.CalculatorSolving -> {
						CalculatorSolving(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = ExtraTitle("Solving")
					}
					
					is IApplication.Child.CalculatorResult -> {
						CalculatorResult(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = ExtraTitle("Result")
					}
					
					is IApplication.Child.ItemList -> {
						ItemList(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = ExtraTitle("Items")
					}
					
					is IApplication.Child.ItemDetails -> {
						ItemDetails(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = { ExtraTitle("Item Details")(); ExtraTitle(child.component.item.namespacedName)() }
					}
					
					is IApplication.Child.NPCList -> {
						NPCList(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = ExtraTitle("NPCs")
					}
					
					is IApplication.Child.NPCDetails -> {
						NPCDetails(child.component)
						navigationIcon = BackArrowButton(child.component::onBackClicked)
						titleExtra = { ExtraTitle("NPC Details")(); ExtraTitle(child.component.npc.namespacedName)() }
					}
					
					is IApplication.Child.RecipeList -> TODO()
					is IApplication.Child.RecipeDetails -> TODO()
				}
			}
		}
	}
}
