package minerofmillions.recipeapp.components

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import minerofmillions.recipeapp.entities.saver.*

interface IApplication {
	val childStack: Value<ChildStack<*, Child>>
	
	val configuration: DataFile
	val recipeList: List<Recipe>
	val itemList: List<Item>
	val npcList: List<NPC>
	val groups: Map<Int, Group>
	val fishingData: LootTable
	
	sealed class Child {
		class MainScreen(val component: IMainScreen) : Child()
		
		class RecipeList(val component: IRecipeList) : Child()
		class RecipeDetails(val component: IRecipeDetails) : Child()
		class ItemList(val component: IItemList) : Child()
		class ItemDetails(val component: IItemDetails) : Child()
		class NPCList(val component: INPCList) : Child()
		class NPCDetails(val component: INPCDetails) : Child()
		class CalculatorSetup(val component: ICalculatorSetup) : Child()
		class CalculatorSolving(val component: ICalculatorSolving) : Child()
		class CalculatorResult(val component: ICalculatorResult) : Child()
	}
}
