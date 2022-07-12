package minerofmillions.recipeapp.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import minerofmillions.recipeapp.data.recipeentities.TerrariaItem.Companion.comparator
import java.lang.reflect.Type

class ItemStack(val item: String, val amount: Rational) : Comparable<ItemStack> {
	override infix fun compareTo(other: ItemStack): Int = comparator.compare(this, other)
	
	override fun toString(): String = "$amount * $item"
	
	object Serializer : JsonDeserializer<ItemStack> {
		override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ItemStack =
			json.asJsonObject.let { obj ->
				if (obj.has("item")) ItemStack(
					obj["item"].asString, context.deserialize(obj["amount"], Rational::class.java) ?: Rational.ONE
				)
				else ItemStack((obj["id"] ?: obj["type"]).asString, Rational.parse(obj["stack"].asString))
			}
	}
	
	companion object {
		private val comparator = compareBy(ItemStack::item, ItemStack::amount)
	}
}

operator fun String.times(amount: Int) = ItemStack(this, Rational.of(amount))
operator fun String.times(amount: Long) = ItemStack(this, Rational.of(amount))
operator fun String.times(amount: Rational): ItemStack = ItemStack(this, amount)

fun Collection<ItemStack>.mergeStacks() = groupBy(ItemStack::item).map { (item, stacks) ->
	ItemStack(item, stacks.map(ItemStack::amount).sum())
}
