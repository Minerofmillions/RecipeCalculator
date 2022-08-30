package minerofmillions.recipeapp.components.defaults

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.google.common.collect.HashBasedTable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import minerofmillions.recipeapp.components.IApplication
import minerofmillions.recipeapp.entities.calculator.CalculationKey
import minerofmillions.recipeapp.entities.calculator.CalculationResult
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.entities.calculator.Recipe
import minerofmillions.recipeapp.entities.saver.*
import minerofmillions.recipeapp.util.*
import org.ojalgo.scalar.RationalNumber
import java.io.File
import javax.swing.filechooser.FileSystemView
import minerofmillions.recipeapp.entities.calculator.Recipe as CalculatorRecipe
import minerofmillions.recipeapp.entities.saver.Recipe as SaverRecipe

class ApplicationComponent(context: ComponentContext) : IApplication, ComponentContext by context {
	private val navigation = StackNavigation<Config>()
	private val stack = childStack(
		source = navigation, initialConfiguration = Config.MainScreen, childFactory = ::createChild
	)
	private val scope = coroutineScope(kotlinx.coroutines.Dispatchers.Default)
	
	override val childStack: Value<ChildStack<*, IApplication.Child>> get() = stack
	
	override var configuration: DataFile = DataFile()
		private set
	
	override val recipeList = mutableStateListOf<SaverRecipe>()
	override val itemList = mutableStateListOf<Item>()
	override val npcList = mutableStateListOf<NPC>()
	override val fishingData: LootTable = HashBasedTable.create()
	override val groups = mutableStateMapOf<Int, Group>()
	
	init {
		val listerDirectory =
			FileSystemView.getFileSystemView().defaultDirectory.resolve("My Games", "Terraria", "tModLoader", "Saver")
				.takeIf { it.exists() && it.isDirectory }
				?: throw IllegalStateException("Saver directory not found. Please install tModLoader and RecipeSaver mod.")
		val data = (listerDirectory.resolve("_Data.json").takeIf(File::exists)
			?: throw IllegalStateException("_Data.json not found")).reader().use {
			gson.fromJson(it, DataFile::class.java)
		}
		Item.extractinatorTests = data.ExtractinatorTests
		val recipeAsync = scope.async {
			listerDirectory.resolve("Recipes.json").reader().use {
				gson.fromJson(it, Array<SaverRecipe>::class.java)
			}.distinct()
		}
		val npcsAsync = scope.async {
			listerDirectory.resolve("Enemies.json").reader().use {
				gson.fromJson(it, Array<NPC>::class.java)
			}.distinct().filterNot { it.type == 0 }
		}
		val itemsAsync = scope.async {
			listerDirectory.resolve("Items.json").reader().use {
				gson.fromJson(it, Array<Item>::class.java)
			}.distinct().filterNot { it.type == 0 }
		}
		val groupsDeferred = scope.async {
			listerDirectory.resolve("Groups.json").reader().use {
				gson.fromJson<Map<Int, Group>>(it, object : TypeToken<Map<Int, Group>>() {}.type)
			}
		}
		
		runBlocking {
			recipeList.addAll(recipeAsync.await())
			recipeList.sort()
			itemList.addAll(itemsAsync.await())
			itemList.sort()
			npcList.addAll(npcsAsync.await())
			npcList.sort()
			groups.putAll(groupsDeferred.await())
		}
	}
	
