package minerofmillions.recipeapp.entities.saver

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import minerofmillions.recipeapp.entities.calculator.CalculatorConditions
import minerofmillions.recipeapp.entities.calculator.ItemStack
import minerofmillions.recipeapp.util.aboutZero
import minerofmillions.recipeapp.util.of
import minerofmillions.recipeapp.util.plus
import org.ojalgo.scalar.RationalNumber
import java.lang.reflect.Type

typealias LootTable = Table<Int, String, RationalNumber>

object LootTableTypeToken : TypeToken<LootTable>()

object LootTableSerializer : JsonDeserializer<LootTable> {
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LootTable =
		HashBasedTable.create<Int, String, RationalNumber>().also { table ->
			json.asJsonObject.entrySet().forEach { (item, conditionalDrops) ->
				conditionalDrops.asJsonObject.entrySet().forEach { (condition, dropRate) ->
					table.put(item.toInt(), condition, of(dropRate.asString))
				}
			}
		}
}

class Loot(val globalLoot: LootTable, val normalLoot: LootTable, val expertLoot: LootTable, val masterLoot: LootTable) {
	val conditions: Set<String>
		get() = globalLoot.columnKeySet() + normalLoot.columnKeySet() + expertLoot.columnKeySet() + masterLoot.columnKeySet()
	val items: Set<Int>
		get() = globalLoot.rowKeySet() + normalLoot.rowKeySet() + expertLoot.rowKeySet() + masterLoot.rowKeySet()
	
	val hasNormalLoot
		get() = !globalLoot.isEmpty || !normalLoot.isEmpty
	val hasExpertLoot
		get() = !expertLoot.isEmpty
	val hasMasterLoot
		get() = !masterLoot.isEmpty
	
	fun getMasterLootTable() = masterLoot + getExpertLootTable()
	fun getExpertLootTable() = expertLoot + getNormalLootTable()
	fun getNormalLootTable() = normalLoot + globalLoot
	
	operator fun contains(item: Int): Boolean = item in items
	operator fun contains(condition: String): Boolean = condition in conditions
	
	val isNotEmpty
		get() = !globalLoot.isEmpty || !normalLoot.isEmpty || !expertLoot.isEmpty || !masterLoot.isEmpty
	
	fun getLoot(conditions: CalculatorConditions): List<ItemStack> = when (conditions.gameMode) {
		CalculatorConditions.GameMode.NORMAL -> getNormalLoot(conditions)
		CalculatorConditions.GameMode.EXPERT -> getExpertLoot(conditions)
		CalculatorConditions.GameMode.MASTER -> getMasterLoot(conditions)
	}
	
	private fun getNormalLoot(conditions: CalculatorConditions): List<ItemStack> = items.mapNotNull { item ->
		conditions.activeConditions.mapNotNull { condition -> getNormalLoot(item, condition) }
			.firstOrNull { !it.aboutZero() }?.let { ItemStack(item.toString(), it) }
	}
	
	private fun getExpertLoot(conditions: CalculatorConditions): List<ItemStack> = items.mapNotNull { item ->
		conditions.activeConditions.mapNotNull { condition -> getExpertLoot(item, condition) }
			.firstOrNull { !it.aboutZero() }?.let { ItemStack(item.toString(), it) }
	}
	
	private fun getMasterLoot(conditions: CalculatorConditions): List<ItemStack> = items.mapNotNull { item ->
		conditions.activeConditions.mapNotNull { condition -> getMasterLoot(item, condition) }
			.firstOrNull { !it.aboutZero() }?.let { ItemStack(item.toString(), it) }
	}
	
	private fun getGlobalLoot(item: Int, condition: String) = globalLoot[item, condition] ?: globalLoot[item, ""]
	private fun getNormalLoot(item: Int, condition: String) =
		normalLoot[item, condition] ?: normalLoot[item, ""] ?: getGlobalLoot(item, condition)
	
	private fun getExpertLoot(item: Int, condition: String) =
		expertLoot[item, condition] ?: expertLoot[item, ""] ?: getNormalLoot(item, condition)
	
	private fun getMasterLoot(item: Int, condition: String) =
		masterLoot[item, condition] ?: masterLoot[item, ""] ?: getExpertLoot(item, condition)
	
	object Serializer : JsonDeserializer<Loot> {
		override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Loot =
			json.asJsonObject.run {
				val globalLoot = deserializeTable(get("globalLoot"))
				val normalLoot = deserializeTable(get("normalLoot"))
				val expertLoot = deserializeTable(get("expertLoot"))
				val masterLoot = deserializeTable(get("masterLoot"))
				
				Loot(globalLoot, normalLoot, expertLoot, masterLoot)
			}
		
		private fun deserializeTable(json: JsonElement): LootTable = json.asJsonObject.run {
			val table = HashBasedTable.create<Int, String, RationalNumber>()
			entrySet().forEach { (itemString, rulesObject) ->
				rulesObject.asJsonObject.let {
					it.entrySet().forEach { (condition, amount) ->
						table.put(itemString.toInt(), condition, RationalNumber.valueOf(amount.asString))
					}
				}
			}
			ImmutableTable.copyOf(table)
		}
	}
}
