package minerofmillions.recipeapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import minerofmillions.recipeapp.components.ICalculatorResult
import minerofmillions.recipeapp.components.ICalculatorSetup
import minerofmillions.recipeapp.components.ICalculatorSolving
import minerofmillions.recipeapp.entities.calculator.CalculationResult
import minerofmillions.recipeapp.entities.calculator.CalculatorConditions
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.ui.util.ScrollableColumn
import minerofmillions.recipeapp.ui.util.Selector
import minerofmillions.recipeapp.ui.util.itemsChunked
import minerofmillions.recipeapp.util.of
import minerofmillions.recipeapp.util.toString

@Composable
fun CalculatorSetup(setup: ICalculatorSetup) {
	Column {
		Row(modifier = Modifier.weight(1f)) {
			Column(Modifier.weight(1f)) {
				var addingProduct by remember { mutableStateOf(false) }
				if (addingProduct) ProductAddDialog(setup.items,
					{ setup.products.add(it); addingProduct = false },
					{ addingProduct = false })
				Text("Products:")
				ScrollableColumn(modifier = Modifier.weight(1f)) {
					items(setup.products) {
						Text(it.toString(), Modifier.clickable { setup.products.remove(it) })
					}
				}
				Button(onClick = { addingProduct = true }) {
					Text("Add product")
				}
			}
			Column(Modifier.weight(1f)) {
				var addingPrimitive by remember { mutableStateOf(false) }
				if (addingPrimitive) PrimitiveAddDialog(setup.items,
					{ setup.primitives.add(it); addingPrimitive = false },
					{ addingPrimitive = false })
				Text("Primitives:")
				ScrollableColumn(modifier = Modifier.weight(1f)) {
					items(setup.primitives) {
						Text(it, Modifier.clickable { setup.primitives.remove(it) })
					}
				}
				Button(onClick = { addingPrimitive = true }) {
					Text("Add primitive")
				}
			}
			Column(Modifier.weight(1f)) {
				Text("Conditions:")
				CalculatorConditions.GameMode.values().forEach {
					Row(Modifier.clickable { setup.conditions.gameMode = it }) {
						RadioButton(selected = setup.conditions.gameMode == it, onClick = {
							setup.conditions.gameMode = it
						})
						Text(it.toString(), Modifier.align(Alignment.CenterVertically))
					}
				}
				ScrollableColumn(Modifier.weight(1f)) {
					items(setup.conditions.possibleConditions.filterNot(String::isBlank)) { condition ->
						Row(Modifier.clickable { setup.conditions.toggleCondition(condition) }) {
							val checked by derivedStateOf { condition in setup.conditions.activeConditions }
							Checkbox(
								checked = checked,
								onCheckedChange = { setup.conditions.toggleCondition(condition) })
							Text(condition, Modifier.align(Alignment.CenterVertically))
						}
					}
				}
			}
		}
		Row {
			Button(onClick = { setup.onSolveClicked(true) }) {
				Text("Solve Rate")
			}
			Button(onClick = { setup.onSolveClicked(false) }) {
				Text("Solve One-off")
			}
		}
	}
}

@Composable
fun CalculatorSolving(solving: ICalculatorSolving) {
//	LaunchedEffect(Unit) {
//		delay(2 * MILLIS_PER_MINUTE)
//		solving.onCancel()
//	}
//
	Column {
		LinearProgressIndicator(Modifier.fillMaxWidth())
		Text("Solving...")
		Button(onClick = solving::onBackClicked) {
			Text("Cancel calculation")
		}
	}
}

@Composable
fun CalculatorResult(result: ICalculatorResult) {
	Column {
		Column(Modifier.weight(1f)) {
			val calculatorResult = remember { result.result }
			if (calculatorResult is CalculationResult.ValidSolution) {
				val baseModifier = Modifier.fillMaxWidth().padding(2.dp)
				Text("Valid Solution")
				ScrollableColumn {
					if (calculatorResult.recipes.isNotEmpty()) {
						item { Text("Recipes:") }
						itemsChunked(calculatorResult.recipes.entries.toList(), 3) { (recipe, ratio) ->
							Text("${ratio.toString(3)} * $recipe", baseModifier.clickable { result.onRecipeClicked(recipe) })
						}
					}
					if (calculatorResult.combinedInputs.isNotEmpty()) {
						item { Text("Combined inputs:") }
						itemsChunked(calculatorResult.combinedInputs, 6) { input ->
							Text(input.toString(), baseModifier.clickable { result.onItemClicked(input.item) })
						}
					}
					if (calculatorResult.combinedOutputs.isNotEmpty()) {
						item { Text("Combined outputs:") }
						itemsChunked(calculatorResult.combinedOutputs, 6) { output ->
							Text(output.toString(), baseModifier.clickable { result.onItemClicked(output.item) })
						}
					}
				}
			} else {
				Text("Invalid Solution")
			}
		}
	}
}

@Composable
fun ProductAddDialog(possibleProducts: List<Item>, onAdd: (ItemStack) -> Unit, onClose: () -> Unit) {
	var item by remember { mutableStateOf<Item?>(null) }
	var amount by remember { mutableStateOf("1") }
	val addButtonEnabled by remember {
		derivedStateOf {
			item != null && try {
				of(amount); true
			} catch (_: NumberFormatException) {
				false
			}
		}
	}
	Dialog(onCloseRequest = onClose, title = "Add Product") {
		Column(Modifier.background(MaterialTheme.colors.background)) {
			Selector(selected = item,
				onSelect = { item = it },
				elements = possibleProducts,
				filterMatches = { item, filter -> item.name.contains(filter, true) },
				modifier = Modifier.weight(1f)
			) {
				Text(it.name)
			}
			TextField(value = amount, onValueChange = { amount = it })
			Button(onClick = { onAdd(ItemStack(item!!.type.toString(), amount.toInt())) }, enabled = addButtonEnabled) {
				Text("Add")
			}
		}
	}
}

@Composable
fun PrimitiveAddDialog(possibleProducts: List<Item>, onAdd: (String) -> Unit, onClose: () -> Unit) {
	var item by remember { mutableStateOf<Item?>(null) }
	val addButtonEnabled by remember { derivedStateOf { item != null } }
	Dialog(onCloseRequest = onClose, title = "Add Product") {
		Column(Modifier.background(MaterialTheme.colors.background)) {
			Selector(selected = item,
				onSelect = { item = it },
				elements = possibleProducts,
				filterMatches = { item, filter -> item.name.contains(filter, true) },
				modifier = Modifier.weight(1f)
			) {
				Text(it.name)
			}
			Button(onClick = { onAdd(item!!.type.toString()) }, enabled = addButtonEnabled) {
				Text("Add")
			}
		}
	}
}
