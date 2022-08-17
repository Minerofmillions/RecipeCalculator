package minerofmillions.recipeapp.entities.calculator

data class CalculationKey(val recipes: List<Recipe>, val products: List<ItemStack>, val primitives: List<String>, val isRate: Boolean)
