package minerofmillions.recipeapp.entities.calculator

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import minerofmillions.recipeapp.util.plus
import minerofmillions.recipeapp.util.sumOf
import minerofmillions.recipeapp.util.times
import minerofmillions.recipeapp.util.toString
import org.ojalgo.scalar.RationalNumber
import java.lang.reflect.Type

data class ItemStack(val item: String, val amount: RationalNumber = RationalNumber.ONE) : Comparable<ItemStack> {
	constructor(item: String, amount: Int) : this(item, RationalNumber.of(amount.toLong(), 1L))
	constructor(item: String, amount: Long) : this(item, RationalNumber.of(amount, 1L))
	
	override fun compareTo(other: ItemStack): Int = comparator.compare(this, other)
	
	override fun toString(): String = "${amount.toString(3)} * $item"
	
	operator fun times(n: Int) = ItemStack(item, amount * n)
	operator fun times(n: Long) = ItemStack(item, amount * n)
	operator fun times(n: RationalNumber) = ItemStack(item, amount * n)
	
	object Serializer : JsonDeserializer<ItemStack> {
		override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ItemStack =
			json.asJsonObject.run {
				if (has("item")) ItemStack(
					get("item").asString, context.deserialize<RationalNumber>(get("amount"), RationalNumber::class.java)
				)
				else ItemStack(
					get("type").asString, context.deserialize<RationalNumber>(get("stack"), RationalNumber::class.java)
				)
			}
	}
	
	companion object {
		private val comparator = compareBy<ItemStack>({ it.item.toIntOrNull() }, { it.item }, { it.amount })
	}
}

fun Collection<ItemStack>.mergeStacks() =
	groupBy(ItemStack::item).map { (item, items) -> ItemStack(item, items.sumOf(ItemStack::amount)) }

fun MutableCollection<ItemStack>.addStack(stack: ItemStack) {
	add(firstOrNull { it.item == stack.item }?.let {
		remove(it)
		ItemStack(it.item, it.amount + stack.amount)
	} ?: stack)
}