	private fun createChild(config: Config, componentContext: ComponentContext): IApplication.Child = when (config) {
		is Config.MainScreen -> IApplication.Child.MainScreen(
			MainScreenComponent(
				componentContext, this::onRecipesShown, this::onItemsShown, this::onNPCsShown, this::onCaluclatorShown
			)
		)
		
		is Config.RecipeList -> IApplication.Child.RecipeList(
			RecipeListComponent(
				context = componentContext, recipeList = recipeList, onRecipeSelected = this::onRecipeDetailsSelected
			)
		)
		
		is Config.RecipeDetails -> IApplication.Child.RecipeDetails(
			RecipeDetailsComponent(
				context = componentContext,
				recipe = config.recipe,
				onItemSelected = this::onItemDetailsSelected,
				onFinished = navigation::pop
			)
		)
		
		is Config.ItemList -> IApplication.Child.ItemList(
			ItemListComponent(
				context = componentContext,
				itemList = itemList,
				onItemSelected = this::onItemDetailsSelected,
				onFinished = navigation::pop
			)
		)
		
		is Config.ItemDetails -> IApplication.Child.ItemDetails(
			ItemDetailsComponent(
				context = componentContext,
				item = config.item,
				onFinished = navigation::pop,
				onItemSelected = this::onItemDetailsSelected,
				onRecipeSelected = this::onRecipeDetailsSelected
			)
		)
		
		is Config.NPCList -> IApplication.Child.NPCList(
			NPCListComponent(
				context = componentContext,
				npcList = npcList,
				onFinished = navigation::pop,
				onNPCSelected = this::onNPCDetailsSelected
			)
		)
		
		is Config.NPCDetails -> IApplication.Child.NPCDetails(
			NPCDetailsComponent(
				context = componentContext,
				npc = config.npc,
				onFinished = navigation::pop,
				onItemSelected = this::onItemDetailsSelected
			)
		)
		
		is Config.CalculatorSetup -> IApplication.Child.CalculatorSetup(
			CalculatorSetupComponent(
				context = componentContext,
				items = itemList,
				recipes = recipeList,
				npcs = npcList,
				groups = groups,
				onFinished = navigation::pop,
				onSolve = this::onCalculatorSolve
			)
		)
		
		is Config.CalculatorSolving -> IApplication.Child.CalculatorSolving(
			CalculatorSolvingComponent(
				context = componentContext,
				key = config.key,
				onCancelled = navigation::pop,
				onSolved = this::onCalculatorSolved
			)
		)
		
		is Config.CalculatorResult -> IApplication.Child.CalculatorResult(
			CalculatorResultComponent(
				context = componentContext,
				result = config.result,
				onFinished = navigation::pop,
				onClickItem = this::onItemDetailsSelected,
				onClickRecipe = this::onRecipeDetailsSelected
			)
		)
	}
	
	private fun onRecipesShown() {
		navigation.push(Config.RecipeList)
	}
	
	private fun onRecipeDetailsSelected(recipe: SaverRecipe) {
		navigation.push(Config.RecipeDetails(recipe))
	}
	
	private fun onRecipeDetailsSelected(recipe: CalculatorRecipe) {
		when (recipe) {
			is CalculatorRecipe.CraftingRecipe -> onRecipeDetailsSelected(recipe.baseRecipe)
			is CalculatorRecipe.OpenRecipe -> onItemDetailsSelected(recipe.item)
			is Recipe.SellRecipe -> onItemDetailsSelected(recipe.item)
			is Recipe.KillRecipe -> onNPCDetailsSelected(recipe.npc)
		}
	}
	
	private fun onItemsShown() {
		navigation.push(Config.ItemList)
	}
	
	private fun onItemDetailsSelected(item: String) {
		itemList.firstOrNull { it.type.toString() == item }?.let(::onItemDetailsSelected)
	}
	
	private fun onItemDetailsSelected(item: Int) {
		itemList.firstOrNull { it.type == item }?.let(::onItemDetailsSelected)
	}
	
	private fun onItemDetailsSelected(item: Item) {
		navigation.push(Config.ItemDetails(item))
	}
	
	private fun onNPCsShown() {
		navigation.push(Config.NPCList)
	}
	
	private fun onNPCDetailsSelected(npc: NPC) {
		navigation.push(Config.NPCDetails(npc))
	}
	
	private fun onCaluclatorShown() {
		navigation.push(Config.CalculatorSetup)
	}
	
	private fun onCalculatorSolve(key: CalculationKey) {
		navigation.push(Config.CalculatorSolving(key))
	}
	
	private fun onCalculatorSolved(result: CalculationResult) {
		navigation.pop()
		navigation.push(Config.CalculatorResult(result))
	}
	
	private sealed class Config : Parcelable {
		@Parcelize
		object MainScreen : Config()
		
		@Parcelize
		object RecipeList : Config()
		
		@Parcelize
		data class RecipeDetails(val recipe: SaverRecipe) : Config()
		
		@Parcelize
		object ItemList : Config()
		
		@Parcelize
		data class ItemDetails(val item: Item) : Config()
		
		@Parcelize
		object NPCList : Config()
		
		@Parcelize
		data class NPCDetails(val npc: NPC) : Config()
		
		@Parcelize
		object CalculatorSetup : Config()
		
		@Parcelize
		data class CalculatorSolving(val key: CalculationKey) : Config()
		
		@Parcelize
		data class CalculatorResult(val result: CalculationResult) : Config()
	}
	
	companion object {
		private val gson = GsonBuilder().registerTypeAdapter(ItemStack::class.java, ItemStack.Serializer)
			.registerTypeAdapter(RationalNumber::class.java, RationalSerializer)
			.registerTypeAdapter(LootTableTypeToken.type, LootTableSerializer).create()
	}
}
