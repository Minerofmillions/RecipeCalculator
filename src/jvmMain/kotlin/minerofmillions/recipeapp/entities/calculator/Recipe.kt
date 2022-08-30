package minerofmillions.recipeapp.entities.calculator

import minerofmillions.recipeapp.entities.saver.Group
import minerofmillions.recipeapp.entities.saver.Item
import minerofmillions.recipeapp.entities.saver.NPC
import minerofmillions.recipeapp.util.aboutZero
import minerofmillions.recipeapp.util.minus
import minerofmillions.recipeapp.util.of
import minerofmillions.recipeapp.util.permutations
import org.ojalgo.scalar.RationalNumber
import minerofmillions.recipeapp.entities.saver.Recipe as SaverRecipe

open class Recipe(val name: String, inputs: List<ItemStack>, outputs: List<ItemStack>) : Comparable<Recipe> {
	val inputs: List<ItemStack>
	val outputs: List<ItemStack>
	
	val inputItems by lazy { inputs.map(ItemStack::item) }
	val outputItems by lazy { outputs.map(ItemStack::item) }
	
	override fun toString() = name
	
	override fun compareTo(other: Recipe) = comparator.compare(this, other)
	
	fun isReverseOf(other: Recipe): Boolean =
		other.outputItems.all(inputItems::contains) && outputItems.all(other.inputItems::contains)
	
	val criticalNumerator: RationalNumber by lazy {
		maxOf(
			inputs.maxOfOrNull(ItemStack::amount) ?: RationalNumber.ONE,
			outputs.maxOfOrNull(ItemStack::amount) ?: RationalNumber.ONE
		)
	}
	val criticalDenominator: RationalNumber by lazy {
		minOf(
			inputs.minOfOrNull(ItemStack::amount) ?: RationalNumber.ONE,
			outputs.minOfOrNull(ItemStack::amount) ?: RationalNumber.ONE
		)
	}
	
	init {
		val ins = mutableListOf<ItemStack>()
		val outs = mutableListOf<ItemStack>()
		generateIO(inputs, outputs, ins, outs)
		this.inputs = ins
		this.outputs = outs
	}
	
	companion object {
		private val typeOrder = listOf(KillRecipe::class, OpenRecipe::class, SellRecipe::class, CraftingRecipe::class)
		private val comparator = compareBy<Recipe>({ typeOrder.indexOf(it::class) }, { it.name })
		
		fun generateIO(inputs: MutableList<ItemStack>, outputs: MutableList<ItemStack>) =
			generateIO(inputs.toList(), outputs.toList(), inputs, outputs)
		
		fun generateIO(
			i: List<ItemStack>,
			o: List<ItemStack>,
			inputs: MutableList<ItemStack>,
			outputs: MutableList<ItemStack>,
		) {
			val ins = i.mergeStacks()
			val outs = o.mergeStacks()
			
			inputs.clear()
			outputs.clear()
			
			val inItems = ins.map(ItemStack::item)
			val outItems = outs.map(ItemStack::item)
			
			ins.filter { it.item !in outItems }.forEach { inputs.add(it) }
			outs.filter { it.item !in inItems }.forEach { outputs.add(it) }
			
			(inItems intersect outItems.toSet()).forEach { item ->
				val inItem = ins.first { it.item == item }
				val outItem = outs.first { it.item == item }
				if (inItem.amount > outItem.amount) inputs.add(ItemStack(item, inItem.amount - outItem.amount))
				else if (inItem.amount < outItem.amount) outputs.add(ItemStack(item, outItem.amount - inItem.amount))
			}
			
			inputs.removeIf { it.amount.aboutZero() }
			outputs.removeIf { it.amount.aboutZero() }
		}
	}
	
	class CraftingRecipe(val baseRecipe: SaverRecipe, replacements: Map<Int, Int>) : Recipe("${baseRecipe.mod}: ${
		baseRecipe.requiredItems.map { item ->
			item.item.toIntOrNull()?.let { itemID ->
				replacements[itemID]?.let {
					ItemStack(it.toString(), item.amount)
				}
			} ?: item
		}
	} → ${baseRecipe.createItem}", baseRecipe.requiredItems.map { item ->
		item.item.toIntOrNull()?.let { itemID ->
			replacements[itemID]?.let { ItemStack(it.toString(), item.amount) }
		} ?: item
	}, listOf(baseRecipe.createItem)
	) {
		override fun compareTo(other: Recipe) = if (other is CraftingRecipe) baseRecipe.compareTo(other.baseRecipe) else super.compareTo(other)
		companion object {
			fun generateRecipes(recipe: SaverRecipe, groups: Map<Int, Group>): Sequence<CraftingRecipe> =
				groups.filterKeys(recipe.acceptedGroups::contains).values.associate { it.iconicItem to it.validItems }
					.permutations().map { CraftingRecipe(recipe, it) }
		}
	}
	
	class OpenRecipe(val item: Item, conditions: CalculatorConditions) : Recipe(
		"Open: ${item.namespacedName}", listOf(ItemStack(item.type.toString())), item.bagItems.getLoot(conditions)
	) {
		override fun compareTo(other: Recipe): Int = if (other is OpenRecipe) item.compareTo(other.item) else super.compareTo(other)
	}
	
	class ExtractRecipe(val item: Item) : Recipe("Extract: ${item.namespacedName}",
		listOf(ItemStack(item.type.toString())),
		item.extractinatorItems.map { (item, amount) ->
			ItemStack(
				item.toString(), of(amount, Item.extractinatorTests)
			)
		}) {
		override fun compareTo(other: Recipe): Int = if (other is ExtractRecipe) item.compareTo(other.item) else super.compareTo(other)
	}
	
	class KillRecipe(val npc: NPC, conditions: CalculatorConditions) :
		Recipe("Kill: ${npc.namespacedName}@${npc.type}", emptyList(), npc.drops.getLoot(conditions).let {
			if (npc.banner > 0) it + ItemStack(npc.banner.toString(), of(1, npc.killsPerBanner)) else it
		}) {
			override fun compareTo(other: Recipe) = if (other is KillRecipe) npc.compareTo(other.npc) else super.compareTo(other)
		}
	
	class SellRecipe(val item: Item) : Recipe(
		"Sell: ${item.namespacedName}", listOf(ItemStack(item.type.toString())), generateCoins(item.value / 5)
	) {
		override fun compareTo(other: Recipe): Int = if (other is SellRecipe) item.compareTo(other.item) else super.compareTo(other)
		companion object {
			private fun generateCoins(value: Int) = listOfNotNull(
				ItemStack("71", value % 100).takeIf { !it.amount.aboutZero() },
				ItemStack("72", (value / 100) % 100).takeIf { !it.amount.aboutZero() },
				ItemStack("73", (value / 10000) % 100).takeIf { !it.amount.aboutZero() },
				ItemStack("74", value / 1000000).takeIf { !it.amount.aboutZero() },
			)
		}
	}
}
