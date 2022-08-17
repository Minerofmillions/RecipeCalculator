package minerofmillions.recipeapp.entities.calculator

import minerofmillions.recipeapp.util.plus
import minerofmillions.recipeapp.util.sumOf
import minerofmillions.recipeapp.util.times
import org.ojalgo.scalar.RationalNumber

data class ItemStack(val item: String, val amount: RationalNumber = RationalNumber.ONE) : Comparable<ItemStack> {
	constructor(item: String, amount: Int) : this(item, RationalNumber.of(amount.toLong(), 1L))
	constructor(item: String, amount: Long) : this(item, RationalNumber.of(amount, 1L))
	
	override fun compareTo(other: ItemStack): Int = when {
		item != other.item -> item.compareTo(other.item)
		else -> amount.compareTo(other.amount)
	}
	
	operator fun times(n: Int) = ItemStack(item, amount * n)
	operator fun times(n: Long) = ItemStack(item, amount * n)
	operator fun times(n: RationalNumber) = ItemStack(item, amount * n)
}

fun Collection<ItemStack>.mergeStacks() =
	groupBy(ItemStack::item).map { (item, items) -> ItemStack(item, items.sumOf(ItemStack::amount)) }

fun MutableCollection<ItemStack>.addStack(stack: ItemStack) {
	add(firstOrNull { it.item == stack.item }?.let {
		remove(it)
		ItemStack(it.item, it.amount + stack.amount)
	} ?: stack)
}
